package ru.homerep.orderservice.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import ru.homerep.orderservice.config.HomeRepProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
@Entity
@Slf4j
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String streetName;
    private String buildingNumber;
    private String apartmentNumber;
    private String cityName;
    private Double longitude;
    private Double latitude;

    public Address(String streetName, String buildingNumber, String apartmentNumber, String cityName) {
        this.streetName = streetName;
        this.buildingNumber = buildingNumber;
        this.apartmentNumber = apartmentNumber;
        this.cityName = cityName;
        try{
            GeoPair pair = fillCords();
            longitude = pair.getLng();
            latitude = pair.getLat();
        }
        catch (IOException e) {
            longitude = 0.0;
            latitude = 0.0;
        }
    }

    public GeoPair fillCords() throws IOException {
        HomeRepProperties props = new HomeRepProperties();
        String apiUrl = "https://geocode-maps.yandex.ru/1.x/?apikey=" + props.getYandexgeo() +
                "&geocode=" + cityName + streetName + buildingNumber + "&format=json";

        // Create connection
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Add headers
        connection.setRequestProperty("Cookie",
                "_yasc=e1VP9dyZ+z8dMIhp3aHHh3kOCyQyxZjx6uJPuLCRoSCIf6TB0sR9ImOficVvYL9L; " +
                        "i=LLHPI4/Ho6GerZQWdY8sOKIqspSzpcCNdJ/Qrz6YyPduPPjwKtWg+NvP55cR6M5tc/Oo5VHrhH1U5JrfBxKM596QGUk=; " +
                        "yandexuid=574250821742190749; yashr=928269021742190749");

        // Get response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            GeoPair pair = processGeocodingResponse(jsonResponse);
            log.warn(response.toString());
            return pair;

        } else {
            System.out.println("GET request failed. Response Code: " + responseCode);
        }
        return new GeoPair(0,0);
    }



    private static GeoPair processGeocodingResponse(JSONObject response) {
        try {
            JSONObject geoObjectCollection = response.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection");

            JSONObject firstFeature = geoObjectCollection.getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject");

            JSONObject point = firstFeature.getJSONObject("Point");
            String[] coordinates = point.getString("pos").split(" ");
            return new GeoPair(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GeoPair(0,0);
    }

    @Override
    public String toString() {
        return "Address{" +
                ", id=" + id +
                ", streetName='" + streetName + '\'' +
                ", buildingNumber='" + buildingNumber + '\'' +
                ", apartmentNumber='" + apartmentNumber + '\'' +
                ", cityName='" + cityName + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}


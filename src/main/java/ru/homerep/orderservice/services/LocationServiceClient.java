package ru.homerep.orderservice.services;

import com.google.protobuf.ProtocolStringList;
import locationservice.Location;
import locationservice.LocationServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.homerep.orderservice.models.GeoPair;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class LocationServiceClient {

    @GrpcClient("location-service")
    private LocationServiceGrpc.LocationServiceBlockingStub locationServiceBlockingStub;

    public long[] getUsersByLatLng(double lat, double lng, int users) {
         Location.GetUsersBetweenLongAndLatRequest request = Location.GetUsersBetweenLongAndLatRequest.newBuilder()

                 .setLocation(
                         Location.GeoPair.newBuilder().setLat(lat).setLng(lng).build()
                 )
                 .setMaxUsers(users)
                 .build();
         Location.GetUsersBetweenLongAndLatResponse response = locationServiceBlockingStub.getUsersBetweenLongAndLat(request);
        return response.getUserList().stream()
                .mapToLong(Location.UserResponse::getUserId)
                .toArray();
    }

}
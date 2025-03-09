package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.homerep.models.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findClientByFirstNameAndLastName(String firstName, String lastName);
    Client findClientById(Long id);
    Client findClientByEmail(String email);

}

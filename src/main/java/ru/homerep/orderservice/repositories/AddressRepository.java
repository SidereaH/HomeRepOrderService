package ru.homerep.orderservice.repositories;

import ru.homerep.orderservice.models.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository  extends CrudRepository<Address, Long> {
}

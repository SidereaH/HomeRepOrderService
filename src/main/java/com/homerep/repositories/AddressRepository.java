package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.homerep.models.Address;

public interface AddressRepository  extends CrudRepository<Address, Long> {
}

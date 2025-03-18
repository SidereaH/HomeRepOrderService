package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import com.homerep.models.Address;

public interface AddressRepository  extends CrudRepository<Address, Long> {
}

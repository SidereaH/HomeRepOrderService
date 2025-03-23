package com.homerep.repositories;

import com.homerep.models.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository  extends CrudRepository<Address, Long> {
}

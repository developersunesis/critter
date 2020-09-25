package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.model.CustomerModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerModel, Long> {

}

package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.model.PetModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends CrudRepository<PetModel, Long> {

    List<PetModel> findAllByOwnerId(long id);
}

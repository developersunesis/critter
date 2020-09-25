package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.model.DayOfWeekModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayAvailableRepository extends CrudRepository<DayOfWeekModel, Long> {
}

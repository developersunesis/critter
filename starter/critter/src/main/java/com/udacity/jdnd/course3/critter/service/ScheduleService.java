package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.model.ScheduleModel;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    public ScheduleModel saveSchedule(ScheduleModel scheduleModel) {
        return scheduleRepository.save(scheduleModel);
    }

    public List<ScheduleModel> findAllSchedules() {
        return (List<ScheduleModel>) scheduleRepository.findAll();
    }
}

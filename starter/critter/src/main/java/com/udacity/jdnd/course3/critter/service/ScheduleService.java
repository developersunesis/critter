package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dto.ScheduleDTO;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.EmployeeModel;
import com.udacity.jdnd.course3.critter.model.PetModel;
import com.udacity.jdnd.course3.critter.model.ScheduleModel;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class ScheduleService {

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    @Autowired
    ScheduleRepository scheduleRepository;

    public ScheduleDTO saveSchedule(ScheduleDTO scheduleDTO) {
        ScheduleModel scheduleModel = new ScheduleModel();
        List<PetModel> petModelSet = new ArrayList<>();
        List<EmployeeModel> employeeModels = new ArrayList<>();

        scheduleDTO.getPetIds().forEach(id -> {
            petModelSet.add(petService.findPetById(id));
        });
        scheduleDTO.getEmployeeIds().forEach(id -> {
            employeeModels.add(userService.getEmployeeById(id));
        });

        scheduleModel.addActivities(scheduleDTO.getActivities());
        scheduleModel.setEmployees(employeeModels);
        scheduleModel.setPets(petModelSet);
        scheduleModel.setDate(scheduleDTO.getDate());

        scheduleModel = scheduleRepository.save(scheduleModel);

        BeanUtils.copyProperties(scheduleModel, scheduleDTO);

        Set<EmployeeSkill> activities = new HashSet<>();
        scheduleModel.getActivities().forEach(scheduleActivitiesModel -> {
            activities.add(scheduleActivitiesModel.getSkill());
        });
        scheduleDTO.setActivities(activities);

        return scheduleDTO;
    }

    public List<ScheduleDTO> findAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        scheduleRepository.findAll().forEach(scheduleModel -> {
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            scheduleDTO.setDate(scheduleModel.getDate());

            Set<EmployeeSkill> activities = new HashSet<>();
            scheduleModel.getActivities().forEach(scheduleActivitiesModel -> {
                activities.add(scheduleActivitiesModel.getSkill());
            });
            scheduleDTO.setActivities(activities);

            List<Long> employeeModels = new ArrayList<>();
            scheduleModel.getEmployees().forEach(employeeModel -> {
                employeeModels.add(employeeModel.getId());
            });
            scheduleDTO.setEmployeeIds(employeeModels);

            List<Long> petModels = new ArrayList<>();
            scheduleModel.getPets().forEach(petModel -> {
                petModels.add(petModel.getId());
            });
            scheduleDTO.setPetIds(petModels);

            scheduleDTOS.add(scheduleDTO);
        });

        return scheduleDTOS;
    }

    public List<ScheduleDTO> findSchedulesByEmployee(long employeeId) {
        return findAllSchedules().stream()
                .filter(scheduleDTO -> scheduleDTO.getEmployeeIds().contains(employeeId))
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> findSchedulesByPet(long petId) {
        return findAllSchedules().stream()
                .filter(scheduleDTO -> scheduleDTO.getPetIds().contains(petId))
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> findSchedulesByCustomer(long customerId) {
        return findAllSchedules().stream()
                .filter(scheduleDTO -> {
                    Set<Long> customerIds = new HashSet<>();
                    scheduleDTO.getPetIds().forEach(petId -> {
                        customerIds.add(petService.findPetById(petId).getOwnerId());
                    });

                    return customerIds.contains(customerId);
                })
                .collect(Collectors.toList());
    }
}

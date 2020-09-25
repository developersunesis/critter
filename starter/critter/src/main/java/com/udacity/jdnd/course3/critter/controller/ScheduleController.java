package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.ScheduleDTO;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.EmployeeModel;
import com.udacity.jdnd.course3.critter.model.PetModel;
import com.udacity.jdnd.course3.critter.model.ScheduleModel;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    private ScheduleDTO convertScheduleToDTO(ScheduleModel scheduleModel){
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setDate(scheduleModel.getDate());

        BeanUtils.copyProperties(scheduleModel, scheduleDTO);

        Set<EmployeeSkill> activities = new HashSet<>();
        scheduleModel.getActivities().forEach(scheduleActivitiesModel ->
                activities.add(scheduleActivitiesModel.getSkill()));
        scheduleDTO.setActivities(activities);

        List<Long> employeeModels = new ArrayList<>();
        scheduleModel.getEmployees().forEach(employeeModel ->
                employeeModels.add(employeeModel.getId()));
        scheduleDTO.setEmployeeIds(employeeModels);

        List<Long> petModels = new ArrayList<>();
        scheduleModel.getPets().forEach(petModel ->
                petModels.add(petModel.getId()));
        scheduleDTO.setPetIds(petModels);

        return scheduleDTO;
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleModel scheduleModel = new ScheduleModel();
        List<PetModel> petModels = new ArrayList<>();
        List<EmployeeModel> employeeModels = new ArrayList<>();

        if(scheduleDTO.getPetIds() != null)
        scheduleDTO.getPetIds().forEach(id -> petModels.add(petService.findPetById(id)));

        if(scheduleDTO.getEmployeeIds() != null)
        scheduleDTO.getEmployeeIds().forEach(id -> employeeModels.add(userService.getEmployeeById(id)));

        scheduleModel.addActivities(scheduleDTO.getActivities());
        scheduleModel.setEmployees(employeeModels);
        scheduleModel.setPets(petModels);
        scheduleModel.setDate(scheduleDTO.getDate());

        return convertScheduleToDTO(scheduleService.saveSchedule(scheduleModel));
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        scheduleService.findAllSchedules().forEach(scheduleModel ->
                scheduleDTOS.add(convertScheduleToDTO(scheduleModel)));

        return scheduleDTOS;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return getAllSchedules().stream()
                .filter(scheduleDTO -> scheduleDTO.getPetIds().contains(petId))
                .collect(Collectors.toList());
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return getAllSchedules().stream()
                .filter(scheduleDTO -> scheduleDTO.getEmployeeIds().contains(employeeId))
                .collect(Collectors.toList());
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        return getAllSchedules().stream()
                .filter(scheduleDTO -> {
                    Set<Long> customerIds = new HashSet<>();
                    scheduleDTO.getPetIds().forEach(petId -> customerIds.add(petService.findPetById(petId).getOwnerId()));

                    return customerIds.contains(customerId);
                })
                .collect(Collectors.toList());
    }
}

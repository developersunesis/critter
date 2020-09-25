package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.*;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PetService petService;

    private CustomerDTO convertCustomerToDTO(CustomerModel customerModel){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customerModel, customerDTO);
        return customerDTO;
    }

    private CustomerModel convertCustomerToEntity(CustomerDTO customerDTO){
        CustomerModel customerModel = new CustomerModel();
        BeanUtils.copyProperties(customerDTO, customerModel);
        return customerModel;
    }

    private EmployeeDTO convertEmployeeToDTO(EmployeeModel employeeModel, boolean includeDays){
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employeeModel, employeeDTO);

        Set<EmployeeSkill> skills = new HashSet<>();
        employeeModel.getSkills().forEach(employeeSkillModel -> skills.add(employeeSkillModel.getSkill()));
        employeeDTO.setSkills(skills);

        if(includeDays){
            List<DayOfWeekModel> dayOfWeekModels = employeeModel.getDaysAvailable();
            if(dayOfWeekModels != null) {
                Set<DayOfWeek> dayOfWeeks = new HashSet<>();
                dayOfWeekModels.forEach(dayOfWeekModel ->
                        dayOfWeeks.add(dayOfWeekModel.getDayOfWeek()));
                employeeDTO.setDaysAvailable(dayOfWeeks);
            }
        }

        return employeeDTO;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        CustomerModel customerModel = convertCustomerToEntity(customerDTO);
        customerModel = userService.saveCustomer(customerModel);
        BeanUtils.copyProperties(customerModel, customerDTO);
        return customerDTO;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        userService.findAllCustomers().forEach(
                customerModel -> {
                    CustomerDTO customerDTO = convertCustomerToDTO(customerModel);
                    customerDTO.setPetIds(petService.getCustomerPetIds(customerModel.getId()));
                    customerDTOS.add(customerDTO);
                });
        return customerDTOS;
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        PetModel petModel = petService.findPetById(petId);
        CustomerModel customerModel = userService.getCustomerById(petModel.getOwnerId());
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customerModel, customerDTO);
        customerDTO.setPetIds(petService.getCustomerPetIds(petModel.getOwnerId()));
        return customerDTO;
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeModel employeeModel = new EmployeeModel();
        employeeDTO.getSkills().forEach(employeeSkill ->
                employeeModel.addSkill(EmployeeSkillModel.builder()
                        .skill(employeeSkill).build()));

        if(employeeDTO.getDaysAvailable() != null)
            employeeDTO.getDaysAvailable().forEach(employeeModel::addAvailableDay);

        employeeModel.setName(employeeDTO.getName());

        return convertEmployeeToDTO(userService.saveEmployee(employeeModel), false);
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        EmployeeModel employeeModel = userService.getEmployeeById(employeeId);
        return convertEmployeeToDTO(employeeModel, true);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        EmployeeModel employeeModel = userService.getEmployeeById(employeeId);
        daysAvailable.forEach(employeeModel::addAvailableDay);
        userService.updateEmployee(employeeModel);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();

        userService.findEmployeesForService(employeeDTO).forEach(employeeModel ->
                employeeDTOS.add(convertEmployeeToDTO(employeeModel, true)));

        return employeeDTOS;
    }

}

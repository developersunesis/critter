package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.CustomerModel;
import com.udacity.jdnd.course3.critter.model.DayOfWeekModel;
import com.udacity.jdnd.course3.critter.model.EmployeeModel;
import com.udacity.jdnd.course3.critter.model.PetModel;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
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

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        CustomerModel customerModel = userService.saveCustomer(customerDTO);
        BeanUtils.copyProperties(customerModel, customerDTO);
        return customerDTO;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        return userService.findAllCustomers();
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
        return userService.saveEmployee(employeeDTO);
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        return userService.getEmployeeDTOById(employeeId);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        EmployeeModel employeeModel = userService.getEmployeeById(employeeId);
        daysAvailable.forEach(employeeModel::addAvailableDay);
        userService.updateEmployee(employeeModel);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        return userService.findEmployeesForService(employeeDTO);
    }

}

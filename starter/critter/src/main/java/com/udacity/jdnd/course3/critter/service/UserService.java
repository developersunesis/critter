package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.CustomerModel;
import com.udacity.jdnd.course3.critter.model.DayOfWeekModel;
import com.udacity.jdnd.course3.critter.model.EmployeeModel;
import com.udacity.jdnd.course3.critter.model.EmployeeSkillModel;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.DayAvailableRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class UserService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DayAvailableRepository dayAvailableRepository;

    @Autowired
    PetService petService;

    public CustomerModel saveCustomer(CustomerDTO customerDTO){
        CustomerModel customerModel = new CustomerModel();
        BeanUtils.copyProperties(customerDTO, customerModel);
        customerModel.setId(null);
        return customerRepository.save(customerModel);
    }

    public CustomerModel getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer doesn't exist"));
    }

    public List<CustomerDTO> findAllCustomers(){
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        customerRepository.findAll().forEach(
                customerModel -> {
                    CustomerDTO customerDTO = new CustomerDTO();
                    BeanUtils.copyProperties(customerModel, customerDTO);
                    customerDTO.setPetIds(petService.getCustomerPetIds(customerModel.getId()));
                    customerDTOS.add(customerDTO);
                });
        return customerDTOS;
    }

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setName(employeeDTO.getName());
        employeeDTO.getSkills().forEach(employeeSkill ->
                employeeModel.addSkill(EmployeeSkillModel.builder()
                .skill(employeeSkill).build()));

        if(employeeDTO.getDaysAvailable() != null)
            employeeDTO.getDaysAvailable().forEach(employeeModel::addAvailableDay);

        return convertToEmployeeDTO(employeeRepository.save(employeeModel), employeeDTO);
    }

    public EmployeeModel getEmployeeById(long employeeId){
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException("Employee doesn't exist"));
    }

    public EmployeeDTO getEmployeeDTOById(long employeeId) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        EmployeeModel employeeModel = getEmployeeById(employeeId);
        return convertToEmployeeDTOIncludeDays(employeeModel, employeeDTO);
    }

    public void updateEmployee(EmployeeModel employeeModel) {
        employeeRepository.save(employeeModel);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        employeeRepository.findAll().forEach(employeeModel -> {
            boolean employeeIsAvailable = employeeModel.getDaysAvailable()
                    .stream().anyMatch(dayOfWeekModel ->
                            dayOfWeekModel.getDayOfWeek()
                                    .equals(employeeDTO.getDate().getDayOfWeek()));

            Set<EmployeeSkill> employeeSkills = new HashSet<>();
            employeeModel.getSkills().forEach(employeeSkillModel ->
                    employeeSkills.add(employeeSkillModel.getSkill()));
            boolean employeeHasSkill = employeeSkills.containsAll(employeeDTO.getSkills());

            if(employeeHasSkill && employeeIsAvailable){
                EmployeeDTO employeeDTO1 = new EmployeeDTO();
                employeeDTOS.add(convertToEmployeeDTOIncludeDays(employeeModel, employeeDTO1));
            }
        });

        return employeeDTOS;
    }

    /*
     * Doesn't include employee's available days in DTO, this function is used when an EmployeeModel is first created
     */
    public static EmployeeDTO convertToEmployeeDTO(EmployeeModel employeeModel, EmployeeDTO employeeDTO){
        BeanUtils.copyProperties(employeeModel, employeeDTO);

        Set<EmployeeSkill> skills = new HashSet<>();
        employeeModel.getSkills().forEach(employeeSkillModel -> skills.add(employeeSkillModel.getSkill()));
        employeeDTO.setSkills(skills);

        return employeeDTO;
    }

    /*
    * Include employee's available days in DTO, this function is used when an EmployeeModel is fetched
     */
    public static EmployeeDTO convertToEmployeeDTOIncludeDays(EmployeeModel employeeModel, EmployeeDTO employeeDTO){
        convertToEmployeeDTO(employeeModel, employeeDTO);

        List<DayOfWeekModel> dayOfWeekModels = employeeModel.getDaysAvailable();
        if(dayOfWeekModels != null) {
            Set<DayOfWeek> dayOfWeeks = new HashSet<>();
            dayOfWeekModels.forEach(dayOfWeekModel ->
                    dayOfWeeks.add(dayOfWeekModel.getDayOfWeek()));
            employeeDTO.setDaysAvailable(dayOfWeeks);
        }

        return employeeDTO;
    }
}

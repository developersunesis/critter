package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.model.CustomerModel;
import com.udacity.jdnd.course3.critter.model.EmployeeModel;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.DayAvailableRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
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

    public CustomerModel saveCustomer(CustomerModel customerModel){
        customerModel.setId(null);
        return customerRepository.save(customerModel);
    }

    public CustomerModel getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer doesn't exist"));
    }

    public List<CustomerModel> findAllCustomers(){
        return (List<CustomerModel>) customerRepository.findAll();
    }

    public EmployeeModel saveEmployee(EmployeeModel employeeModel) {
        employeeModel.setId(null);
        return employeeRepository.save(employeeModel);
    }

    public EmployeeModel getEmployeeById(long employeeId){
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException("Employee doesn't exist"));
    }

    public void updateEmployee(EmployeeModel employeeModel) {
        employeeRepository.save(employeeModel);
    }

    public List<EmployeeModel> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<EmployeeModel> employeeModels = new ArrayList<>();
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
                employeeModels.add(employeeModel);
            }
        });

        return employeeModels;
    }
}

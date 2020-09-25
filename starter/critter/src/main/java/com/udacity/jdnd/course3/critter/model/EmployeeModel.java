package com.udacity.jdnd.course3.critter.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a sub class of users that will be making use of this
 * system (employees)
 */
@Entity
@Setter
@Getter
public class EmployeeModel extends UserModel {

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<EmployeeSkillModel> skills;

    @OneToMany(cascade = CascadeType.ALL)
    private List<DayOfWeekModel> daysAvailable = new ArrayList<>();

    @ManyToMany(mappedBy = "employees")
    private Set<ScheduleModel> schedules;

    public void addSkill(EmployeeSkillModel employeeSkillModel){
        if(skills == null) skills = new HashSet<>();

        skills.add(employeeSkillModel);
        employeeSkillModel.setEmployee(this);
    }

    public void addAvailableDay(DayOfWeek dayOfWeek){
        if(daysAvailable == null) daysAvailable = new ArrayList<>();

        DayOfWeekModel dayOfWeekModel = new DayOfWeekModel();
        dayOfWeekModel.setDayOfWeek(dayOfWeek);
        daysAvailable.add(dayOfWeekModel);
        dayOfWeekModel.setEmployee(this);
    }
}

package com.udacity.jdnd.course3.critter.model;

import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
public class ScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<EmployeeModel> employees = new ArrayList<>();

    @ManyToMany
    private List<PetModel> pets = new ArrayList<>();

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private Set<ScheduleActivitiesModel> activities;

    private LocalDate date;

    public void addActivities(Set<EmployeeSkill> activities) {
        if(this.activities == null) this.activities = new HashSet<>();

        activities.forEach(activity -> {
            ScheduleActivitiesModel scheduleActivitiesModel = new ScheduleActivitiesModel();
            scheduleActivitiesModel.setSchedule(this);
            scheduleActivitiesModel.setSkill(activity);
            this.activities.add(scheduleActivitiesModel);
        });
    }
}

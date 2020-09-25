package com.udacity.jdnd.course3.critter.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;

@Setter
@Getter
@Entity
public class DayOfWeekModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @ManyToOne
    private EmployeeModel employee;
}

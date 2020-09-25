package com.udacity.jdnd.course3.critter.model;

import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkillModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EmployeeSkill skill;

    @ManyToOne
    private EmployeeModel employee;
}

package com.udacity.jdnd.course3.critter.model;

import com.udacity.jdnd.course3.critter.enums.PetType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Entity
public class PetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PetType type;

    private long ownerId;

    private LocalDate birthDate;

    private String notes;

    @ManyToMany(mappedBy = "pets")
    private Set<ScheduleModel> schedules;
}

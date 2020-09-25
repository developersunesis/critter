package com.udacity.jdnd.course3.critter.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * This class is a sub class of users that will be making use of this
 * system (customers)
 */
@Entity
@Setter
@Getter
public class CustomerModel extends UserModel {

    private String notes;

}

package org.example.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class LongestQuestion {

    @Id
    private Long id;
    private String question;

}

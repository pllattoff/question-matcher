package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "questions", schema = "question_matcher")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "questions_id_seq")
    private Integer id;

    @Column(name = "question")
    private String question;

}

package com.ITJobsBackend.domain;


import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "employers")
public class Employer implements Serializable {

    private static final long serialVersionUID = 1L;

    // Como id viene de la tabla user, podemos usar @MapsId si hacemos relación uno a uno
    @Id
    private Integer id;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(length = 50)
    private String industry;

    @Column(length = 100)
    private String website;

    @Column(length = 100)
    private String location;

    @Column(name = "contact_person", length = 50)
    private String contactPerson;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Relación con User
    @OneToOne
    @MapsId  // Esto indica que el id de Employer es el mismo que el id del User
    @JoinColumn(name = "id")
    private User user;
}

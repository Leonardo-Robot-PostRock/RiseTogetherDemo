package com.ITJobsBackend.domain;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Roles implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@Column(length = 50, unique = true)
	private String name;

	@Column
	private String description;

	@ManyToMany(mappedBy = "roles")
	private Set<User> users;

}

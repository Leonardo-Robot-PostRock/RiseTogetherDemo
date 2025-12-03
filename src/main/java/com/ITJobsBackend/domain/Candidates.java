package com.ITJobsBackend.domain;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "candidates")
public class Candidates implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private User user;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	private String resume;

	private String profileSummary;

	private String location;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	// habilidades
	@ManyToMany
	@JoinTable(name = "candidate_skills", joinColumns = @JoinColumn(name = "candidate_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
	private Set<Skill> skills;

}

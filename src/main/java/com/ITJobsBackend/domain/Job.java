package com.ITJobsBackend.domain;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.ITJobsBackend.enums.EmploymentType;
import com.ITJobsBackend.enums.JobStatus;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "jobs")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private String location;

    private String salaryRange;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @CreationTimestamp
    private Timestamp postedAt;

    private Timestamp expiresAt;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToMany
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;
}

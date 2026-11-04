package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "worker_skills",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_worker_category",
           columnNames = {"worker_id", "category_id"}
       ))
public class WorkerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "worker_skill_id")
    private Long workerSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @Column(name = "skill_description", length = 255)
    private String skillDescription;

    public WorkerSkill() {
    }

	public Long getWorkerSkillId() {
		return workerSkillId;
	}

	public void setWorkerSkillId(Long workerSkillId) {
		this.workerSkillId = workerSkillId;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public ServiceCategory getCategory() {
		return category;
	}

	public void setCategory(ServiceCategory category) {
		this.category = category;
	}

	public String getSkillDescription() {
		return skillDescription;
	}

	public void setSkillDescription(String skillDescription) {
		this.skillDescription = skillDescription;
	}

    // Generate getters and setters using STS
    
}
package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "worker_service_areas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_worker_service_pincode",
        columnNames = {"worker_id", "pincode"}
    )
)
public class WorkerServiceArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_area_id")
    private Long serviceAreaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    public WorkerServiceArea() {
    }

	public Long getServiceAreaId() {
		return serviceAreaId;
	}

	public void setServiceAreaId(Long serviceAreaId) {
		this.serviceAreaId = serviceAreaId;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

    // Generate getters and setters using STS
    
}
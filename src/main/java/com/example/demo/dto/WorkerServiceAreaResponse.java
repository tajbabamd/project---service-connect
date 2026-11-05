package com.example.demo.dto;

public class WorkerServiceAreaResponse {

    private Long serviceAreaId;
    private String city;
    private String state;
    private String pincode;

    public WorkerServiceAreaResponse(
            Long serviceAreaId,
            String city,
            String state,
            String pincode) {

        this.serviceAreaId = serviceAreaId;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public Long getServiceAreaId() {
        return serviceAreaId;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }
}
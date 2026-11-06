package com.example.demo.dto;

public class CustomerAddressResponse {

    private Long addressId;
    private String addressLine;
    private String area;
    private String city;
    private String state;
    private String pincode;
    private Boolean isDefault;

    public CustomerAddressResponse(
            Long addressId,
            String addressLine,
            String area,
            String city,
            String state,
            String pincode,
            Boolean isDefault) {

        this.addressId = addressId;
        this.addressLine = addressLine;
        this.area = area;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.isDefault = isDefault;
    }

    public Long getAddressId() {
        return addressId;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getArea() {
        return area;
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

    public Boolean getIsDefault() {
        return isDefault;
    }
}
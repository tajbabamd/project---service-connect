package com.example.demo.dto;

public class AddWorkerSkillRequest {

    private Long categoryId;
    private String skillDescription;

    public AddWorkerSkillRequest() {
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSkillDescription() {
        return skillDescription;
    }

    public void setSkillDescription(String skillDescription) {
        this.skillDescription = skillDescription;
    }
}
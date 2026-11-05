package com.example.demo.dto;

public class WorkerSkillResponse {

    private Long workerSkillId;
    private Long categoryId;
    private String categoryName;
    private String skillDescription;

    public WorkerSkillResponse(
            Long workerSkillId,
            Long categoryId,
            String categoryName,
            String skillDescription) {

        this.workerSkillId = workerSkillId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.skillDescription = skillDescription;
    }

    public Long getWorkerSkillId() {
        return workerSkillId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSkillDescription() {
        return skillDescription;
    }
}
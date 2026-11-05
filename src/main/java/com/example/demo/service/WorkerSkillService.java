package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AddWorkerSkillRequest;
import com.example.demo.dto.WorkerSkillResponse;

public interface WorkerSkillService {

    WorkerSkillResponse addSkill(
            Long userId,
            AddWorkerSkillRequest request);

    List<WorkerSkillResponse> getMySkills(Long userId);

    String removeSkill(Long userId, Long skillId);
}
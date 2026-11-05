package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddWorkerSkillRequest;
import com.example.demo.dto.WorkerSkillResponse;

import com.example.demo.entity.ServiceCategory;
import com.example.demo.entity.Worker;
import com.example.demo.entity.WorkerSkill;

import com.example.demo.repository.ServiceCategoryRepository;
import com.example.demo.repository.WorkerRepository;
import com.example.demo.repository.WorkerSkillRepository;

import com.example.demo.service.WorkerSkillService;

@Service
public class WorkerSkillServiceImpl implements WorkerSkillService {

    private final WorkerRepository workerRepository;
    private final WorkerSkillRepository workerSkillRepository;
    private final ServiceCategoryRepository categoryRepository;

    public WorkerSkillServiceImpl(
            WorkerRepository workerRepository,
            WorkerSkillRepository workerSkillRepository,
            ServiceCategoryRepository categoryRepository) {

        this.workerRepository = workerRepository;
        this.workerSkillRepository = workerSkillRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public WorkerSkillResponse addSkill(
            Long userId,
            AddWorkerSkillRequest request) {

        if (request == null || request.getCategoryId() == null) {
            throw new IllegalArgumentException(
                    "Category ID is required");
        }

        // Find worker using authenticated user ID
        Worker worker = getWorker(userId);

        // Find service category
        ServiceCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                    new NoSuchElementException(
                        "Service category not found"));

        // Only active service categories are allowed
        boolean active = categoryRepository.findByIsActiveTrue()
                .stream()
                .anyMatch(c ->
                    c.getCategoryId().equals(category.getCategoryId()));

        if (!active) {
            throw new IllegalArgumentException(
                    "Service category is inactive");
        }

        // Prevent duplicate skills
        boolean exists = workerSkillRepository
                .existsByWorker_WorkerIdAndCategory_CategoryId(
                    worker.getWorkerId(),
                    category.getCategoryId());

        if (exists) {
            throw new IllegalStateException(
                    "Worker already has this skill");
        }

        // Create skill
        WorkerSkill skill = new WorkerSkill();

        skill.setWorker(worker);
        skill.setCategory(category);
        skill.setSkillDescription(
                request.getSkillDescription() == null
                    ? null
                    : request.getSkillDescription().trim()
        );

        WorkerSkill savedSkill = workerSkillRepository.save(skill);

        return toResponse(savedSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerSkillResponse> getMySkills(Long userId) {

        Worker worker = getWorker(userId);

        List<WorkerSkill> skills =
                workerSkillRepository.findByWorker_WorkerId(
                        worker.getWorkerId());

        List<WorkerSkillResponse> response = new ArrayList<>();

        for (WorkerSkill skill : skills) {
            response.add(toResponse(skill));
        }

        return response;
    }

    @Override
    @Transactional
    public String removeSkill(Long userId, Long skillId) {

        Worker worker = getWorker(userId);

        WorkerSkill skill = workerSkillRepository
                .findById(skillId)
                .orElseThrow(() ->
                    new NoSuchElementException("Skill not found"));

        // Ensure the skill belongs to this worker
        if (!skill.getWorker().getWorkerId()
                .equals(worker.getWorkerId())) {

            throw new NoSuchElementException("Skill not found");
        }

        workerSkillRepository.delete(skill);

        return "Worker skill removed successfully";
    }

    private Worker getWorker(Long userId) {

        return workerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                    new NoSuchElementException(
                        "Worker profile not found"));
    }

    private WorkerSkillResponse toResponse(WorkerSkill skill) {

        return new WorkerSkillResponse(
                skill.getWorkerSkillId(),
                skill.getCategory().getCategoryId(),
                skill.getCategory().getCategoryName(),
                skill.getSkillDescription()
        );
    }
}
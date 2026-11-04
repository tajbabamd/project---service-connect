package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkerSkill;

public interface WorkerSkillRepository
        extends JpaRepository<WorkerSkill, Long> {

    List<WorkerSkill> findByWorker_WorkerId(Long workerId);

    List<WorkerSkill> findByCategory_CategoryId(Long categoryId);

    boolean existsByWorker_WorkerIdAndCategory_CategoryId(
            Long workerId,
            Long categoryId
    );
}
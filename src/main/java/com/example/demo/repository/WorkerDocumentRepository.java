package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkerDocument;
import com.example.demo.entity.DocumentStatus;

public interface WorkerDocumentRepository
        extends JpaRepository<WorkerDocument, Long> {

    List<WorkerDocument> findByWorker_WorkerId(
            Long workerId
    );

    List<WorkerDocument> findByDocumentStatus(
            DocumentStatus documentStatus
    );

    List<WorkerDocument> findByWorker_WorkerIdAndDocumentStatus(
            Long workerId,
            DocumentStatus documentStatus
    );

}
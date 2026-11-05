package com.example.demo.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.WorkerDocumentResponse;
import com.example.demo.entity.DocumentType;

public interface WorkerDocumentService {

    WorkerDocumentResponse uploadDocument(
            Long userId,
            DocumentType documentType,
            String aadhaarLastFour,
            MultipartFile file);

    List<WorkerDocumentResponse> getMyDocuments(Long userId);

    List<WorkerDocumentResponse> getPendingDocuments();

    Path getDocumentPath(Long documentId);

    String verifyDocument(Long documentId);

    String rejectDocument(Long documentId);
}
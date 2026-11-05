package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.WorkerDocumentResponse;
import com.example.demo.entity.DocumentType;
import com.example.demo.entity.User;
import com.example.demo.service.WorkerDocumentService;

@RestController
@RequestMapping("/api/workers/documents")
public class WorkerDocumentController {

    private final WorkerDocumentService documentService;

    public WorkerDocumentController(
            WorkerDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WorkerDocumentResponse> upload(
            @AuthenticationPrincipal User user,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "aadhaarLastFour", required = false)
                    String aadhaarLastFour,
            @RequestParam("file") MultipartFile file) {

        WorkerDocumentResponse result =
                documentService.uploadDocument(
                        user.getUserId(),
                        documentType,
                        aadhaarLastFour,
                        file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping
    public List<WorkerDocumentResponse> myDocuments(
            @AuthenticationPrincipal User user) {

        return documentService.getMyDocuments(user.getUserId());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(
            IllegalArgumentException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(
            NoSuchElementException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
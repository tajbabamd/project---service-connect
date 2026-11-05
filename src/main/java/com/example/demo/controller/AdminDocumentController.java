package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.WorkerDocumentResponse;
import com.example.demo.service.WorkerDocumentService;

@RestController
@RequestMapping("/api/admin/documents")
public class AdminDocumentController {

    private final WorkerDocumentService documentService;

    public AdminDocumentController(
            WorkerDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/pending")
    public List<WorkerDocumentResponse> pendingDocuments() {
        return documentService.getPendingDocuments();
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId) throws IOException {

        Path path = documentService.getDocumentPath(documentId);
        Resource resource = new UrlResource(path.toUri());

        String filename = "document-" + documentId
                + path.getFileName().toString().substring(
                        path.getFileName().toString().lastIndexOf("."));

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(path))
                .body(resource);
    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<String> verify(
            @PathVariable Long documentId) {

        return ResponseEntity.ok(
                documentService.verifyDocument(documentId));
    }

    @PutMapping("/{documentId}/reject")
    public ResponseEntity<String> reject(
            @PathVariable Long documentId) {

        return ResponseEntity.ok(
                documentService.rejectDocument(documentId));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(
            NoSuchElementException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> conflict(
            IllegalStateException e) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}
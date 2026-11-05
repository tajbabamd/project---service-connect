package com.example.demo.serviceimpl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.WorkerDocumentResponse;
import com.example.demo.entity.DocumentStatus;
import com.example.demo.entity.DocumentType;
import com.example.demo.entity.Worker;
import com.example.demo.entity.WorkerDocument;
import com.example.demo.repository.WorkerDocumentRepository;
import com.example.demo.repository.WorkerRepository;
import com.example.demo.service.WorkerDocumentService;

@Service
public class WorkerDocumentServiceImpl
        implements WorkerDocumentService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final WorkerRepository workerRepository;
    private final WorkerDocumentRepository documentRepository;
    private final Path storageDirectory;

    public WorkerDocumentServiceImpl(
            WorkerRepository workerRepository,
            WorkerDocumentRepository documentRepository,
            @Value("${serviceconnect.documents.storage-dir}")
            String storagePath) {

        this.workerRepository = workerRepository;
        this.documentRepository = documentRepository;
        this.storageDirectory = Path.of(storagePath)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    @Transactional
    public WorkerDocumentResponse uploadDocument(
            Long userId,
            DocumentType documentType,
            String aadhaarLastFour,
            MultipartFile file) {

        if (documentType == null) {
            throw new IllegalArgumentException(
                    "Document type is required");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a document");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size must not exceed 5 MB");
        }

        String lastFour = aadhaarLastFour == null
                || aadhaarLastFour.isBlank()
                ? null
                : aadhaarLastFour.trim();

        if (lastFour != null && !lastFour.matches("[0-9]{4}")) {
            throw new IllegalArgumentException(
                    "Aadhaar last four digits must contain exactly 4 digits");
        }

        if (documentType != DocumentType.AADHAAR
                && lastFour != null) {
            throw new IllegalArgumentException(
                    "Aadhaar digits are allowed only for Aadhaar documents");
        }

        Worker worker = workerRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Worker profile not found"));

        byte[] content;

        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Unable to read uploaded file", e);
        }

        // Determine file type using its contents
        String extension = detectExtension(content);

        // Generate our own filename
        String storedName = UUID.randomUUID() + extension;

        Path destination = storageDirectory.resolve(storedName);

        try {
            Files.createDirectories(storageDirectory);

            // Restrict directory access on macOS/Linux
            try {
                Files.setPosixFilePermissions(
                        storageDirectory,
                        PosixFilePermissions.fromString("rwx------"));
            } catch (UnsupportedOperationException ignored) {
                // File systems without POSIX permissions
            }

            Files.write(
                    destination,
                    content,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);

            try {
                Files.setPosixFilePermissions(
                        destination,
                        PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException ignored) {
                // File systems without POSIX permissions
            }

            WorkerDocument document = new WorkerDocument();

            document.setWorker(worker);
            document.setDocumentType(documentType);
            document.setDocumentReference(storedName);
            document.setAadhaarLastFour(lastFour);
            document.setDocumentStatus(DocumentStatus.PENDING);

            WorkerDocument saved =
                    documentRepository.save(document);

            return toResponse(saved);

        } catch (IOException e) {

            deleteQuietly(destination);

            throw new UncheckedIOException(
                    "Unable to store document", e);

        } catch (RuntimeException e) {

            deleteQuietly(destination);

            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerDocumentResponse> getMyDocuments(
            Long userId) {

        Worker worker = workerRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Worker profile not found"));

        List<WorkerDocument> documents =
                documentRepository.findByWorker_WorkerId(
                        worker.getWorkerId());

        List<WorkerDocumentResponse> response =
                new ArrayList<>();

        for (WorkerDocument document : documents) {
            response.add(toResponse(document));
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerDocumentResponse> getPendingDocuments() {

        List<WorkerDocument> documents =
                documentRepository.findByDocumentStatus(
                        DocumentStatus.PENDING);

        List<WorkerDocumentResponse> response =
                new ArrayList<>();

        for (WorkerDocument document : documents) {
            response.add(toResponse(document));
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Path getDocumentPath(Long documentId) {

        WorkerDocument document = documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Document not found"));

        Path path = storageDirectory
                .resolve(document.getDocumentReference())
                .normalize();

        if (!path.startsWith(storageDirectory)
                || !Files.isRegularFile(path)) {
            throw new NoSuchElementException(
                    "Document file not found");
        }

        return path;
    }

    @Override
    @Transactional
    public String verifyDocument(Long documentId) {

        WorkerDocument document = getPendingDocument(documentId);

        document.setDocumentStatus(DocumentStatus.VERIFIED);

        documentRepository.save(document);

        return "Document verified successfully";
    }

    @Override
    @Transactional
    public String rejectDocument(Long documentId) {

        WorkerDocument document = getPendingDocument(documentId);

        document.setDocumentStatus(DocumentStatus.REJECTED);

        documentRepository.save(document);

        return "Document rejected successfully";
    }

    private WorkerDocument getPendingDocument(Long documentId) {

        WorkerDocument document = documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Document not found"));

        if (document.getDocumentStatus()
                != DocumentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending documents can be reviewed");
        }

        return document;
    }

    private WorkerDocumentResponse toResponse(
            WorkerDocument document) {

        return new WorkerDocumentResponse(
                document.getDocumentId(),
                document.getWorker().getWorkerId(),
                document.getDocumentType().name(),
                document.getDocumentStatus().name(),
                document.getAadhaarLastFour()
        );
    }

    private String detectExtension(byte[] content) {

        // PDF signature: %PDF-
        if (content.length >= 5
                && content[0] == 0x25
                && content[1] == 0x50
                && content[2] == 0x44
                && content[3] == 0x46
                && content[4] == 0x2D) {
            return ".pdf";
        }

        // JPEG signature
        if (content.length >= 3
                && (content[0] & 0xFF) == 0xFF
                && (content[1] & 0xFF) == 0xD8
                && (content[2] & 0xFF) == 0xFF) {
            return ".jpg";
        }

        // PNG signature
        if (content.length >= 8
                && (content[0] & 0xFF) == 0x89
                && content[1] == 0x50
                && content[2] == 0x4E
                && content[3] == 0x47
                && content[4] == 0x0D
                && content[5] == 0x0A
                && content[6] == 0x1A
                && content[7] == 0x0A) {
            return ".png";
        }

        throw new IllegalArgumentException(
                "Only PDF, JPEG and PNG documents are supported");
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Record cleanup failures in production logging
        }
    }
}
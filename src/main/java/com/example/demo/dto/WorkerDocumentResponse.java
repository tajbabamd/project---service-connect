package com.example.demo.dto;

public class WorkerDocumentResponse {

    private Long documentId;
    private Long workerId;
    private String documentType;
    private String documentStatus;
    private String aadhaarLastFour;

    public WorkerDocumentResponse(
            Long documentId,
            Long workerId,
            String documentType,
            String documentStatus,
            String aadhaarLastFour) {

        this.documentId = documentId;
        this.workerId = workerId;
        this.documentType = documentType;
        this.documentStatus = documentStatus;
        this.aadhaarLastFour = aadhaarLastFour;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public String getAadhaarLastFour() {
        return aadhaarLastFour;
    }
}
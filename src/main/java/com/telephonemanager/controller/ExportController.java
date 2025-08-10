package com.telephonemanager.controller;

import com.telephonemanager.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    public ResponseEntity<InputStreamResource> exportUsers(@RequestParam(defaultValue = "csv") String format) {
        ByteArrayInputStream stream = exportService.exportUsers(format);
        String filename = "users." + ("excel".equalsIgnoreCase(format) ? "xlsx" : "csv");
        String contentType = "excel".equalsIgnoreCase(format) ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/phones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    public ResponseEntity<InputStreamResource> exportPhones(@RequestParam(defaultValue = "csv") String format) {
        ByteArrayInputStream stream = exportService.exportPhones(format);
        String filename = "phones." + ("excel".equalsIgnoreCase(format) ? "xlsx" : "csv");
        String contentType = "excel".equalsIgnoreCase(format) ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/simcards")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    public ResponseEntity<InputStreamResource> exportSimCards(@RequestParam(defaultValue = "csv") String format) {
        ByteArrayInputStream stream = exportService.exportSimCards(format);
        String filename = "simcards." + ("excel".equalsIgnoreCase(format) ? "xlsx" : "csv");
        String contentType = "excel".equalsIgnoreCase(format) ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/attributions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    public ResponseEntity<InputStreamResource> exportAttributions(@RequestParam(defaultValue = "csv") String format) {
        ByteArrayInputStream stream = exportService.exportAttributions(format);
        String filename = "attributions." + ("excel".equalsIgnoreCase(format) ? "xlsx" : "csv");
        String contentType = "excel".equalsIgnoreCase(format) ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    public ResponseEntity<InputStreamResource> exportRequests(@RequestParam(defaultValue = "csv") String format) {
        ByteArrayInputStream stream = exportService.exportRequests(format);
        String filename = "requests." + ("excel".equalsIgnoreCase(format) ? "xlsx" : "csv");
        String contentType = "excel".equalsIgnoreCase(format) ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportAuditLogs() {
        ByteArrayInputStream stream = exportService.exportAuditLogsToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit_logs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(stream));
    }
} 
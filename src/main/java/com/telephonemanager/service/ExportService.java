package com.telephonemanager.service;

import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ExportService {
    public ByteArrayInputStream exportUsersToCsv() {
        String csv = "id,name,email,role,department,status\n";
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
    public ByteArrayInputStream exportPhonesToCsv() {
        String csv = "id,brand,model,imei,status,assigned_to\n";
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
    public ByteArrayInputStream exportSimCardsToCsv() {
        String csv = "id,number,operator,status,assigned_to\n";
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
    public ByteArrayInputStream exportAttributionsToCsv() {
        String csv = "id,user_id,phone_id,simcard_id,start_date,end_date,status\n";
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
    public ByteArrayInputStream exportRequestsToCsv() {
        String csv = "id,user_id,type,status,created_at,processed_by\n";
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
    public ByteArrayInputStream exportAuditLogsToCsv() {
        String csv = "id,user_id,action,entity,entity_id,timestamp,details\n";
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
} 
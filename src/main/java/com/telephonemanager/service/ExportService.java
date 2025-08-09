package com.telephonemanager.service;

import com.telephonemanager.dto.UserDto;
import com.telephonemanager.repository.UserRepository;
import com.telephonemanager.utils.CsvExportUtil;
import com.telephonemanager.utils.ExcelExportUtil;
import com.telephonemanager.dto.PhoneDto;
import com.telephonemanager.repository.PhoneRepository;
import com.telephonemanager.dto.SimCardDto;
import com.telephonemanager.repository.SimCardRepository;
import com.telephonemanager.dto.AttributionDto;
import com.telephonemanager.repository.AttributionRepository;
import com.telephonemanager.dto.RequestDto;
import com.telephonemanager.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private SimCardRepository simCardRepository;
    @Autowired
    private AttributionRepository attributionRepository;
    @Autowired
    private RequestRepository requestRepository;

    public ByteArrayInputStream exportUsers(String format) {
        List<UserDto> users = userRepository.findAll().stream().map(UserDto::new).toList();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Name", "Email", "Role", "Department", "Position", "Status", "Join Date", "Phone", "Address", "Manager"});
        for (UserDto user : users) {
            rows.add(new String[]{
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : "",
                user.getDepartment(),
                user.getPosition() != null ? user.getPosition() : "",
                user.getStatus() != null ? user.getStatus().name() : "",
                user.getJoinDate() != null ? user.getJoinDate().toString() : "",
                user.getPhone() != null ? user.getPhone() : "",
                user.getAddress() != null ? user.getAddress() : "",
                user.getManager() != null ? user.getManager() : ""
            });
        }
        if ("excel".equalsIgnoreCase(format)) {
            return ExcelExportUtil.writeToExcel(rows);
        } else {
            return CsvExportUtil.writeToCsv(rows);
        }
    }
    public ByteArrayInputStream exportPhones(String format) {
        List<PhoneDto> phones = phoneRepository.findAll().stream().map(PhoneDto::new).toList();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Brand", "Model", "IMEI", "Status", "Assigned To ID", "Assigned To Name", "Assigned Date", "Notes"});
        for (PhoneDto phone : phones) {
            rows.add(new String[]{
                String.valueOf(phone.getId()),
                phone.getBrand(),
                phone.getModel(),
                phone.getImei(),
                phone.getStatus() != null ? phone.getStatus().name() : "",
                phone.getAssignedToId() != null ? phone.getAssignedToId().toString() : "",
                phone.getAssignedToName() != null ? phone.getAssignedToName() : "",
                phone.getAssignedDate() != null ? phone.getAssignedDate().toString() : "",
                phone.getNotes() != null ? phone.getNotes() : ""
            });
        }
        if ("excel".equalsIgnoreCase(format)) {
            return ExcelExportUtil.writeToExcel(rows);
        } else {
            return CsvExportUtil.writeToCsv(rows);
        }
    }
    public ByteArrayInputStream exportSimCards(String format) {
        List<SimCardDto> sims = simCardRepository.findAll().stream().map(SimCardDto::new).toList();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Number", "ICCID", "Status", "Assigned To ID", "Assigned To Name", "Assigned Date", "Notes", "PIN", "PUK", "POKE"});
        for (SimCardDto sim : sims) {
            rows.add(new String[]{
                String.valueOf(sim.getId()),
                sim.getNumber(),
                sim.getIccid(),
                sim.getStatus() != null ? sim.getStatus().name() : "",
                sim.getAssignedToId() != null ? sim.getAssignedToId().toString() : "",
                sim.getAssignedToName() != null ? sim.getAssignedToName() : "",
                sim.getAssignedDate() != null ? sim.getAssignedDate().toString() : "",
                sim.getNotes() != null ? sim.getNotes() : "",
                sim.getPin(),
                sim.getPuk(),
                sim.getPoke()
            });
        }
        if ("excel".equalsIgnoreCase(format)) {
            return ExcelExportUtil.writeToExcel(rows);
        } else {
            return CsvExportUtil.writeToCsv(rows);
        }
    }
    public ByteArrayInputStream exportAttributions(String format) {
        List<AttributionDto> attributions = attributionRepository.findAll().stream().map(AttributionDto::new).toList();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "User ID", "User Name", "User Email", "Phone ID", "Phone Model", "Phone Brand", "SIM Card ID", "SIM Card Number", "Assigned By ID", "Assigned By Name", "Assignment Date", "Return Date", "Status", "Notes", "Created At", "Updated At"});
        for (AttributionDto attr : attributions) {
            rows.add(new String[]{
                String.valueOf(attr.getId()),
                attr.getUserId() != null ? attr.getUserId().toString() : "",
                attr.getUserName() != null ? attr.getUserName() : "",
                attr.getUserEmail() != null ? attr.getUserEmail() : "",
                attr.getPhoneId() != null ? attr.getPhoneId().toString() : "",
                attr.getPhoneModel() != null ? attr.getPhoneModel() : "",
                attr.getPhoneBrand() != null ? attr.getPhoneBrand() : "",
                attr.getSimCardId() != null ? attr.getSimCardId().toString() : "",
                attr.getSimCardNumber() != null ? attr.getSimCardNumber() : "",
                attr.getAssignedById() != null ? attr.getAssignedById().toString() : "",
                attr.getAssignedByName() != null ? attr.getAssignedByName() : "",
                attr.getAssignmentDate() != null ? attr.getAssignmentDate().toString() : "",
                attr.getReturnDate() != null ? attr.getReturnDate().toString() : "",
                attr.getStatus() != null ? attr.getStatus().name() : "",
                attr.getNotes() != null ? attr.getNotes() : "",
                attr.getCreatedAt() != null ? attr.getCreatedAt().toString() : "",
                attr.getUpdatedAt() != null ? attr.getUpdatedAt().toString() : ""
            });
        }
        if ("excel".equalsIgnoreCase(format)) {
            return ExcelExportUtil.writeToExcel(rows);
        } else {
            return CsvExportUtil.writeToCsv(rows);
        }
    }
    public ByteArrayInputStream exportRequests(String format) {
        List<RequestDto> requests = requestRepository.findAll().stream().map(RequestDto::new).toList();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "User ID", "User Name", "User Email", "Type", "Title", "Description", "Status", "Priority", "Assigned To ID", "Assigned To Name", "Created At", "Updated At", "Resolved At", "Resolution"});
        for (RequestDto req : requests) {
            rows.add(new String[]{
                String.valueOf(req.getId()),
                req.getUserId() != null ? req.getUserId().toString() : "",
                req.getUserName() != null ? req.getUserName() : "",
                req.getUserEmail() != null ? req.getUserEmail() : "",
                req.getType() != null ? req.getType().name() : "",
                req.getTitle() != null ? req.getTitle() : "",
                req.getDescription() != null ? req.getDescription() : "",
                req.getStatus() != null ? req.getStatus().name() : "",
                req.getPriority() != null ? req.getPriority().name() : "",
                req.getAssignedToId() != null ? req.getAssignedToId().toString() : "",
                req.getAssignedToName() != null ? req.getAssignedToName() : "",
                req.getCreatedAt() != null ? req.getCreatedAt().toString() : "",
                req.getUpdatedAt() != null ? req.getUpdatedAt().toString() : "",
                req.getResolvedAt() != null ? req.getResolvedAt().toString() : "",
                req.getResolution() != null ? req.getResolution() : ""
            });
        }
        if ("excel".equalsIgnoreCase(format)) {
            return ExcelExportUtil.writeToExcel(rows);
        } else {
            return CsvExportUtil.writeToCsv(rows);
        }
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
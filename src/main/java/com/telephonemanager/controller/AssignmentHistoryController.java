package com.telephonemanager.controller;

import com.telephonemanager.dto.AssignmentHistoryDto;
import com.telephonemanager.service.AssignmentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assignment-history")
@Tag(name = "Assignment History", description = "Endpoints for assignment and transfer history")
public class AssignmentHistoryController {
    @Autowired
    private AssignmentHistoryService historyService;

    @GetMapping("/phone/{phoneId}")
    @Operation(summary = "Get phone assignment history", description = "Get assignment/transfer history for a phone")
    public ResponseEntity<Map<String, Object>> getPhoneHistory(@PathVariable Long phoneId) {
        List<AssignmentHistoryDto> history = historyService.getHistoryByPhone(phoneId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", history);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sim/{simId}")
    @Operation(summary = "Get SIM card assignment history", description = "Get assignment/transfer history for a SIM card")
    public ResponseEntity<Map<String, Object>> getSimHistory(@PathVariable Long simId) {
        List<AssignmentHistoryDto> history = historyService.getHistoryBySim(simId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", history);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user assignment history", description = "Get assignment/transfer history for a user")
    public ResponseEntity<Map<String, Object>> getUserHistory(@PathVariable Long userId) {
        List<AssignmentHistoryDto> history = historyService.getHistoryByUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", history);
        return ResponseEntity.ok(response);
    }
} 
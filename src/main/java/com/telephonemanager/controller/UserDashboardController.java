package com.telephonemanager.controller;

import com.telephonemanager.entity.Attribution;
import com.telephonemanager.entity.Phone;
import com.telephonemanager.entity.Request;
import com.telephonemanager.entity.SimCard;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.AttributionRepository;
import com.telephonemanager.repository.RequestRepository;
import com.telephonemanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Dashboard", description = "Aggregated data for the authenticated user's dashboard")
public class UserDashboardController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AttributionRepository attributionRepository;

    @Autowired
    private RequestRepository requestRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get my dashboard data", description = "Returns user profile plus currently assigned phone, SIM card, and recent requests")
    public ResponseEntity<Map<String, Object>> getMyDashboard(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication.getName());

            Map<String, Object> userMap = buildUserMap(user);

            List<Attribution> activeAttributions = attributionRepository
                    .findByUserIdAndStatus(user.getId(), Attribution.Status.ACTIVE);

            Map<String, Object> phoneMap = null;
            Map<String, Object> simMap = null;

            if (activeAttributions != null && !activeAttributions.isEmpty()) {
                Optional<Attribution> latestPhoneAttr = activeAttributions.stream()
                        .filter(a -> a.getPhone() != null)
                        .max(Comparator.comparing(Attribution::getAssignmentDate));
                if (latestPhoneAttr.isPresent()) {
                    phoneMap = buildPhoneMap(latestPhoneAttr.get().getPhone(), latestPhoneAttr.get().getAssignmentDate());
                }

                Optional<Attribution> latestSimAttr = activeAttributions.stream()
                        .filter(a -> a.getSimCard() != null)
                        .max(Comparator.comparing(Attribution::getAssignmentDate));
                if (latestSimAttr.isPresent()) {
                    simMap = buildSimMap(latestSimAttr.get().getSimCard(), latestSimAttr.get().getAssignmentDate());
                }
            }

            var page = requestRepository.findByUserId(
                    user.getId(),
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            );

            List<Map<String, Object>> requests = new ArrayList<>();
            page.getContent().forEach(r -> requests.add(buildRequestMap(r)));

            Map<String, Object> data = new HashMap<>();
            data.put("user", userMap);
            data.put("phone", phoneMap);
            data.put("simCard", simMap);
            data.put("requests", requests);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                    "code", "DASHBOARD_ERROR",
                    "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        String fullName = Optional.ofNullable(user.getName()).orElse("");
        String firstName = fullName;
        String lastName = "";
        int spaceIdx = fullName.indexOf(' ');
        if (spaceIdx > 0) {
            firstName = fullName.substring(0, spaceIdx);
            lastName = fullName.substring(spaceIdx + 1);
        }
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", user.getEmail());
        map.put("department", user.getDepartment());
        map.put("profilePicture", user.getAvatar());
        return map;
    }

    private Map<String, Object> buildPhoneMap(Phone phone, LocalDate assignedDate) {
        if (phone == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("model", phone.getModel());
        map.put("serialNumber", phone.getSerialNumber());
        map.put("imei", phone.getImei());
        map.put("assignedDate", assignedDate != null ? assignedDate.toString() : null);
        map.put("status", phone.getStatus() != null ? phone.getStatus().name() : null);
        map.put("batteryHealth", null);
        map.put("storageUsed", null);
        map.put("lastSync", null);
        return map;
    }

    private Map<String, Object> buildSimMap(SimCard sim, LocalDate assignedDate) {
        if (sim == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("number", sim.getNumber());
        map.put("iccid", sim.getIccid());
        map.put("carrier", sim.getCarrier());
        map.put("dataPlan", sim.getPlan());
        map.put("assignedDate", assignedDate != null ? assignedDate.toString() : null);
        map.put("status", sim.getStatus() != null ? sim.getStatus().name() : null);
        map.put("lastSync", null);
        return map;
    }

    private Map<String, Object> buildRequestMap(Request r) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getId());
        map.put("type", mapRequestType(r.getType()));
        map.put("status", r.getStatus() != null ? r.getStatus().name() : null);
        map.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
        map.put("description", r.getDescription());
        return map;
    }

    private String mapRequestType(Request.Type type) {
        if (type == null) return null;
        return switch (type) {
            case PROBLEM -> "Problème";
            case REPLACEMENT -> "Remplacement";
            case SUPPORT -> "Support";
            case CHANGE -> "Changement";
        };
    }
}


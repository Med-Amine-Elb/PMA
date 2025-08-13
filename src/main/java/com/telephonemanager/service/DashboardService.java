package com.telephonemanager.service;

import com.telephonemanager.entity.Phone;
import com.telephonemanager.entity.SimCard;
import com.telephonemanager.entity.User;
import com.telephonemanager.entity.AssignmentHistory;
import com.telephonemanager.repository.PhoneRepository;
import com.telephonemanager.repository.SimCardRepository;
import com.telephonemanager.repository.UserRepository;
import com.telephonemanager.repository.AssignmentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private SimCardRepository simCardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssignmentHistoryRepository assignmentHistoryRepository;

    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // Total counts
        long totalPhones = phoneRepository.count();
        long totalSimCards = simCardRepository.count();
        long totalUsers = userRepository.count();
        
        // Assigned counts
        long assignedPhones = phoneRepository.countByAssignedToIsNotNull();
        long assignedSimCards = simCardRepository.countByAssignedToIsNotNull();
        
        // Available counts
        long availablePhones = phoneRepository.countByStatus(Phone.Status.AVAILABLE);
        long availableSimCards = simCardRepository.countByStatus(SimCard.Status.AVAILABLE);
        
        // Recent activity (last 7 days)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentAssignments = assignmentHistoryRepository.countByDateAfter(weekAgo);
        
        // Monthly statistics for the last 6 months
        List<Map<String, Object>> monthlyStats = generateMonthlyStats();
        
        // Pending requests and maintenance data
        long pendingRequests = assignmentHistoryRepository.countByDateAfter(weekAgo);
        long pendingReturns = assignmentHistoryRepository.countByDateBetweenAndAction(
            weekAgo, LocalDateTime.now(), AssignmentHistory.Action.UNASSIGN);
        long scheduledMaintenance = phoneRepository.countByStatus(Phone.Status.DAMAGED);
        
        overview.put("totals", Map.of(
            "phones", totalPhones,
            "simCards", totalSimCards,
            "users", totalUsers
        ));
        
        overview.put("assigned", Map.of(
            "phones", assignedPhones,
            "simCards", assignedSimCards
        ));
        
        overview.put("available", Map.of(
            "phones", availablePhones,
            "simCards", availableSimCards
        ));
        
        overview.put("recentActivity", Map.of(
            "assignmentsLastWeek", recentAssignments
        ));
        
        overview.put("monthlyStats", monthlyStats);
        overview.put("pendingRequests", pendingRequests);
        overview.put("pendingReturns", pendingReturns);
        overview.put("scheduledMaintenance", scheduledMaintenance);
        
        return overview;
    }

    public Map<String, Object> getPhoneStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Status distribution
        Map<String, Long> statusCounts = new HashMap<>();
        for (Phone.Status status : Phone.Status.values()) {
            long count = phoneRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }
        
        // Brand distribution
        List<Object[]> brandStats = phoneRepository.findBrandDistribution();
        Map<String, Long> brandCounts = brandStats.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
        
        // Assignment rate
        long totalPhones = phoneRepository.count();
        long assignedPhones = phoneRepository.countByAssignedToIsNotNull();
        double assignmentRate = totalPhones > 0 ? (double) assignedPhones / totalPhones * 100 : 0;
        
        stats.put("statusDistribution", statusCounts);
        stats.put("brandDistribution", brandCounts);
        stats.put("assignmentRate", Math.round(assignmentRate * 100.0) / 100.0);
        stats.put("totalPhones", totalPhones);
        stats.put("assignedPhones", assignedPhones);
        stats.put("availablePhones", totalPhones - assignedPhones);
        
        return stats;
    }

    public Map<String, Object> getSimCardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Status distribution
        Map<String, Long> statusCounts = new HashMap<>();
        for (SimCard.Status status : SimCard.Status.values()) {
            long count = simCardRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }
        
        // Assignment rate
        long totalSimCards = simCardRepository.count();
        long assignedSimCards = simCardRepository.countByAssignedToIsNotNull();
        double assignmentRate = totalSimCards > 0 ? (double) assignedSimCards / totalSimCards * 100 : 0;
        
        stats.put("statusDistribution", statusCounts);
        stats.put("assignmentRate", Math.round(assignmentRate * 100.0) / 100.0);
        stats.put("totalSimCards", totalSimCards);
        stats.put("assignedSimCards", assignedSimCards);
        stats.put("availableSimCards", totalSimCards - assignedSimCards);
        
        return stats;
    }

    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Role distribution
        Map<String, Long> roleCounts = new HashMap<>();
        for (User.UserRole role : User.UserRole.values()) {
            long count = userRepository.countByRole(role);
            roleCounts.put(role.name(), count);
        }
        
        // Department distribution with enhanced data
        List<Map<String, Object>> departmentStats = generateDepartmentStats();
        
        // Users with assignments
        long totalUsers = userRepository.count();
        long usersWithPhones = phoneRepository.countByAssignedToIsNotNull();
        long usersWithSimCards = simCardRepository.countByAssignedToIsNotNull();
        
        stats.put("roleDistribution", roleCounts);
        stats.put("departmentStats", departmentStats);
        stats.put("totalUsers", totalUsers);
        stats.put("usersWithPhones", usersWithPhones);
        stats.put("usersWithSimCards", usersWithSimCards);
        
        return stats;
    }

    public Map<String, Object> getRecentActivity(int limit) {
        Map<String, Object> activity = new HashMap<>();
        
        // Recent assignments
        List<AssignmentHistory> recentHistory = assignmentHistoryRepository
            .findTop10ByOrderByDateDesc();
        
        List<Map<String, Object>> activities = recentHistory.stream()
            .map(this::convertToActivityMap)
            .collect(Collectors.toList());
        
        activity.put("recentActivities", activities);
        activity.put("totalActivities", activities.size());
        
        return activity;
    }

    public Map<String, Object> getAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        List<Map<String, Object>> alertList = new ArrayList<>();
        
        // Phones in damaged status for more than 30 days
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Phone> longDamagedPhones = phoneRepository
            .findByStatusAndAssignedDateBefore(Phone.Status.DAMAGED, thirtyDaysAgo);
        
        for (Phone phone : longDamagedPhones) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "LONG_DAMAGED_PHONE");
            alert.put("itemId", phone.getId());
            alert.put("itemName", phone.getBrand() + " " + phone.getModel());
            alert.put("message", "Phone in damaged status for more than 30 days");
            alert.put("severity", "MEDIUM");
            alertList.add(alert);
        }
        
        // SIM cards in blocked status for more than 30 days
        List<SimCard> longBlockedSims = simCardRepository
            .findByStatusAndAssignedDateBefore(SimCard.Status.BLOCKED, thirtyDaysAgo);
        
        for (SimCard sim : longBlockedSims) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "LONG_BLOCKED_SIM");
            alert.put("itemId", sim.getId());
            alert.put("itemName", sim.getNumber());
            alert.put("message", "SIM card in blocked status for more than 30 days");
            alert.put("severity", "MEDIUM");
            alertList.add(alert);
        }
        
        // Users with multiple assignments (simplified for now)
        // Will be implemented when we add proper relationships
        // For now, we'll skip this alert type
        
        alerts.put("alerts", alertList);
        alerts.put("totalAlerts", alertList.size());
        alerts.put("highPriorityAlerts", alertList.stream()
            .filter(a -> "HIGH".equals(a.get("severity"))).count());
        alerts.put("mediumPriorityAlerts", alertList.stream()
            .filter(a -> "MEDIUM".equals(a.get("severity"))).count());
        alerts.put("lowPriorityAlerts", alertList.stream()
            .filter(a -> "LOW".equals(a.get("severity"))).count());
        
        return alerts;
    }

    private Map<String, Object> convertToActivityMap(AssignmentHistory history) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("id", history.getId());
        activity.put("type", history.getType().name());
        activity.put("action", history.getAction().name());
        activity.put("itemId", history.getItemId());
        activity.put("fromUserId", history.getFromUserId());
        activity.put("toUserId", history.getToUserId());
        activity.put("date", history.getDate());
        activity.put("notes", history.getNotes());
        return activity;
    }
    
    private List<Map<String, Object>> generateMonthlyStats() {
        List<Map<String, Object>> monthlyStats = new ArrayList<>();
        String[] monthNames = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun"};
        
        // Generate data for the last 6 months
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            
            // Count attributions for this month
            long attributions = assignmentHistoryRepository.countByDateBetweenAndAction(
                monthStart, monthEnd, AssignmentHistory.Action.ASSIGN);
            
            // Count returns for this month
            long returns = assignmentHistoryRepository.countByDateBetweenAndAction(
                monthStart, monthEnd, AssignmentHistory.Action.RETURN);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", monthNames[5 - i]);
            monthData.put("attributions", attributions);
            monthData.put("returns", returns);
            
            monthlyStats.add(monthData);
        }
        
        return monthlyStats;
    }
    
    private List<Map<String, Object>> generateDepartmentStats() {
        List<Map<String, Object>> departmentStats = new ArrayList<>();
        
        // Get department distribution from repository
        List<Object[]> deptData = userRepository.findDepartmentDistribution();
        
        for (Object[] row : deptData) {
            String department = (String) row[0];
            Long userCount = (Long) row[1];
            
            // For now, estimate assigned phones and SIMs based on user count
            // This can be enhanced when proper relationships are established
            long estimatedPhones = Math.round(userCount * 0.8); // 80% of users have phones
            long estimatedSims = Math.round(userCount * 0.9);   // 90% of users have SIMs
            
            Map<String, Object> deptStat = new HashMap<>();
            deptStat.put("department", department);
            deptStat.put("totalUsers", userCount);
            deptStat.put("assignedPhones", estimatedPhones);
            deptStat.put("assignedSimCards", estimatedSims);
            
            departmentStats.add(deptStat);
        }
        
        return departmentStats;
    }
} 
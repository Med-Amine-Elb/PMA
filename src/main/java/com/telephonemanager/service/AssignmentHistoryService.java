package com.telephonemanager.service;

import com.telephonemanager.dto.AssignmentHistoryDto;
import com.telephonemanager.entity.AssignmentHistory;
import com.telephonemanager.entity.AssignmentHistory.Action;
import com.telephonemanager.entity.AssignmentHistory.Type;
import com.telephonemanager.repository.AssignmentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentHistoryService {
    @Autowired
    private AssignmentHistoryRepository historyRepository;

    public void record(Type type, Long itemId, Long fromUserId, Long toUserId, Action action, String notes) {
        AssignmentHistory h = new AssignmentHistory();
        h.setType(type);
        h.setItemId(itemId);
        h.setFromUserId(fromUserId);
        h.setToUserId(toUserId);
        h.setAction(action);
        h.setDate(LocalDateTime.now());
        h.setNotes(notes);
        historyRepository.save(h);
    }

    public List<AssignmentHistoryDto> getHistoryByPhone(Long phoneId) {
        return historyRepository.findByTypeAndItemId(Type.PHONE, phoneId)
                .stream().map(AssignmentHistoryDto::new).collect(Collectors.toList());
    }

    public List<AssignmentHistoryDto> getHistoryBySim(Long simId) {
        return historyRepository.findByTypeAndItemId(Type.SIM, simId)
                .stream().map(AssignmentHistoryDto::new).collect(Collectors.toList());
    }

    public List<AssignmentHistoryDto> getHistoryByUser(Long userId) {
        return historyRepository.findByToUserIdOrFromUserId(userId, userId)
                .stream().map(AssignmentHistoryDto::new).collect(Collectors.toList());
    }
} 
package com.telephonemanager.service;

import com.telephonemanager.dto.RequestDto;
import com.telephonemanager.entity.Request;
import com.telephonemanager.entity.Request.Priority;
import com.telephonemanager.entity.Request.Status;
import com.telephonemanager.entity.Request.Type;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.RequestRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestService {
    
    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Page<RequestDto> getRequests(int page, int limit, Status status, Type type, Priority priority, Long userId, Long assignedToId, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Request> requests = requestRepository.findWithFilters(userId, status, type, priority, assignedToId, search, pageable);
        return requests.map(RequestDto::new);
    }

    public Optional<RequestDto> getRequestById(Long id) {
        return requestRepository.findById(id).map(RequestDto::new);
    }

    public RequestDto createRequest(RequestDto dto, Long userId) {
        // Validate user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate assigned to user if provided
        User assignedTo = null;
        if (dto.getAssignedToId() != null) {
            assignedTo = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Assigned to user not found"));
        }
        
        // Create request
        Request request = new Request();
        request.setUser(user);
        request.setType(dto.getType());
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.PENDING);
        request.setPriority(dto.getPriority() != null ? dto.getPriority() : Priority.NORMAL);
        request.setAssignedTo(assignedTo);
        
        Request saved = requestRepository.save(request);
        return new RequestDto(saved);
    }

    public RequestDto updateRequest(Long id, RequestDto dto) {
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        // Update fields
        if (dto.getTitle() != null) {
            request.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            request.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
            
            // Set resolved date if status is RESOLVED
            if (dto.getStatus() == Status.RESOLVED && request.getResolvedAt() == null) {
                request.setResolvedAt(LocalDateTime.now());
            }
        }
        if (dto.getPriority() != null) {
            request.setPriority(dto.getPriority());
        }
        if (dto.getResolution() != null) {
            request.setResolution(dto.getResolution());
        }
        
        // Update assigned to if provided
        if (dto.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Assigned to user not found"));
            request.setAssignedTo(assignedTo);
        }
        
        Request saved = requestRepository.save(request);
        return new RequestDto(saved);
    }

    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new RuntimeException("Request not found");
        }
        requestRepository.deleteById(id);
    }

    public RequestDto addComment(Long id, String comment) {
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        // Add comment to description (in a real app, you'd have a separate comments table)
        String currentDescription = request.getDescription();
        String newDescription = currentDescription + "\n\n--- Comment ---\n" + comment + "\n--- End Comment ---";
        request.setDescription(newDescription);
        
        Request saved = requestRepository.save(request);
        return new RequestDto(saved);
    }

    public List<RequestDto> getRequestsByUser(Long userId) {
        List<Request> requests = requestRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return requests.stream().map(RequestDto::new).collect(Collectors.toList());
    }

    public List<RequestDto> getPendingRequests() {
        List<Request> requests = requestRepository.findByStatusOrderByCreatedAtDesc(Status.PENDING);
        return requests.stream().map(RequestDto::new).collect(Collectors.toList());
    }

    public List<RequestDto> getUrgentRequests() {
        List<Request> requests = requestRepository.findByPriorityOrderByCreatedAtDesc(Priority.URGENT);
        return requests.stream().map(RequestDto::new).collect(Collectors.toList());
    }

    public RequestDto assignRequest(Long requestId, Long assignedToId) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        User assignedTo = userRepository.findById(assignedToId)
            .orElseThrow(() -> new RuntimeException("Assigned to user not found"));
        
        request.setAssignedTo(assignedTo);
        request.setStatus(Status.IN_PROGRESS);
        
        Request saved = requestRepository.save(request);
        return new RequestDto(saved);
    }

    public RequestDto resolveRequest(Long requestId, String resolution) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(Status.RESOLVED);
        request.setResolution(resolution);
        request.setResolvedAt(LocalDateTime.now());
        
        Request saved = requestRepository.save(request);
        return new RequestDto(saved);
    }

    public RequestDto rejectRequest(Long requestId, String reason) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(Status.REJECTED);
        request.setResolution("Rejected: " + reason);
        
        Request saved = requestRepository.save(request);
        return new RequestDto(saved);
    }
} 
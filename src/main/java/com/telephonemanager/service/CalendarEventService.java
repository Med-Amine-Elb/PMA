package com.telephonemanager.service;

import com.telephonemanager.dto.CalendarEventDto;
import com.telephonemanager.entity.CalendarEvent;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.CalendarEventRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CalendarEventService {
    
    @Autowired
    private CalendarEventRepository calendarEventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<CalendarEventDto> getAllEvents(Pageable pageable) {
        return calendarEventRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    public Optional<CalendarEventDto> getEventById(Long id) {
        return calendarEventRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public CalendarEventDto createEvent(CalendarEventDto eventDto) {
        CalendarEvent event = convertToEntity(eventDto);
        CalendarEvent savedEvent = calendarEventRepository.save(event);
        return convertToDto(savedEvent);
    }
    
    public Optional<CalendarEventDto> updateEvent(Long id, CalendarEventDto eventDto) {
        return calendarEventRepository.findById(id)
                .map(existingEvent -> {
                    updateEventFromDto(existingEvent, eventDto);
                    CalendarEvent savedEvent = calendarEventRepository.save(existingEvent);
                    return convertToDto(savedEvent);
                });
    }
    
    public boolean deleteEvent(Long id) {
        if (calendarEventRepository.existsById(id)) {
            calendarEventRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<CalendarEventDto> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return calendarEventRepository.findByDateRange(startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<CalendarEventDto> getEventsByOrganizer(Long organizerId, Pageable pageable) {
        Optional<User> organizer = userRepository.findById(organizerId);
        if (organizer.isPresent()) {
            return calendarEventRepository.findByOrganizer(organizer.get(), pageable)
                    .map(this::convertToDto);
        }
        return Page.empty(pageable);
    }
    
    public Page<CalendarEventDto> getEventsByAttendee(Long attendeeId, Pageable pageable) {
        return calendarEventRepository.findByAttendeeId(attendeeId, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CalendarEventDto> getEventsByType(String type, Pageable pageable) {
        try {
            CalendarEvent.EventType eventType = CalendarEvent.EventType.valueOf(type.toUpperCase());
            return calendarEventRepository.findByType(eventType, pageable)
                    .map(this::convertToDto);
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }
    }
    
    public Page<CalendarEventDto> getEventsByStatus(String status, Pageable pageable) {
        try {
            CalendarEvent.EventStatus eventStatus = CalendarEvent.EventStatus.valueOf(status.toUpperCase());
            return calendarEventRepository.findByStatus(eventStatus, pageable)
                    .map(this::convertToDto);
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }
    }
    
    public List<CalendarEventDto> getUpcomingEventsForUser(Long userId) {
        return calendarEventRepository.findUpcomingEventsForUser(userId, LocalDateTime.now())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<CalendarEventDto> searchEventsByLocation(String location, Pageable pageable) {
        return calendarEventRepository.findByLocationContainingIgnoreCase(location, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CalendarEventDto> searchEventsByTitle(String title, Pageable pageable) {
        return calendarEventRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CalendarEventDto> getAllDayEvents(Pageable pageable) {
        return calendarEventRepository.findByIsAllDayTrue(pageable)
                .map(this::convertToDto);
    }
    
    public Page<CalendarEventDto> getRecurringEvents(Pageable pageable) {
        return calendarEventRepository.findByRecurrenceIsNotNull(pageable)
                .map(this::convertToDto);
    }
    
    private CalendarEventDto convertToDto(CalendarEvent event) {
        CalendarEventDto dto = new CalendarEventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setLocation(event.getLocation());
        dto.setType(event.getType().name());
        dto.setStatus(event.getStatus().name());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getName());
        dto.setAttendeeIds(event.getAttendees().stream().map(User::getId).collect(Collectors.toSet()));
        dto.setAttendeeNames(event.getAttendees().stream()
                .map(User::getName)
                .collect(Collectors.toSet()));
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setColor(event.getColor());
        dto.setAllDay(event.isAllDay());
        dto.setRecurrence(event.getRecurrence());
        return dto;
    }
    
    private CalendarEvent convertToEntity(CalendarEventDto dto) {
        CalendarEvent event = new CalendarEvent();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());
        event.setType(CalendarEvent.EventType.valueOf(dto.getType()));
        event.setStatus(CalendarEvent.EventStatus.valueOf(dto.getStatus()));
        event.setColor(dto.getColor());
        event.setAllDay(dto.isAllDay());
        event.setRecurrence(dto.getRecurrence());
        
        // Set organizer
        if (dto.getOrganizerId() != null) {
            userRepository.findById(dto.getOrganizerId()).ifPresent(event::setOrganizer);
        }
        
        // Set attendees
        if (dto.getAttendeeIds() != null) {
            Set<User> attendees = dto.getAttendeeIds().stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            event.setAttendees(attendees);
        }
        
        return event;
    }
    
    private void updateEventFromDto(CalendarEvent event, CalendarEventDto dto) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getStartTime() != null) event.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) event.setEndTime(dto.getEndTime());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getType() != null) event.setType(CalendarEvent.EventType.valueOf(dto.getType()));
        if (dto.getStatus() != null) event.setStatus(CalendarEvent.EventStatus.valueOf(dto.getStatus()));
        if (dto.getColor() != null) event.setColor(dto.getColor());
        event.setAllDay(dto.isAllDay());
        if (dto.getRecurrence() != null) event.setRecurrence(dto.getRecurrence());
        
        // Update organizer if provided
        if (dto.getOrganizerId() != null) {
            userRepository.findById(dto.getOrganizerId()).ifPresent(event::setOrganizer);
        }
        
        // Update attendees if provided
        if (dto.getAttendeeIds() != null) {
            Set<User> attendees = dto.getAttendeeIds().stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            event.setAttendees(attendees);
        }
    }
} 
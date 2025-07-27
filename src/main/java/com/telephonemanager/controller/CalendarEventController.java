package com.telephonemanager.controller;

import com.telephonemanager.dto.CalendarEventDto;
import com.telephonemanager.service.CalendarEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class CalendarEventController {
    
    @Autowired
    private CalendarEventService calendarEventService;
    
    // Get all events with pagination
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CalendarEventDto> events = calendarEventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get event by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<CalendarEventDto> getEventById(@PathVariable Long id) {
        Optional<CalendarEventDto> event = calendarEventService.getEventById(id);
        return event.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new event
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<CalendarEventDto> createEvent(@RequestBody CalendarEventDto eventDto) {
        CalendarEventDto createdEvent = calendarEventService.createEvent(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
    
    // Update event
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<CalendarEventDto> updateEvent(
            @PathVariable Long id, 
            @RequestBody CalendarEventDto eventDto) {
        Optional<CalendarEventDto> updatedEvent = calendarEventService.updateEvent(id, eventDto);
        return updatedEvent.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete event
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean deleted = calendarEventService.deleteEvent(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Get events by date range
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<CalendarEventDto>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<CalendarEventDto> events = calendarEventService.getEventsByDateRange(startDate, endDate);
        return ResponseEntity.ok(events);
    }
    
    // Get events by organizer
    @GetMapping("/organizer/{organizerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getEventsByOrganizer(
            @PathVariable Long organizerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.getEventsByOrganizer(organizerId, pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get events by attendee
    @GetMapping("/attendee/{attendeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getEventsByAttendee(
            @PathVariable Long attendeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.getEventsByAttendee(attendeeId, pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get events by type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getEventsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.getEventsByType(type, pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get events by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getEventsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.getEventsByStatus(status, pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get upcoming events for current user
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<CalendarEventDto>> getUpcomingEvents(@RequestParam Long userId) {
        List<CalendarEventDto> events = calendarEventService.getUpcomingEventsForUser(userId);
        return ResponseEntity.ok(events);
    }
    
    // Search events by location
    @GetMapping("/search/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> searchEventsByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.searchEventsByLocation(location, pageable);
        return ResponseEntity.ok(events);
    }
    
    // Search events by title
    @GetMapping("/search/title")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> searchEventsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.searchEventsByTitle(title, pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get all-day events
    @GetMapping("/all-day")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getAllDayEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.getAllDayEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    // Get recurring events
    @GetMapping("/recurring")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<CalendarEventDto>> getRecurringEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CalendarEventDto> events = calendarEventService.getRecurringEvents(pageable);
        return ResponseEntity.ok(events);
    }
} 
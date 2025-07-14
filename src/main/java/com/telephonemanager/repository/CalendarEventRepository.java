package com.telephonemanager.repository;

import com.telephonemanager.entity.CalendarEvent;
import com.telephonemanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    
    // Find events by date range
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :startDate AND e.endTime <= :endDate")
    List<CalendarEvent> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    // Find events by organizer
    Page<CalendarEvent> findByOrganizer(User organizer, Pageable pageable);
    
    // Find events where user is an attendee
    @Query("SELECT e FROM CalendarEvent e JOIN e.attendees a WHERE a.id = :userId")
    Page<CalendarEvent> findByAttendeeId(@Param("userId") Long userId, Pageable pageable);
    
    // Find events by type
    Page<CalendarEvent> findByType(CalendarEvent.EventType type, Pageable pageable);
    
    // Find events by status
    Page<CalendarEvent> findByStatus(CalendarEvent.EventStatus status, Pageable pageable);
    
    // Find upcoming events for a user (as organizer or attendee)
    @Query("SELECT e FROM CalendarEvent e WHERE (e.organizer.id = :userId OR :userId IN (SELECT a.id FROM e.attendees a)) AND e.startTime >= :now ORDER BY e.startTime")
    List<CalendarEvent> findUpcomingEventsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Find events by location
    Page<CalendarEvent> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    // Find events by title containing
    Page<CalendarEvent> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    // Find all-day events
    Page<CalendarEvent> findByIsAllDayTrue(Pageable pageable);
    
    // Find events with recurrence
    Page<CalendarEvent> findByRecurrenceIsNotNull(Pageable pageable);
} 
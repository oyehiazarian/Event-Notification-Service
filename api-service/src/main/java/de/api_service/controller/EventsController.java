package de.api_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.api_service.dto.EventRequest;
import de.api_service.dto.EventResponse;
import de.api_service.model.User;
import de.api_service.service.EventsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    /** Admin only, see {@code SecurityConfig}. */
    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventsService.create(request));
    }

    @GetMapping
    public List<EventResponse> list(@AuthenticationPrincipal User current) {
        return eventsService.list(current);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User current) {
        eventsService.delete(id, current);
        return ResponseEntity.noContent().build();
    }
}

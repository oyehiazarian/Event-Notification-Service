package de.api_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.api_service.model.Events;
import de.api_service.service.EventsService;

@RestController
public class EventsController {

    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PostMapping("/new_event")
    public ResponseEntity<Map<String, String>> newEvent(@RequestBody Events events) {
        eventsService.registerNewEvent(events);
        Map<String, String> body = Map.of(
                "status", "successfully"
        );
        return ResponseEntity.status(200)
                .body(body);
    }

    @GetMapping("/all-events")
    public String allEvents() {
        return eventsService.allEventsFromUser();
    }

    @PostMapping("/delete-event")
    public String deleteEvent(@RequestBody Events events) {
        return eventsService.deleteEventsFromUser(events);
    }
}

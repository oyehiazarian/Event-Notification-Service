package de.api_service.controller;

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
    public String newEvent(@RequestBody Events events){
        eventsService.registerNewEvent(events);
        return "OK";

    }
}

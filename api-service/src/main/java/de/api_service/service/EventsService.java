package de.api_service.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import de.api_service.model.Events;
import de.api_service.model.User;
import de.api_service.repository.EventsRepository;
import de.api_service.repository.UserRepository;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;

    private final UserRepository userRepository;

    public final AuthenticationService authenticationService;

    public EventsService(EventsRepository eventsRepository, UserRepository userRepository,
            AuthenticationService authenticationService) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;

    }

    public boolean registerNewEvent(Events request) {
        Events events = new Events();

        events.setTitle(request.getTitle());
        events.setTopic(request.getTopic());
        events.setContent(request.getContent());
        events.setDue_date(request.getDue_date());
        events.setUserId(request.getUserId());
        eventsRepository.save(events);
        return true;

    }

    public String allEventsFromUser() {
        List<Events> events = eventsRepository.findAllByUserId(gettingUserId());

        StringBuilder result = new StringBuilder();

        for (Events event : events) {
            result.append("Title: ").append(event.getTitle()).append("\n")
                    .append("Topic: ").append(event.getTopic()).append("\n")
                    .append("Due Date: ").append(event.getDue_date()).append("\n")
                    .append("Content: ").append(event.getContent()).append("\n")
                    .append("------\n");
        }

        return result.toString();
    }

    public String deleteEventsFromUser(Events events) {
        eventsRepository.deleteById(events.getId());
        return "OK, DELETED";
    }

    public User gettingUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user;
    }
}

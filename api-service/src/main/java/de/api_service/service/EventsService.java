package de.api_service.service;

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

    public EventsService(EventsRepository eventsRepository, UserRepository userRepository, AuthenticationService authenticationService) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public String registerNewEvent(Events request){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Events events = new Events();


        events.setTitle(request.getTitle());
        events.setTopic(request.getTopic());
        events.setContent(request.getContent());
        events.setDue_date(request.getDue_date());

        events.setUserId(user);
        eventsRepository.save(events);

        return "The Event was successfully created ";
    }
}

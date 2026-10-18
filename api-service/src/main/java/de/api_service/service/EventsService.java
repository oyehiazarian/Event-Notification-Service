package de.api_service.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import de.api_service.dto.EventRequest;
import de.api_service.dto.EventResponse;
import de.api_service.model.Event;
import de.api_service.model.Role;
import de.api_service.model.User;
import de.api_service.repository.EventsRepository;
import de.api_service.repository.UserRepository;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;

    public EventsService(EventsRepository eventsRepository, UserRepository userRepository) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        User recipient = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));

        Event event = new Event();
        event.setTitle(request.title());
        event.setTopic(request.topic());
        event.setContent(request.content());
        event.setDueDate(request.dueDate());
        event.setUserId(recipient.getId());
        event.setSent(false);

        return toResponse(eventsRepository.save(event), recipient.getUsername());
    }

    /** Admins see every event, regular users only the ones addressed to them. */
    @Transactional(readOnly = true)
    public List<EventResponse> list(User current) {
        List<Event> events = current.getRole() == Role.ADMIN
                ? eventsRepository.findAllByOrderByDueDateAsc()
                : eventsRepository.findAllByUserIdOrderByDueDateAsc(current.getId());

        List<Long> userIds = events.stream().map(Event::getUserId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> usernames = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        return events.stream()
                .map(e -> toResponse(e, usernames.get(e.getUserId())))
                .toList();
    }

    @Transactional
    public void delete(Long id, User current) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        boolean owner = current.getId().equals(event.getUserId());
        if (!owner && current.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this event");
        }
        eventsRepository.delete(event);
    }

    private EventResponse toResponse(Event e, String username) {
        return new EventResponse(e.getId(), e.getTitle(), e.getTopic(), e.getContent(), e.getDueDate(),
                e.getUserId(), username);
    }
}

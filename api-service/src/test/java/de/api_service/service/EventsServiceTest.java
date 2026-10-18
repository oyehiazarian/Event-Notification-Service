package de.api_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import de.api_service.dto.EventRequest;
import de.api_service.dto.EventResponse;
import de.api_service.model.Event;
import de.api_service.model.Role;
import de.api_service.model.User;
import de.api_service.repository.EventsRepository;
import de.api_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class EventsServiceTest {

    @Mock
    EventsRepository eventsRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    EventsService service;

    private static User user(long id, String name, Role role) {
        User u = new User();
        u.setId(id);
        u.setUsername(name);
        u.setRole(role);
        return u;
    }

    private static Event event(long id, long userId) {
        Event e = new Event();
        e.setId(id);
        e.setUserId(userId);
        e.setTitle("t");
        e.setDueDate(LocalDateTime.of(2030, 1, 1, 10, 0));
        return e;
    }

    @Test
    void createStoresEventAsNotYetSent() {
        User bob = user(2, "bob", Role.USER);
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(eventsRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        EventResponse response = service.create(
                new EventRequest("Meeting", "Work", "Details", LocalDateTime.of(2030, 1, 1, 10, 0), 2L));

        assertEquals(10L, response.id());
        assertEquals("bob", response.username());
        org.mockito.ArgumentCaptor<Event> captor = org.mockito.ArgumentCaptor.forClass(Event.class);
        verify(eventsRepository).save(captor.capture());
        assertFalse(captor.getValue().isSent());
    }

    @Test
    void createForUnknownUserIsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(
                new EventRequest("Meeting", null, null, LocalDateTime.now(), 99L)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void userCannotDeleteForeignEvent() {
        when(eventsRepository.findById(5L)).thenReturn(Optional.of(event(5, 2)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.delete(5L, user(3, "eve", Role.USER)));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(eventsRepository, never()).delete(any());
    }

    @Test
    void ownerAndAdminCanDelete() {
        Event e = event(5, 2);
        when(eventsRepository.findById(5L)).thenReturn(Optional.of(e));

        service.delete(5L, user(2, "bob", Role.USER));
        service.delete(5L, user(1, "root", Role.ADMIN));

        verify(eventsRepository, org.mockito.Mockito.times(2)).delete(e);
    }

    @Test
    void regularUserOnlyListsOwnEvents() {
        User bob = user(2, "bob", Role.USER);
        when(eventsRepository.findAllByUserIdOrderByDueDateAsc(2L)).thenReturn(List.of(event(1, 2)));
        when(userRepository.findAllById(List.of(2L))).thenReturn(List.of(bob));

        List<EventResponse> result = service.list(bob);

        assertEquals(1, result.size());
        assertEquals("bob", result.get(0).username());
        verify(eventsRepository, never()).findAllByOrderByDueDateAsc();
    }

    @Test
    void adminListsEverything() {
        User root = user(1, "root", Role.ADMIN);
        when(eventsRepository.findAllByOrderByDueDateAsc()).thenReturn(List.of(event(1, 2), event(2, 3)));
        when(userRepository.findAllById(any())).thenReturn(List.of(user(2, "bob", Role.USER), user(3, "eve", Role.USER)));

        assertTrue(service.list(root).size() == 2);
    }
}

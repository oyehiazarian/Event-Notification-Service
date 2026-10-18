package de.api_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.api_service.model.Event;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByUserIdOrderByDueDateAsc(Long userId);

    List<Event> findAllByOrderByDueDateAsc();

    List<Event> findTop100BySentFalseOrderByIdAsc();
}

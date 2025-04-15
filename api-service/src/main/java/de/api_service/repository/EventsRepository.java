package de.api_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.api_service.model.Events;
import de.api_service.model.User;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    Optional<Events> findByUserId (User userId);
}

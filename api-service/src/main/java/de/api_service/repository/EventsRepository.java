package de.api_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.api_service.model.Events;
import de.api_service.model.User;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    List<Events> findAllByUserId(User userId);

}

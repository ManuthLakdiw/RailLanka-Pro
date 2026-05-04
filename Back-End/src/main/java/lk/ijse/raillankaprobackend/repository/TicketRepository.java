package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TicketRepository extends JpaRepository <Ticket,String>{

    @Query(value = "SELECT ticket_id FROM ticket ORDER BY ticket_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastTicketId();
}

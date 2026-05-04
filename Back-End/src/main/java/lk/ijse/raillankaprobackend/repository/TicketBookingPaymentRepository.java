package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.TicketBookingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TicketBookingPaymentRepository extends JpaRepository <TicketBookingPayment,String>{
    @Query(value = "SELECT ticket_booking_payment_id FROM ticket_booking_payment ORDER BY ticket_booking_payment_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastPaymentId();
}

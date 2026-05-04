package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Booking;
import lk.ijse.raillankaprobackend.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface BookingRepository extends JpaRepository <Booking,String>{

    @Query(value = "SELECT booking_id FROM booking ORDER BY booking_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastBookingId();


    List<Booking> findByPassenger(Passenger passenger);
}

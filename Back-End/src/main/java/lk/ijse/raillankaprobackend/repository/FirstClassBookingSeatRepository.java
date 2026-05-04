package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.FirstClassBookingSeat;
import lk.ijse.raillankaprobackend.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface FirstClassBookingSeatRepository extends JpaRepository <FirstClassBookingSeat,String>{

    @Query(value = "SELECT seat_id FROM first_class_booking_seat ORDER BY seat_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastBookingId();

    List<FirstClassBookingSeat> findByTravelDateAndSchedule(LocalDate travelDate, Schedule schedule);
}

package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Counter;
import lk.ijse.raillankaprobackend.entity.Dtypes.PassengerType;
import lk.ijse.raillankaprobackend.entity.Passenger;
import lk.ijse.raillankaprobackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PassengerRepository extends JpaRepository <Passenger,String> {

    @Query(value = "SELECT passenger_id FROM passenger ORDER BY passenger_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastPassengerId();

    @Query("SELECT p FROM Passenger p WHERE " +
            "LOWER(p.passengerId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.idNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Passenger> filterPassengerByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Passenger> findPassengerByBlocked(boolean blocked, Pageable pageable);

    Page<Passenger> findPassengerByPassengerType(PassengerType passengerType, Pageable pageable);

    List<Passenger> findPassengerByPassengerType(PassengerType passengerType);

    List<Passenger> findAllByBlocked(boolean blocked);

    List<Passenger> findByPassengerTypeAndBlocked(PassengerType passengerType, boolean blocked);

    Optional<Passenger> findByEmail(String newEmail);
}

package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}

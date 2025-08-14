package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface StationRepository extends JpaRepository <Station,String> {

    @Query(value = "SELECT station_id FROM station ORDER BY station_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastStationId();
}

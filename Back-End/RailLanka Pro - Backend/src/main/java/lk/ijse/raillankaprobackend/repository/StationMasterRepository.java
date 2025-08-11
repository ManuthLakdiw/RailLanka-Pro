package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.StationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface StationMasterRepository extends JpaRepository<StationMaster, String> {

    @Query(value = "SELECT station_master_id FROM station_master ORDER BY station_master_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastStationMasterId();
}


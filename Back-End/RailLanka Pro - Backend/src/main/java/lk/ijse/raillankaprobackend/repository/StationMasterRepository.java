package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.StationMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Query("SELECT sm.station.name FROM StationMaster sm")
    List<String> getAllAssignedStations();

    @Modifying
    @Transactional
    @Query("UPDATE StationMaster sm SET sm.active = ?2 WHERE sm.stationMasterId = ?1")
    void updateStationMasterStatus(String stationMasterId, boolean status);

    @Query("SELECT sm FROM StationMaster sm WHERE " +
            "LOWER(sm.stationMasterId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(sm.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(sm.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<StationMaster> filterStationsByKeyword(@Param("keyword") String keyword, Pageable pageable);


}


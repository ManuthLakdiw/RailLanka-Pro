package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Station;
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
public interface StationRepository extends JpaRepository <Station,String> {

    @Query(value = "SELECT station_id FROM station ORDER BY station_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastStationId();

    Optional<Station> findByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Station s SET s.inService = ?2 WHERE s.stationId = ?1")
    void updateStationServiceStatus(String stationId, boolean status);


    @Query("SELECT s FROM Station s WHERE " +
            "LOWER(s.stationId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Station> filterStationsByKeyword(@Param("keyword") String keyword, Pageable pageable);

}

package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Counter;
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
public interface CounterRepository extends JpaRepository <Counter,String> {

    @Query(value = "SELECT counter_id FROM counter ORDER BY counter_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastCounterId();

    @Query("SELECT c.counterNumber FROM Counter c WHERE c.station.name = :stationName")
    List<String> findCounterNumberByStationName(String stationName);

    @Modifying
    @Transactional
    @Query("UPDATE Counter c SET c.active = ?2 WHERE c.counterId = ?1")
    void updateCounterStatus(String counterId, boolean status);

    @Query("SELECT c FROM Counter c WHERE " +
            "LOWER(c.counterId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Counter> filterCountersByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

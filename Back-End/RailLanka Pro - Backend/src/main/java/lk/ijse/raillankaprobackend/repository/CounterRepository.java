package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Counter;
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
public interface CounterRepository extends JpaRepository <Counter,String> {

    @Query(value = "SELECT counter_id FROM counter ORDER BY counter_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastCounterId();

    @Query("SELECT c.counterNumber FROM Counter c WHERE c.station.name = :stationName")
    List<String> findCounterNumberByStationName(String stationName);
}

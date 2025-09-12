package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Dtypes.ScheduleFrequency;
import lk.ijse.raillankaprobackend.entity.Schedule;
import lk.ijse.raillankaprobackend.entity.Train;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,String> {

    @Query(value = "SELECT schedule_id FROM schedule ORDER BY schedule_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastScheduleId();

    @Query("SELECT s FROM Schedule s WHERE s.train = :train " +
            "AND ((s.mainDepartureTime < :arrivalTime AND s.mainArrivalTime > :departureTime))")
    List<Schedule> findConflictingSchedules(@Param("train") Train train,
                                            @Param("departureTime") LocalTime departureTime,
                                            @Param("arrivalTime") LocalTime arrivalTime);

    @Query("SELECT s FROM Schedule s " +
            "WHERE LOWER(s.scheduleId) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.train.trainId) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.train.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Schedule> filterSchedulesByKeyword(@Param("keyword") String keyword, Pageable pageable);


    Page<Schedule> findSchedulesByStatus(boolean status, Pageable pageable);

    Page<Schedule> findSchedulesByTrain(Train train, Pageable pageable);

    Page<Schedule> findSchedulesByScheduleFrequency(ScheduleFrequency scheduleFrequency, Pageable pageable);
}

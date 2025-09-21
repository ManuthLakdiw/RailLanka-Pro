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

import java.time.LocalDate;
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

    long countScheduleByStatus(boolean status);

    @Query(value = "SELECT AVG(daily_count) FROM ( " +
            "SELECT COUNT(*) AS daily_count " +
            "FROM schedule " +
            "WHERE schedule_frequency = 'DAILY' " +
            "GROUP BY train_id " +
            ") AS counts", nativeQuery = true)
    Double findAverageDailyTrips();

    @Query("SELECT s.scheduleFrequency, COUNT(s) " +
            "FROM Schedule s " +
            "GROUP BY s.scheduleFrequency")
    List<Object[]> getScheduleCountsByFrequency();


    List<Schedule> findSchedulesByStatus(boolean status);


    @Query("""
        SELECT DISTINCT s
        FROM Schedule s
        JOIN FETCH s.train t
        JOIN FETCH s.mainDepartureStation ds
        JOIN FETCH s.mainArrivalStation ars
        LEFT JOIN FETCH s.stops stp
        LEFT JOIN FETCH stp.station
        WHERE (
            ds.name = :fromStation OR EXISTS (
                SELECT 1 FROM ScheduleIntermediateStop sis
                WHERE sis.schedule = s AND sis.station.name = :fromStation
            )
        )
        AND (
            ars.name = :toStation OR EXISTS (
                SELECT 1 FROM ScheduleIntermediateStop sis
                WHERE sis.schedule = s AND sis.station.name = :toStation
            )
        )
    """)
    List<Schedule> findSchedulesWithRelatedDetails(
            @Param("fromStation") String fromStation,
            @Param("toStation") String toStation,
            @Param("departureDate") LocalDate departureDate
    );

    @Query("""
    SELECT DISTINCT s
    FROM Schedule s
    JOIN FETCH s.train t
    JOIN FETCH s.mainDepartureStation ds
    JOIN FETCH s.mainArrivalStation ars
    LEFT JOIN FETCH s.stops stp
    LEFT JOIN FETCH stp.station
    WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :trainName, '%'))
      AND (
          ds.name = :fromStation OR EXISTS (
              SELECT 1 FROM ScheduleIntermediateStop sis
              WHERE sis.schedule = s AND sis.station.name = :fromStation
          )
      )
      AND (
          ars.name = :toStation OR EXISTS (
              SELECT 1 FROM ScheduleIntermediateStop sis
              WHERE sis.schedule = s AND sis.station.name = :toStation
          )
      )

""")
    List<Schedule> findSchedulesWithRelatedDetailsByTrainName(
            @Param("fromStation") String fromStation,
            @Param("toStation") String toStation,
            @Param("trainName") String trainName,
            @Param("departureDate") LocalDate departureDate
    );

    @Query("""
    SELECT DISTINCT s
    FROM Schedule s
    JOIN FETCH s.train t
    JOIN FETCH s.mainDepartureStation ds
    JOIN FETCH s.mainArrivalStation ars
    LEFT JOIN FETCH s.stops stp
    LEFT JOIN FETCH stp.station
    WHERE (:trainClass IS NULL OR t.classes LIKE CONCAT('%', :trainClass, '%'))
      AND (
          ds.name = :fromStation OR EXISTS (
              SELECT 1 FROM ScheduleIntermediateStop sis
              WHERE sis.schedule = s AND sis.station.name = :fromStation
          )
      )
      AND (
          ars.name = :toStation OR EXISTS (
              SELECT 1 FROM ScheduleIntermediateStop sis
              WHERE sis.schedule = s AND sis.station.name = :toStation
          )
      )
""")
    List<Schedule> findSchedulesWithRelatedDetailsByClass(
            @Param("fromStation") String fromStation,
            @Param("toStation") String toStation,
            @Param("trainClass") String trainClass,
            @Param("departureDate") LocalDate departureDate
    );






}

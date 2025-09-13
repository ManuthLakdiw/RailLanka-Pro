package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Train;
import lk.ijse.raillankaprobackend.entity.projection.TrainProjection;
import lk.ijse.raillankaprobackend.entity.projection.TrainStationProjection;
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
public interface TrainRepository extends JpaRepository <Train,String> {

    @Query(value = "SELECT train_id FROM train ORDER BY train_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastTrainId();

    Optional<Train> findByName(String name);

    @Query("SELECT t.trainId AS trainId, " +
            "t.name AS name, " +
            "t.category AS category, " +
            "t.active AS active, " +
            "t.trainType AS trainType, " +
            "t.classes AS classes, " +
            "COUNT(s.stationId) AS stopStationCount " +
            "FROM Train t " +
            "LEFT JOIN t.stations s " +
            "GROUP BY t.trainId, t.name, t.category, t.trainType, t.classes, t.active")
    Page<TrainProjection> findAllWithStopCount(Pageable pageable);


    @Query("SELECT t.trainId AS trainId, " +
            "t.name AS name, " +
            "t.category AS category, " +
            "t.trainType AS trainType, " +
            "t.classes AS classes, " +
            "t.active AS active, " +
            "COUNT(s.stationId) AS stopStationCount " +
            "FROM Train t " +
            "LEFT JOIN t.stations s " +
            "WHERE LOWER(t.trainId) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.trainType) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY t.trainId, t.name, t.category, t.trainType, t.classes, t.active")
    Page<TrainProjection> filterTrainsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t.trainId AS trainId, " +
            "t.name AS name, " +
            "t.category AS category, " +
            "t.trainType AS trainType, " +
            "t.classes AS classes, " +
            "t.active AS active, " +
            "COUNT(s.stationId) AS stopStationCount " +
            "FROM Train t " +
            "LEFT JOIN t.stations s " +
            "WHERE LOWER(t.category) = LOWER(:category) " +
            "GROUP BY t.trainId, t.name, t.category, t.trainType, t.classes, t.active")
    Page<TrainProjection> filterTrainsByCategory(@Param("category") String category, Pageable pageable);

    @Query(value = """
    SELECT s.name
    FROM train_station ts
    JOIN station s ON ts.station_id = s.station_id
    WHERE ts.train_id = :trainId
    """, nativeQuery = true)
    List<String> findStationNamesByTrainId(@Param("trainId") String trainId);

    @Query(value = """
    SELECT s.name AS stationName,
           s.station_code AS stationCode,
           s.in_service AS status,
           t.name AS trainName
    FROM train_station ts
    JOIN station s ON ts.station_id = s.station_id
    JOIN train t ON ts.train_id = t.train_id
    WHERE ts.train_id = :trainId
    """, nativeQuery = true)
    List<TrainStationProjection> findStationsWithCodeAndTrainNameByTrainId(@Param("trainId") String trainId);


    @Query(value = """
   SELECT t.train_id AS trainId,
          t.name AS name,
          t.category AS category,
          t.active AS active,
          t.train_type AS trainType,
          t.classes AS classes,
          GROUP_CONCAT(DISTINCT s.name) AS stationNames,
          COUNT(DISTINCT s.station_id) AS stopStationCount,
          g.cargo_type AS cargoType,
          g.`capacity (tons)` AS capacity,
          sp.special_features AS specialFeatures,
          sp.special_train_type AS specialTrainType
   FROM train t
   LEFT JOIN train_station ts ON t.train_id = ts.train_id
   LEFT JOIN station s ON ts.station_id = s.station_id
   LEFT JOIN goods_train g ON g.train_id = t.train_id
   LEFT JOIN special_train sp ON sp.train_id = t.train_id
   WHERE t.train_id = ?1
   GROUP BY t.train_id, t.name, t.category, t.train_type, t.classes, t.active,
            g.cargo_type, g.`capacity (tons)`,
            sp.special_features, sp.special_train_type
   """, nativeQuery = true)
    TrainProjection findTrainProjectionById(String trainId);


    long countTrainByActive(boolean active);

    @Query(value = "SELECT train_type, COUNT(*) FROM train GROUP BY train_type", nativeQuery = true)
    List<Object[]> countTrainsByType();


}

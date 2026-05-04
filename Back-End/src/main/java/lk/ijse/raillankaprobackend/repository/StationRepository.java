package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.projection.StaffProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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


    @Query(value = "SELECT CONCAT(c.firstname, ' ', c.lastname) AS name, " +
            "c.counter_id AS id, " +
            "c.email AS email, " +
            "c.phone_number AS telephone, " +
            "'COUNTER' AS position, " +
            "u.created_date AS joinDate, " +
            "c.dob AS dob, " +
            "CONCAT(s.province, ', ', s.district) AS location, " +
            "c.active AS status " +  // <-- counter active status
            "FROM counter c " +
            "JOIN station s ON c.station_station_id = s.station_id " +
            "JOIN user u ON c.user_id = u.user_id " +
            "WHERE s.name = :stationName " +
            "UNION ALL " +
            "SELECT CONCAT(e.first_name, ' ', e.last_name) AS name, " +
            "e.employee_id AS id, " +
            "e.email AS email, " +
            "e.contact_number AS telephone, " +
            "e.position AS position, " +
            "e.joining_date AS joinDate, " +
            "e.date_of_birth AS dob, " +
            "CONCAT(s.province, ', ', s.district) AS location, " +
            "e.active AS status " +  // <-- employee active status
            "FROM employee e " +
            "JOIN station s ON e.station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "UNION ALL " +
            "SELECT CONCAT(sm.firstname, ' ', sm.lastname) AS name, " +
            "sm.station_master_id AS id, " +
            "sm.email AS email, " +
            "sm.phone_number AS telephone, " +
            "'STATION_MASTER' AS position, " +
            "u.created_date AS joinDate, " +
            "sm.dob AS dob, " +
            "CONCAT(s.province, ', ', s.district) AS location, " +
            "s.in_service AS status " +  // <-- station_master active status
            "FROM station_master sm " +
            "JOIN station s ON sm.station_id = s.station_id " +
            "JOIN user u ON sm.user_id = u.user_id " +
            "WHERE s.name = :stationName",
            nativeQuery = true)
    List<StaffProjection> findAllStaffByStationName(@Param("stationName") String stationName);




    @Query(value = "SELECT CONCAT(c.firstname, ' ', c.lastname) AS name, " +
            "c.counter_id AS id, " +
            "c.email AS email, " +
            "c.phone_number AS telephone, " +
            "'COUNTER' AS position " +
            "FROM counter c " +
            "JOIN station s ON c.station_station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "AND 'COUNTER' = :position " +
            "UNION ALL " +
            "SELECT CONCAT(e.first_name, ' ', e.last_name) AS name, " +
            "e.employee_id AS id, " +
            "e.email AS email, " +
            "e.contact_number AS telephone, " +
            "e.position AS position " +
            "FROM employee e " +
            "JOIN station s ON e.station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "AND e.position = :position " +
            "UNION ALL " +
            "SELECT CONCAT(sm.firstname, ' ', sm.lastname) AS name, " +
            "sm.station_master_id AS id, " +
            "sm.email AS email, " +
            "sm.phone_number AS telephone, " +
            "'STATION_MASTER' AS position " +
            "FROM station_master sm " +
            "JOIN station s ON sm.station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "AND 'STATION_MASTER' = :position",
            nativeQuery = true)
    List<StaffProjection> findStaffByStationAndPosition(@Param("stationName") String stationName, @Param("position") String position);


    @Query(value = "SELECT CONCAT(c.firstname, ' ', c.lastname) AS name, " +
            "c.counter_id AS id, " +
            "c.email AS email, " +
            "c.phone_number AS telephone, " +
            "'COUNTER' AS position " +
            "FROM counter c " +
            "JOIN station s ON c.station_station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "AND (CONCAT(c.firstname, ' ', c.lastname) LIKE %:keyword% " +
            "     OR c.email LIKE %:keyword% " +
            "     OR c.phone_number LIKE %:keyword%) " +

            "UNION ALL " +

            "SELECT CONCAT(e.first_name, ' ', e.last_name) AS name, " +
            "e.employee_id AS id, " +
            "e.email AS email, " +
            "e.contact_number AS telephone, " +
            "e.position AS position " +
            "FROM employee e " +
            "JOIN station s ON e.station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "AND (CONCAT(e.first_name, ' ', e.last_name) LIKE %:keyword% " +
            "     OR e.email LIKE %:keyword% " +
            "     OR e.contact_number LIKE %:keyword%) " +

            "UNION ALL " +

            "SELECT CONCAT(sm.firstname, ' ', sm.lastname) AS name, " +
            "sm.station_master_id AS id, " +
            "sm.email AS email, " +
            "sm.phone_number AS telephone, " +
            "'STATION_MASTER' AS position " +
            "FROM station_master sm " +
            "JOIN station s ON sm.station_id = s.station_id " +
            "WHERE s.name = :stationName " +
            "AND (CONCAT(sm.firstname, ' ', sm.lastname) LIKE %:keyword% " +
            "     OR sm.email LIKE %:keyword% " +
            "     OR sm.phone_number LIKE %:keyword%)",
            nativeQuery = true)
    List<StaffProjection> findStaffByStationAndKeyword(
            @Param("stationName") String stationName,
            @Param("keyword") String keyword
    );


    long countStationByInService(boolean inService);


    @Query("SELECT s.province AS province, COUNT(s) AS stationCount " +
            "FROM Station s " +
            "GROUP BY s.province " +
            "ORDER BY s.province")
    List<Map<String, Object>> countStationsByProvince();


    @Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM station) AS totalStations, " +
            "(SELECT COUNT(DISTINCT station_id) FROM station_master WHERE active = 1) AS stationsWithMasters",
            nativeQuery = true)
    List<Object[]> findTotalAndAssignedStationCounts();


    List<Station> findStationByInService(boolean inService);
}

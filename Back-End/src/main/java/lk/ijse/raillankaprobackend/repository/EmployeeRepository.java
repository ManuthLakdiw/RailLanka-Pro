package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Counter;
import lk.ijse.raillankaprobackend.entity.Dtypes.EmployeePosition;
import lk.ijse.raillankaprobackend.entity.Employee;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.projection.EmployeeCountsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface EmployeeRepository extends JpaRepository <Employee,String> {

    @Query(value = "SELECT employee_id FROM employee ORDER BY employee_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastEmployeeId();

    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.idNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> filterEmployeesByKeyword(@Param("keyword") String keyword, Pageable pageable);


    Page<Employee> findAllByPosition(EmployeePosition position, Pageable pageable);

    List<Employee> findByStation(Station station);

    @Query(value = """
        SELECT
            (SELECT COUNT(*) FROM counter WHERE active = 1) +
            (SELECT COUNT(*) FROM station_master WHERE active = 1) +
            (SELECT COUNT(*) FROM employee WHERE active = 1) AS active_count,
            
            (SELECT COUNT(*) FROM counter WHERE active = 0) +
            (SELECT COUNT(*) FROM station_master WHERE active = 0) +
            (SELECT COUNT(*) FROM employee WHERE active = 0) AS inactive_count,
            
            (SELECT COUNT(*) FROM counter) +
            (SELECT COUNT(*) FROM station_master) +
            (SELECT COUNT(*) FROM employee) AS total_count
        """, nativeQuery = true)
    EmployeeCountsProjection getEmployeeCounts();

    @Query(value = """
        SELECT position AS role, COUNT(*) AS count
        FROM (
            SELECT position FROM employee
            UNION ALL
            SELECT 'Counter' AS position FROM counter
            UNION ALL
            SELECT 'Station Master' AS position FROM station_master
        ) AS all_employees
        GROUP BY position
    """, nativeQuery = true)
    List<Object[]> countEmployeesByRole();

    List<Employee> findEmployeeByActive(boolean active);
}

package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Counter;
import lk.ijse.raillankaprobackend.entity.Dtypes.EmployeePosition;
import lk.ijse.raillankaprobackend.entity.Employee;
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
}

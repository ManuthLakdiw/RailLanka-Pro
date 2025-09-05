package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */


public interface EmployeeService {

    String generateNewEmployeeId();

    String registerEmployee(EmployeeDto employeeDto);

    String formatName(String name);

    Page<EmployeeDto> getAllEmployees(int pageNo, int pageSize);

    String changeEmployeeStatus(String employeeId, boolean status);

    String deleteEmployee(String employeeId);

    Page<EmployeeDto> filterEmployeesByKeyword(String keyword, int pageNo, int pageSize);

    Page<EmployeeDto> filterEmployeesByPosition(String position, int pageNo, int pageSize);

    EmployeeDto getEmployeeDetailsByEmployeeId(String employeeId);

    String updateEmployeeDetails(EmployeeDto employeeDto);

}

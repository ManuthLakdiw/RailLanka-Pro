package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.EmployeeDto;
import lk.ijse.raillankaprobackend.entity.Dtypes.EmployeePosition;
import lk.ijse.raillankaprobackend.entity.Employee;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.projection.EmployeeCountsProjection;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.EmployeeRepository;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StationRepository stationRepository;

    @Override
    public String generateNewEmployeeId() {
        if (employeeRepository.getLastEmployeeId().isPresent()) {
            String lastEmployeeId = employeeRepository.getLastEmployeeId().get();
            String[] split = lastEmployeeId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999) {
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999) {
                    throw new IdGenerateLimitReachedException("All available Employee IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("EMP%05d-%05d", prefixNumber, suffixNumber);
        }
        return "EMP00000-00001";
    }

    @Transactional
    @Override
    public String registerEmployee(EmployeeDto employeeDto) {

        Station station = stationRepository.findByName(
                employeeDto.getStation()).orElseThrow(() -> new RuntimeException("Station not found"));

        Employee employee = Employee.builder()
                .employeeId(generateNewEmployeeId())
                .firstName(formatName(employeeDto.getFirstname()))
                .lastName(formatName(employeeDto.getLastname()))
                .email(employeeDto.getEmail())
                .idNumber(employeeDto.getIdNumber())
                .contactNumber(employeeDto.getContactNumber())
                .address(employeeDto.getAddress())
                .position(EmployeePosition.valueOf(employeeDto.getPosition()))
                .dateOfBirth(employeeDto.getDateOfBirth())
                .joiningDate(LocalDate.now())
                .active(true)
                .station(station)
                .build();

        employeeRepository.save(employee);

        return "Employee has been registered successfully.";
    }

    @Override
    public String formatName(String name) {

        if (name == null) {
            throw new RuntimeException("Name cannot be null");
        }

        return name.substring(0, 1).toUpperCase() +
                name.substring(1).toLowerCase();
    }

    @Override
    public Page<EmployeeDto> getAllEmployees(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        return getEmployeeDtos(employeePage);

    }

    @Override
    public String changeEmployeeStatus(String employeeId, boolean status) {
        Employee employee = employeeRepository.findById(employeeId).
                orElseThrow(() -> new RuntimeException("Employee ID not found"));
        employee.setActive(status);
        employeeRepository.save(employee);

        return "Employee has been successfully set to " + (status ? "Active" : "Inactive");
    }

    @Override
    public String deleteEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee ID not found"));
        employeeRepository.delete(employee);

        return "Employee has been successfully deleted.";
    }

    @Override
    public Page<EmployeeDto> filterEmployeesByKeyword(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Employee> employeePage = employeeRepository.filterEmployeesByKeyword(keyword,pageable);

        return getEmployeeDtos(employeePage);

    }

    @Override
    public Page<EmployeeDto> filterEmployeesByPosition(String position, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Employee> allByPosition = employeeRepository.findAllByPosition(EmployeePosition.valueOf(position), pageable);

        return getEmployeeDtos(allByPosition);
    }

    @Override
    public EmployeeDto getEmployeeDetailsByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findById(
                employeeId).orElseThrow(() -> new RuntimeException("Employee ID not found"));

        return EmployeeDto.builder()
                .employeeId(employee.getEmployeeId())
                .firstname(employee.getFirstName())
                .lastname(employee.getLastName())
                .email(employee.getEmail())
                .contactNumber(employee.getContactNumber())
                .dateOfBirth(employee.getDateOfBirth())
                .address(employee.getAddress())
                .idNumber(employee.getIdNumber())
                .active(employee.isActive())
                .position(employee.getPosition().name())
                .station(employee.getStation().getName())
                .build();
    }

    @Override
    public String updateEmployeeDetails(EmployeeDto employeeDto) {
        if (employeeRepository.findById(employeeDto.getEmployeeId()).isPresent()){
            Employee employee = employeeRepository.findById(employeeDto.getEmployeeId()).get();
            employee.setFirstName(formatName(employeeDto.getFirstname()));
            employee.setLastName(formatName(employeeDto.getLastname()));
            employee.setEmail(employeeDto.getEmail());
            employee.setIdNumber(employeeDto.getIdNumber());
            employee.setContactNumber(employeeDto.getContactNumber());
            employee.setAddress(employeeDto.getAddress());
            employee.setPosition(EmployeePosition.valueOf(employeeDto.getPosition()));
            System.out.println(employeeDto.getDateOfBirth());
            employee.setDateOfBirth(employeeDto.getDateOfBirth());
            employee.setActive(employeeDto.isActive());
            employee.setStation(stationRepository.findByName(employeeDto.getStation())
                    .orElseThrow(() -> new RuntimeException("Station not found")));

            employeeRepository.save(employee);

            return "Employee details has been updated successfully.";
        }
        throw new RuntimeException("Employee ID not found") ;
    }

    @Override
    public Map<String, Long> getEmployeeCounts() {
        EmployeeCountsProjection employeeCounts = employeeRepository.getEmployeeCounts();
        return Map.of(
                "total",employeeCounts.getTotalCount(),
                "active",employeeCounts.getActiveCount(),
                "inactive",employeeCounts.getInactiveCount()
        );
    }

    @Override
    public Map<String, Long> getEmployeeCountByRole() {
        List<Object[]> results = employeeRepository.countEmployeesByRole();
        Map<String, Long> roleCounts = new HashMap<>();
        for (Object[] row : results) {
            String role = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            roleCounts.put(role, count);
        }
        return roleCounts;
    }


    private Page<EmployeeDto> getEmployeeDtos(Page<Employee> employeePage) {
        return employeePage.map(employee -> {
            String contactNumber = employee.getContactNumber();
            String formattedContactNumber = contactNumber.substring(0, 3) + "-" + contactNumber.substring(3);

            return EmployeeDto.builder()
                    .employeeId(employee.getEmployeeId())
                    .firstname(employee.getFirstName())
                    .lastname(employee.getLastName())
                    .email(employee.getEmail())
                    .contactNumber(formattedContactNumber)
                    .dateOfBirth(employee.getDateOfBirth())
                    .address(employee.getAddress())
                    .idNumber(employee.getIdNumber())
                    .active(employee.isActive())
                    .position(employee.getPosition().name())
                    .station(employee.getStation().getName() + "," + employee.getStation().getStationCode())
                    .build();
        });
    }



}

package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.EmployeeDto;
import lk.ijse.raillankaprobackend.service.EmployeeService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lk.ijse.raillankaprobackend.util.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/api/v1/raillankapro/employee")
@CrossOrigin
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("register")
    public ResponseEntity<ApiResponse<String>> registerEmployee(@RequestBody EmployeeDto employeeDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "employee registered",
                employeeService.registerEmployee(employeeDto)

        ),HttpStatus.CREATED);
    }

    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<EmployeeDto>>> getAllCounters(@PathVariable int pageNo, @PathVariable int pageSize) {
        Page<EmployeeDto> allEmployees = employeeService.getAllEmployees(pageNo, pageSize);

        int startNumber = allEmployees.getNumber() * allEmployees.getSize() + 1;
        int endNumber = Math.min(startNumber + allEmployees.getSize() - 1, (int) allEmployees.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<EmployeeDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched employees")
                        .currentPage(allEmployees.getNumber()+1)
                        .totalItems(allEmployees.getTotalElements())
                        .totalPages(allEmployees.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allEmployees.getContent())
                        .build());

    }

    @PutMapping("/changestatus/{employeeId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeCounterStatus(@PathVariable String employeeId, @PathVariable boolean status){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                employeeService.changeEmployeeStatus(employeeId, status),
                status
        ));
    }

    @PutMapping(value = "delete" , params = "id")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@RequestParam("id") String employeeId){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "employee deleted",
                employeeService.deleteEmployee(employeeId)
        ));
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<PaginatedResponse<List<EmployeeDto>>> getStationsByKeyword(
            @RequestParam("keyword") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<EmployeeDto> allPaginatedEmployees = employeeService.filterEmployeesByKeyword(keyword, pageNo, pageSize);
        int startNumber = allPaginatedEmployees.getNumber() * allPaginatedEmployees.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedEmployees.getSize() - 1, (int) allPaginatedEmployees.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<EmployeeDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered employees")
                        .currentPage(allPaginatedEmployees.getNumber()+1)
                        .totalItems(allPaginatedEmployees.getTotalElements())
                        .totalPages(allPaginatedEmployees.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedEmployees.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/filter/by/position/{pageNo}/{pageSize}" , params = "position")
    public ResponseEntity<PaginatedResponse<List<EmployeeDto>>> getStationsByPosition(
            @RequestParam("position") String position,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<EmployeeDto> allPaginatedEmployees = employeeService.filterEmployeesByPosition(position, pageNo, pageSize);
        int startNumber = allPaginatedEmployees.getNumber() * allPaginatedEmployees.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedEmployees.getSize() - 1, (int) allPaginatedEmployees.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<EmployeeDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered employees")
                        .currentPage(allPaginatedEmployees.getNumber()+1)
                        .totalItems(allPaginatedEmployees.getTotalElements())
                        .totalPages(allPaginatedEmployees.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedEmployees.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/getemployee" , params = {"id"})
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(@RequestParam("id") String employeeId){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "employee fetched",
                employeeService.getEmployeeDetailsByEmployeeId(employeeId)
        ));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateEmployee(@RequestBody EmployeeDto employeeDto){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "employee updated",
                employeeService.updateEmployeeDetails(employeeDto)
        ));
    }


}

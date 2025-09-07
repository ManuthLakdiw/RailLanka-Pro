package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.service.PassengerService;
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
@RequestMapping("/api/v1/raillankapro/passenger")
@CrossOrigin
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<PassengerDto>>> getAllPassengers(@PathVariable int pageNo, @PathVariable int pageSize) {
        Page<PassengerDto> allPassengers = passengerService.getAllPassengers(pageNo, pageSize);

        int startNumber = allPassengers.getNumber() * allPassengers.getSize() + 1;
        int endNumber = Math.min(startNumber + allPassengers.getSize() - 1, (int) allPassengers.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<PassengerDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched passengers")
                        .currentPage(allPassengers.getNumber()+1)
                        .totalItems(allPassengers.getTotalElements())
                        .totalPages(allPassengers.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPassengers.getContent())
                        .build());

    }

    @PutMapping("/changestatus/{passengerId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeCounterStatus(@PathVariable String passengerId, @PathVariable boolean status){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                passengerService.changePassengerStatus(passengerId, status),
                status
        ));
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<PaginatedResponse<List<PassengerDto>>> getPassengersByKeyword(
            @RequestParam("keyword") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<PassengerDto> allPaginatedPassengers = passengerService.filterPassengerByKeyword(keyword, pageNo, pageSize);
        int startNumber = allPaginatedPassengers.getNumber() * allPaginatedPassengers.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedPassengers.getSize() - 1, (int) allPaginatedPassengers.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<PassengerDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered passengers")
                        .currentPage(allPaginatedPassengers.getNumber()+1)
                        .totalItems(allPaginatedPassengers.getTotalElements())
                        .totalPages(allPaginatedPassengers.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedPassengers.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "status")
    public ResponseEntity<PaginatedResponse<List<PassengerDto>>> getPassengersByStatus(
            @RequestParam("status") String status,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<PassengerDto> allPaginatedPassengers = passengerService
                .filterPassengerByStatus(Boolean.parseBoolean(status), pageNo, pageSize);
        int startNumber = allPaginatedPassengers.getNumber() * allPaginatedPassengers.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedPassengers.getSize() - 1, (int) allPaginatedPassengers.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<PassengerDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered passengers")
                        .currentPage(allPaginatedPassengers.getNumber()+1)
                        .totalItems(allPaginatedPassengers.getTotalElements())
                        .totalPages(allPaginatedPassengers.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedPassengers.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "type")
    public ResponseEntity<PaginatedResponse<List<PassengerDto>>> getPassengersByPassengerType(
            @RequestParam("type") String type,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<PassengerDto> allPaginatedPassengers = passengerService
                .filterPassengerByPassengerType(type, pageNo, pageSize);
        int startNumber = allPaginatedPassengers.getNumber() * allPaginatedPassengers.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedPassengers.getSize() - 1, (int) allPaginatedPassengers.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<PassengerDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered passengers")
                        .currentPage(allPaginatedPassengers.getNumber()+1)
                        .totalItems(allPaginatedPassengers.getTotalElements())
                        .totalPages(allPaginatedPassengers.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedPassengers.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/getpassenger" , params = {"id"})
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerById(@RequestParam("id") String id){
        return  ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched by " + id + " id",
                passengerService.getPassengerDetailsByPassengerId(id)
        ));
    }




}

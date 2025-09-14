package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.service.CounterService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lk.ijse.raillankaprobackend.util.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@RestController
@RequestMapping("/api/v1/raillankapro/counter")
@CrossOrigin
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @GetMapping("/get/counternumbers/by/stationname/{stationName}")
    public ResponseEntity<ApiResponse<List<String>>> getCounterNumbersByStationName(@PathVariable String stationName){
         return ResponseEntity.ok(new ApiResponse<>(
                 200,
                 "counter number list",
                 counterService.getCounterNumberByStationName(stationName)

         ));
    }


    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<CounterDto>>> getAllCounters(@PathVariable int pageNo, @PathVariable int pageSize) {
        Page<CounterDto> allCounters = counterService.getAllCounters(pageNo, pageSize);

        int startNumber = allCounters.getNumber() * allCounters.getSize() + 1;
        int endNumber = Math.min(startNumber + allCounters.getSize() - 1, (int) allCounters.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<CounterDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched counters")
                        .currentPage(allCounters.getNumber()+1)
                        .totalItems(allCounters.getTotalElements())
                        .totalPages(allCounters.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allCounters.getContent())
                        .build());

    }

    @PutMapping("/changestatus/{counterId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeCounterStatus(@PathVariable String counterId, @PathVariable boolean status){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                counterService.changeCounterStatus(counterId, status),
                status
        ));
    }

    @PutMapping(value = "/delete" , params = {"id"})
    public ResponseEntity<ApiResponse<String>> deleteCounter(@RequestParam("id") String id){
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "deleted counter",
                counterService.deleteCounter(id)
        ));
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<PaginatedResponse<List<CounterDto>>> getStationsByKeyword(
            @RequestParam("keyword") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<CounterDto> allPaginatedCounters = counterService.filterCountersByKeyword(keyword, pageNo, pageSize);
        int startNumber = allPaginatedCounters.getNumber() * allPaginatedCounters.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedCounters.getSize() - 1, (int) allPaginatedCounters.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<CounterDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered counters")
                        .currentPage(allPaginatedCounters.getNumber()+1)
                        .totalItems(allPaginatedCounters.getTotalElements())
                        .totalPages(allPaginatedCounters.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedCounters.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/getcounter" , params = {"id"})
    public ResponseEntity<ApiResponse<Optional<CounterDto>>> getCounterById(@RequestParam("id") String id){
        return  ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched by " + id + " id",
                counterService.findCounterById(id)
        ));
    }

    @PutMapping("update")
    public ResponseEntity<ApiResponse<String>> updateCounterDetails(@RequestBody CounterDto counterDto){
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Counter updated",
                counterService.updateCounterDetails(counterDto)
        ), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCounterStaffCount(){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "counter staff count",
                counterService.getCounterStaffCount()
                ));
    }

    @GetMapping("/count/by/province")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> findCounterCountByProvince() {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "counter staff count by province",
                counterService.getCounterCountByProvince()
        ));
    }

}

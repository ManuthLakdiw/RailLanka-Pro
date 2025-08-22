package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.service.CounterService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
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
}

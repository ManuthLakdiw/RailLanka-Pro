package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.service.StationService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@RestController
@RequestMapping("/api/v1/raillankapro/station")
@CrossOrigin
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping("register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody StationDto stationDto) {
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Station registration",
                stationService.registerStation(stationDto)
        ), HttpStatus.CREATED);

    }
}

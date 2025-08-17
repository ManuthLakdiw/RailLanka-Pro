package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.service.StationService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lk.ijse.raillankaprobackend.util.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@RestController
@RequestMapping("/api/v1/raillankapro/station")
@CrossOrigin()
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody StationDto stationDto) {
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Station registration",
                stationService.registerStation(stationDto)
        ), HttpStatus.CREATED);

    }

    @GetMapping("/getAll/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<StationDto>>> getAllStations(@PathVariable int pageNo, @PathVariable int pageSize){
        Page<StationDto> allPaginatedStations = stationService.getAllStations(pageNo, pageSize);

        int startNumber = allPaginatedStations.getNumber() * allPaginatedStations.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedStations.getSize() - 1, (int) allPaginatedStations.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<StationDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched stations")
                        .currentPage(allPaginatedStations.getNumber()+1)
                        .totalItems(allPaginatedStations.getTotalElements())
                        .totalPages(allPaginatedStations.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedStations.getContent())
                        .build()
        );

    }

    @PutMapping("/changeInServiceStatus/{stationId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeInServiceStatus(@PathVariable String stationId, @PathVariable boolean status){
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                stationService.changeStationInServiceStatus(stationId, status),
                status

        ), HttpStatus.OK);
    }

    @GetMapping(value = "/getStationById" , params = {"id"})
    public ResponseEntity<ApiResponse<Optional<StationDto>>> getStationById (@RequestParam("id") String id) {
            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "fetched by " + id + " id",
                    stationService.findStationById(id)
            ));
    }

    @PutMapping("update")
    public ResponseEntity<ApiResponse<String>> updateStationDetails(@RequestBody StationDto stationDto){
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Station updated",
                stationService.updateStationDetails(stationDto)
        ), HttpStatus.OK);
    }

    @PutMapping(value = "/delete" , params = {"id"})
    public ResponseEntity<ApiResponse<String>> deleteStation(@RequestParam("id") String id){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Station deleted",
                stationService.deleteStation(id)
        ));
    }

}

package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.entity.projection.StaffProjection;
import lk.ijse.raillankaprobackend.service.StationService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lk.ijse.raillankaprobackend.util.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<ApiResponse<List<StationDto>>> getStationsByKeyword(
            @RequestParam("keyword") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<StationDto> allPaginatedStations = stationService.filterStationsByKeyword(keyword, pageNo, pageSize);
        int startNumber = allPaginatedStations.getNumber() * allPaginatedStations.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedStations.getSize() - 1, (int) allPaginatedStations.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<StationDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered stations")
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

    @GetMapping("/getall/names/and/codes")
    public ResponseEntity<ApiResponse<List<StationDto>>> getAllStationNamesAndCodes(){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched all station names and codes",
                stationService.getAllStationNamesAndCodes()
        ));
    }


    @GetMapping(value = "/getall/staff/by" , params ="station")
    public ResponseEntity<ApiResponse<List<StaffProjection>>> getStaffByStation(@RequestParam("station") String station){
       return ResponseEntity.ok(new ApiResponse<>(
               200,
               "fetched staff by station",
               stationService.getStaffByStation(station)
       ));
    }

    @GetMapping(value = "/getall/staff/by" , params ={"station","position"})
    public ResponseEntity<ApiResponse<List<StaffProjection>>> getStaffByStation(
            @RequestParam("station") String station , @RequestParam("position") String position){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched staff by station and position",
                stationService.getStaffByStationAndPosition(station , position)
        ));
    }

    @GetMapping(value = "/getall/staff/by" , params ={"station","keyword"})
    public ResponseEntity<ApiResponse<List<StaffProjection>>> getStaffByStationAndKeyword(
            @RequestParam("station") String station ,
            @RequestParam("keyword") String keyword ){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched staff by station and keyword",
                stationService.getStaffByStationAndKeyword(station ,keyword)
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCountOfStations(){
        Map<String,Long> count = new HashMap<>();
        count.put("total",stationService.getNumberOfStations());
        count.put("inService",stationService.getNumberOfInServiceStations());
        count.put("outService",stationService.getNumberOfOutServiceStations());

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "station count",
                count
        ));
    }


    @GetMapping("/count/by/province")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCountOfStationsByProvince(){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "station count",
                stationService.countStationsByProvince()
        ));
    }

    @GetMapping("/total/and/stationmaster/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTotalAndAssignedStationCounts() {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "find total and assigned station master count",
                stationService.findTotalAndAssignedStationCounts()
        ));
    }



}

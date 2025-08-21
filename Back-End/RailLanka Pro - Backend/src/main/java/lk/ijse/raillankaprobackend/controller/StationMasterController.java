package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.service.StationMasterService;
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
@RequestMapping("/api/v1/raillankapro/stationmaster")
@CrossOrigin
@RequiredArgsConstructor
public class StationMasterController {

    private final StationMasterService stationMasterService;

    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<StaffDto>>> getAllStationMasters(@PathVariable int pageNo, @PathVariable int pageSize) {
        Page<StaffDto> allStationMasters = stationMasterService.getAllStationMasters(pageNo, pageSize);

        int startNumber = allStationMasters.getNumber() * allStationMasters.getSize() + 1;
        int endNumber = Math.min(startNumber + allStationMasters.getSize() - 1, (int) allStationMasters.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<StaffDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched stations")
                        .currentPage(allStationMasters.getNumber()+1)
                        .totalItems(allStationMasters.getTotalElements())
                        .totalPages(allStationMasters.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allStationMasters.getContent())
                        .build());

    }

    @GetMapping("/getall/assigned/stations")
    public ResponseEntity<List<String>> getAllAssignedStations(){
        return ResponseEntity.ok(stationMasterService.getAllAssignedStations());
    }

    @PutMapping("/changestatus/{stationId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeStationMasterStatus(@PathVariable String stationId, @PathVariable boolean status){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                stationMasterService.changeStationMasterStatus(stationId, status),
                status
        ));
    }

    @PutMapping(value = "/delete" , params = {"id"})
    public ResponseEntity<ApiResponse<String>> deleteStationMaster(@RequestParam("id") String id){
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "deleted station master",
                stationMasterService.deleteStationMaster(id)
        ));
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<PaginatedResponse<List<StaffDto>>> getStationsByKeyword(
            @RequestParam("keyword") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<StaffDto> allPaginatedStations = stationMasterService.filterStationMastersByKeyword(keyword, pageNo, pageSize);
        int startNumber = allPaginatedStations.getNumber() * allPaginatedStations.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedStations.getSize() - 1, (int) allPaginatedStations.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<StaffDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered station masters")
                        .currentPage(allPaginatedStations.getNumber()+1)
                        .totalItems(allPaginatedStations.getTotalElements())
                        .totalPages(allPaginatedStations.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedStations.getContent())
                        .build()
        );
    }

    @GetMapping(value = "/getstationmaster" , params = {"id"})
    public ResponseEntity<ApiResponse<Optional<StaffDto>>> getStationMasterById(@RequestParam("id") String id){
       return  ResponseEntity.ok(new ApiResponse<>(
               200,
               "fetched by " + id + " id",
               stationMasterService.findStationMasterById(id)
       ));
    }

    @PutMapping("update")
    public ResponseEntity<ApiResponse<String>> updateStationMasterDetails(@RequestBody StaffDto staffDto){
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Station updated",
                stationMasterService.updateStationMasterDetails(staffDto)
        ), HttpStatus.OK);
    }






}

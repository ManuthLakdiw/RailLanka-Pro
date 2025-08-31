package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.TrainDto;
import lk.ijse.raillankaprobackend.service.TrainService;
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
@RequestMapping("/api/v1/raillankapro/train")
@CrossOrigin
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody TrainDto trainDto){

        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Train Registered",
                trainService.registerTrain(trainDto)

        ), HttpStatus.CREATED);
    }

    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<TrainDto>>> getAllStations(@PathVariable int pageNo, @PathVariable int pageSize){
        Page<TrainDto> allPaginatedTrains = trainService.getAllTrainsAndStopStationCount(pageNo, pageSize);

        int startNumber = allPaginatedTrains.getNumber() * allPaginatedTrains.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedTrains.getSize() - 1, (int) allPaginatedTrains.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<TrainDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched trains")
                        .currentPage(allPaginatedTrains.getNumber()+1)
                        .totalItems(allPaginatedTrains.getTotalElements())
                        .totalPages(allPaginatedTrains.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedTrains.getContent())
                        .build()
        );

    }

    @PutMapping("/changestatus/{trainId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeCounterStatus(@PathVariable String trainId, @PathVariable boolean status){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                trainService.changeTrainStatus(trainId, status),
                status
        ));
    }

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<ApiResponse<String>> deleteTrain(@RequestParam("id") String trainId){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "train deleted",
                trainService.deleteTrain(trainId)
        ));
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<PaginatedResponse<List<TrainDto>>> getTrainsByKeyword(
            @RequestParam("keyword") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<TrainDto> allPaginatedTrains = trainService.filterTrainsByKeyword(keyword, pageNo, pageSize);
        int startNumber = allPaginatedTrains.getNumber() * allPaginatedTrains.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedTrains.getSize() - 1, (int) allPaginatedTrains.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<TrainDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched trains")
                        .currentPage(allPaginatedTrains.getNumber()+1)
                        .totalItems(allPaginatedTrains.getTotalElements())
                        .totalPages(allPaginatedTrains.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedTrains.getContent())
                        .build()
        );

    }

    @GetMapping(value = "/filter/by/category/{pageNo}/{pageSize}" , params = "category")
    public ResponseEntity<PaginatedResponse<List<TrainDto>>> getTrainsByCategory(
            @RequestParam("category") String keyword,
            @PathVariable  int pageNo,
            @PathVariable int pageSize){

        Page<TrainDto> allPaginatedTrains = trainService.filterTrainsByCategory(keyword, pageNo, pageSize);
        int startNumber = allPaginatedTrains.getNumber() * allPaginatedTrains.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedTrains.getSize() - 1, (int) allPaginatedTrains.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<TrainDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched trains")
                        .currentPage(allPaginatedTrains.getNumber()+1)
                        .totalItems(allPaginatedTrains.getTotalElements())
                        .totalPages(allPaginatedTrains.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedTrains.getContent())
                        .build()
        );

    }

    @GetMapping(value = "get/station/by/train" , params = "id")
    public ResponseEntity<ApiResponse<List<String>>> getStationNamesByTrainId(@RequestParam("id") String id) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched station names from train id",
                trainService.getStopingStationNamesByTrainId(id)
        ));
    }

    @GetMapping(value = "get/train/by" , params = "id")
    public ResponseEntity<ApiResponse<TrainDto>> getTrainById(@RequestParam("id") String id) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched train by id",
                trainService.getTrainsAndStationDetailsByTrainId(id)
        ));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody TrainDto trainDto){

        return new ResponseEntity<>(new ApiResponse<>(
                200,
                "Train Updated",
                trainService.updateTrainDetails(trainDto)

        ), HttpStatus.OK);
    }


}

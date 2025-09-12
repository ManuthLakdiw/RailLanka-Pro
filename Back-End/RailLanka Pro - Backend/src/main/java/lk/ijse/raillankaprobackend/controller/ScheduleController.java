package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.ScheduleDto;
import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.service.ScheduleService;
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
@RequestMapping("/api/v1/raillankapro/schedule")
@CrossOrigin
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody ScheduleDto scheduleDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "schedule registration",
                scheduleService.registerSchedule(scheduleDto)

        ), HttpStatus.CREATED);
    }

    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<PaginatedResponse<List<ScheduleDto>>> getAllSchedule(@PathVariable int pageNo, @PathVariable int pageSize){
        Page<ScheduleDto> schedulePage = scheduleService.getAllSchedule(pageNo, pageSize);

        int startNumber = schedulePage.getNumber() * schedulePage.getSize() + 1;
        int endNumber = Math.min(startNumber + schedulePage.getSize() - 1, (int) schedulePage.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<ScheduleDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched schedules")
                        .currentPage(schedulePage.getNumber()+1)
                        .totalItems(schedulePage.getTotalElements())
                        .totalPages(schedulePage.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(schedulePage.getContent())
                        .build()
        );
    }

    @PutMapping("/change/status/{scheduleId}/{status}")
    public ResponseEntity<ApiResponse<Boolean>> changeScheduleStatus(@PathVariable String scheduleId, @PathVariable boolean status){
        return new ResponseEntity<>(new ApiResponse<>(
                200,
                scheduleService.changeScheduleStatus(scheduleId, status),
                status

        ), HttpStatus.OK);
    }

    @GetMapping(value = "get/by" , params = "scheduleid")
    public ResponseEntity<ApiResponse<ScheduleDto>> getScheduleById(@RequestParam("scheduleid") String scheduleId){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "get by id",
                scheduleService.getScheduleDetailsByScheduleId(scheduleId)
        ));
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "keyword")
    public ResponseEntity<PaginatedResponse<List<ScheduleDto>>> getFilteredScheduleByKeyword(
            @PathVariable int pageNo,
            @PathVariable int pageSize,
            @RequestParam("keyword") String keyword
    ){
        Page<ScheduleDto> schedulePage = scheduleService.filterScheduleByKeyWord(keyword,pageNo, pageSize);

        return getPaginatedResponseResponseEntity(schedulePage);
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "status")
    public ResponseEntity<PaginatedResponse<List<ScheduleDto>>> getFilteredScheduleByStatus(
            @PathVariable int pageNo,
            @PathVariable int pageSize,
            @RequestParam("status") boolean status
    ){
        Page<ScheduleDto> schedulePage = scheduleService.filterScheduleByStatus(status,pageNo, pageSize);

        return getPaginatedResponseResponseEntity(schedulePage);
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "train")
    public ResponseEntity<PaginatedResponse<List<ScheduleDto>>> getFilteredScheduleByTrainName(
            @PathVariable int pageNo,
            @PathVariable int pageSize,
            @RequestParam("train") String train
    ){
        Page<ScheduleDto> schedulePage = scheduleService.filterScheduleByTrainName(train,pageNo, pageSize);

        return getPaginatedResponseResponseEntity(schedulePage);
    }

    @GetMapping(value = "/filter/{pageNo}/{pageSize}" , params = "frequency")
    public ResponseEntity<PaginatedResponse<List<ScheduleDto>>> getFilteredScheduleByFrequency(
            @PathVariable int pageNo,
            @PathVariable int pageSize,
            @RequestParam("frequency") String frequency
    ){
        Page<ScheduleDto> schedulePage = scheduleService.filterScheduleByFrequency(frequency,pageNo, pageSize);

        return getPaginatedResponseResponseEntity(schedulePage);
    }

    private ResponseEntity<PaginatedResponse<List<ScheduleDto>>> getPaginatedResponseResponseEntity(Page<ScheduleDto> schedulePage) {
        int startNumber = schedulePage.getNumber() * schedulePage.getSize() + 1;
        int endNumber = Math.min(startNumber + schedulePage.getSize() - 1, (int) schedulePage.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<ScheduleDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched filtered schedules")
                        .currentPage(schedulePage.getNumber()+1)
                        .totalItems(schedulePage.getTotalElements())
                        .totalPages(schedulePage.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(schedulePage.getContent())
                        .build()
        );
    }


    @PutMapping(value = "delete", params = "id")
    public ResponseEntity<ApiResponse<String>> deleteSchedule(@RequestParam("id") String scheduleId){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "schedule deleted",
                scheduleService.deleteSchedule(scheduleId)
        ));
    }

}

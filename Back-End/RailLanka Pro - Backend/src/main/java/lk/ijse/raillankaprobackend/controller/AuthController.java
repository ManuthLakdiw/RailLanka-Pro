package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.*;
import lk.ijse.raillankaprobackend.service.*;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lk.ijse.raillankaprobackend.util.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/api/v1/raillankapro/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final PassengerService passengerService;
    private final AdminService adminService;
    private final StationMasterService stationMasterService;
    private final CounterService counterService;
    private final AuthService authService;
    private final StationService stationService;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;
    private final  FirstClassBookingSeatService firstClassBookingSeatService;
    private final PDFService pdfService;


    @PostMapping("/register/passenger")
    public ResponseEntity<ApiResponse<String>> registerPassenger(@RequestBody PassengerDto passengerDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Passenger Registration",
                passengerService.registerPassenger(passengerDto)
        ), HttpStatus.CREATED);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<String>> registerAdmin(@RequestBody StaffDto staffDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Admin Registration",
                adminService.registerAdmin(staffDto)
        ),HttpStatus.CREATED);
    }

    @PostMapping("/register/stationmaster")
    public ResponseEntity<ApiResponse<String>> registerStationMaster(@RequestBody StaffDto staffDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "StationMaster Registration",
                stationMasterService.registerStationMaster(staffDto)
        ),HttpStatus.CREATED);
    }

    @PostMapping("/register/counter")
    public ResponseEntity<ApiResponse<String>> registerCounter(@RequestBody CounterDto counterDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Counter Registration",
                counterService.registerCounter(counterDto)
        ),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody AuthDto authDto) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "login",
                authService.authenticate(authDto)
        ));
    }

    @PostMapping("refreshtoken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return  ResponseEntity.ok(new ApiResponse(
                200,
                "OK",
                authService.reGenerateAccessTokenUsingRefreshToken(refreshTokenDto.getToken())
        ));
    }

    @PostMapping(value = "/reset/password/verify" , params = "email")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestParam("email") String email) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "otp send",
                authService.sendVerificationCode(email)
        ));

    }

    @PostMapping(value = "/reset/password/verify/code" , params = {"email","code"})
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestParam("email") String email , @RequestParam("code") String code) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "code verification",
                authService.verifyVerificationCode(email,code)
        ));

    }

    @PostMapping(value = "/reset/password")
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(@RequestBody AuthDto authDto) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "reset password",
                authService.resetPassword(authDto)
        ));

    }

    @GetMapping("/stations")
    public ResponseEntity<ApiResponse<List<StationDto>>> getAllStations(){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetched All Stations",
                stationService.getAllStationNamesAndCodes()
        ));

    }


    @PostMapping(value = "/schedules/{pageNo}/{pageSize}", params = "train")
    public ResponseEntity<ApiResponse<List<TrainScheduleInfoDto>>> searchSchedulesByTrainName(
            @RequestBody SearchTrainDto searchTrainDto,
            @RequestParam("train") String trainName,
            @PathVariable int pageNo,
            @PathVariable int pageSize){
        Page<TrainScheduleInfoDto> allPaginatedDetails = scheduleService.searchSchedulesByTrainName(searchTrainDto,trainName,pageNo, pageSize);

        int startNumber = allPaginatedDetails.getNumber() * allPaginatedDetails.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedDetails.getSize() - 1, (int) allPaginatedDetails.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<TrainScheduleInfoDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched all details by train name")
                        .currentPage(allPaginatedDetails.getNumber()+1)
                        .totalItems(allPaginatedDetails.getTotalElements())
                        .totalPages(allPaginatedDetails.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedDetails.getContent())
                        .build()
        );

    }

    @PostMapping(value = "/schedules/{pageNo}/{pageSize}", params = "class")
    public ResponseEntity<ApiResponse<List<TrainScheduleInfoDto>>> searchSchedulesByTrainClass(
            @RequestBody SearchTrainDto searchTrainDto,
            @RequestParam("class") String trainClass,
            @PathVariable int pageNo,
            @PathVariable int pageSize){
        Page<TrainScheduleInfoDto> allPaginatedDetails = scheduleService.searchSchedulesByTrainClass(searchTrainDto,trainClass,pageNo, pageSize);

        int startNumber = allPaginatedDetails.getNumber() * allPaginatedDetails.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedDetails.getSize() - 1, (int) allPaginatedDetails.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<TrainScheduleInfoDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched all details by train name")
                        .currentPage(allPaginatedDetails.getNumber()+1)
                        .totalItems(allPaginatedDetails.getTotalElements())
                        .totalPages(allPaginatedDetails.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedDetails.getContent())
                        .build()
        );

    }

    @PostMapping("/schedules/{pageNo}/{pageSize}")
    public ResponseEntity<ApiResponse<List<TrainScheduleInfoDto>>> searchAllSchedules(
            @RequestBody SearchTrainDto searchTrainDto,
            @PathVariable int pageNo,
            @PathVariable int pageSize){
        Page<TrainScheduleInfoDto> allPaginatedDetails = scheduleService.searchSchedules(searchTrainDto,pageNo, pageSize);

        int startNumber = allPaginatedDetails.getNumber() * allPaginatedDetails.getSize() + 1;
        int endNumber = Math.min(startNumber + allPaginatedDetails.getSize() - 1, (int) allPaginatedDetails.getTotalElements());

        return ResponseEntity.ok(
                PaginatedResponse.<List<TrainScheduleInfoDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("fetched all details")
                        .currentPage(allPaginatedDetails.getNumber()+1)
                        .totalItems(allPaginatedDetails.getTotalElements())
                        .totalPages(allPaginatedDetails.getTotalPages())
                        .startNumber(startNumber)
                        .endNumber(endNumber)
                        .data(allPaginatedDetails.getContent())
                        .build()
        );

    }

    @PostMapping("check")
    public ResponseEntity<ApiResponse> check(@RequestBody BookingDto bookingDto){
        return ResponseEntity.ok(new ApiResponse(
                200,
                "ok",
                bookingService.placeBooking(bookingDto)

        ));
    }

    @GetMapping(value = "/get/booked/seats" , params = {"traveldate","schedule"})
    public ResponseEntity<ApiResponse<List<SeatSelectionDto>>> getBookingSeatsByTravelDateAndSchedule(@RequestParam("traveldate") LocalDate travelDate, @RequestParam("schedule") String schedule){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "booked seats by travel date and schedule",
                firstClassBookingSeatService.getAllBookingSeatsByTravelDateAndSchedule(travelDate,schedule)

        ));
    }

    @PostMapping(value = "/calc/clases/ticket/price",params = "scheduleid")
    public ResponseEntity<ApiResponse<TrainScheduleInfoDto.AllCalculatedTicketPriceDto>> calculateClassesTicketPice(@RequestParam("scheduleid")String scheduleid , @RequestBody PriceCalcDto priceCalcDto){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "shedule pricess",
                authService.calculateClassesTicketPice(scheduleid,priceCalcDto)
        ));
    }

    @GetMapping(value = "/booking/detail/by", params = "bookingid")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingDetailsByBookingId(@RequestParam("bookingid") String bookingId){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "booking details by booking id",
                bookingService.getBookingDetailsByBookingId(bookingId)
        ));
    }

    @GetMapping("valid/token")
    public ResponseEntity<ApiResponse<Boolean>> isValidToken(@RequestBody String token){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "access token valid check",
                authService.validAccessToken(token)
        ));
    }

    @GetMapping(value = "/download/ticket",params = "bookingid")
    public ResponseEntity<byte[]> downloadTicketByBookingId(@RequestParam("bookingid") String bookingId){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("ticket.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateTicketPdf(bookingId).toByteArray());

    }





}

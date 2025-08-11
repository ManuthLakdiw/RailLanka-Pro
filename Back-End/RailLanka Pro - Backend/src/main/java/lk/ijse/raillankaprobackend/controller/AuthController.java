package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.*;
import lk.ijse.raillankaprobackend.service.*;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/api/v1/raillankapro/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PassengerService passengerService;
    private final AdminService adminService;
    private final StationMasterService stationMasterService;
    private final CounterService counterService;
    private final AuthService authService;


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
    public ResponseEntity<ApiResponse<String>> registerCounter(@RequestBody StaffDto staffDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Counter Registration",
                counterService.registerCounter(staffDto)
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


}

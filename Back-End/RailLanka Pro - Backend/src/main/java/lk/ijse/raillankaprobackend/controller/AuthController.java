package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.service.PassengerService;
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


    @PostMapping("/register/passenger")
    public ResponseEntity<ApiResponse<String>> registerPassenger(@RequestBody PassengerDto passengerDto){
        return new ResponseEntity<>(new ApiResponse<>(
                201,
                "Passenger Registration",
                passengerService.registerPassenger(passengerDto)
        ), HttpStatus.CREATED);
    }

}

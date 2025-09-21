package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.service.BookingService;
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
@RequestMapping("/api/v1/raillankapro/booking")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("place")
    public ResponseEntity<ApiResponse<String>> placeBooking(@RequestBody BookingDto bookingDto){
       return new ResponseEntity<>(new ApiResponse<>(
               201,
               "place schedule",
               bookingService.placeBooking(bookingDto)
       ), HttpStatus.CREATED);
    }
}

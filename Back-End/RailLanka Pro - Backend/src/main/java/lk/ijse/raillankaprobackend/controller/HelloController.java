package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.service.StationService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {


    @GetMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public String hello(){
        return "Hello World";
    }


}

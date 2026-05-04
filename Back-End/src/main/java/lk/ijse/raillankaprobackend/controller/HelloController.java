package lk.ijse.raillankaprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
    @PreAuthorize("hasRole('ADMIN')")
    public String hello(){
        return "Hello World";
    }




}

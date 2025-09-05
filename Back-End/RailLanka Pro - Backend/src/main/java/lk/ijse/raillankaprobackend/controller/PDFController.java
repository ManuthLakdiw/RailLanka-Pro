package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.entity.projection.StaffProjection;
import lk.ijse.raillankaprobackend.service.PDFService;
import lk.ijse.raillankaprobackend.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/api/v1/raillankapro/pdf")
@RequiredArgsConstructor
@CrossOrigin
public class PDFController {

    private final PDFService pdfService;
    private final StationService stationService;

    @GetMapping(value = "/download/by/station" , params = "station")
    public ResponseEntity<byte[]> getEmployeesByStation(@RequestParam("station") String station){

        List<StaffProjection> staffByStation = stationService.getStaffByStation(station);

        if(staffByStation.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("employees_station_" + station + ".pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");



        return ResponseEntity.ok().headers(headers).body(pdfService.generateEmployeePdfByStation(station).toByteArray());



    }

}

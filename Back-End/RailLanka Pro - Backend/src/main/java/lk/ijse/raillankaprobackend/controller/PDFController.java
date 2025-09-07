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

    @GetMapping(value = "/download/all/passengers")
    public ResponseEntity<byte[]> getAllPassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("all_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");



        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllPassengersPdf().toByteArray());



    }

    @GetMapping(value = "/download/local/passengers")
    public ResponseEntity<byte[]> getLocalPassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("local_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");



        return ResponseEntity.ok().headers(headers).body(pdfService.generateLocalPassengersPdf().toByteArray());

    }

    @GetMapping(value = "/download/foreign/passengers")
    public ResponseEntity<byte[]> getForeignPassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("foreign_local_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateForeignPassengersPdf().toByteArray());

    }

    @GetMapping(value = "/download/all/active/passengers")
    public ResponseEntity<byte[]> getActivePassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllActivePassengersPdf().toByteArray());

    }

    @GetMapping(value = "/download/all/blocked/passengers")
    public ResponseEntity<byte[]> getBlockedPassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("blocked_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllBlockedPassengersPdf().toByteArray());

    }

    @GetMapping(value = "/download/local/active/passengers")
    public ResponseEntity<byte[]> getLocalActivePassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("local_active_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateLocalActivePassengersPdf().toByteArray());

    }


    @GetMapping(value = "/download/local/blocked/passengers")
    public ResponseEntity<byte[]> getLocalBlockedPassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("local_blocked_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateLocalBlockedPassengersPdf().toByteArray());

    }

    @GetMapping(value = "/download/foreign/active/passengers")
    public ResponseEntity<byte[]> getForeignActivePassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("foreign_active_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateForeignActivePassengersPdf().toByteArray());

    }

    @GetMapping(value = "/download/foreign/blocked/passengers")
    public ResponseEntity<byte[]> getForeignBlockedPassengers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("foreign_blocked_passengers.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        return ResponseEntity.ok().headers(headers).body(pdfService.generateForeignBlockedPassengersPdf().toByteArray());

    }

}

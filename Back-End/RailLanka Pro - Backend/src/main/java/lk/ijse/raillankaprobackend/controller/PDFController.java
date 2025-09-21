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


    @GetMapping(value = "/download/all/trains")
    public ResponseEntity<byte[]> getAllTrains(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("all_trains.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllTrainsPdf().toByteArray());

    }

    @GetMapping(value = "/download/active/trains")
    public ResponseEntity<byte[]> getActiveTrains(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_trains.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");



        return ResponseEntity.ok().headers(headers).body(pdfService.generateActiveTrainsPdf().toByteArray());



    }

    @GetMapping(value = "/download/inactive/trains")
    public ResponseEntity<byte[]> getInactiveTrains(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inactive_trains.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateInactiveTrainsPdf().toByteArray());

    }


    @GetMapping(value = "/download/all/stations")
    public ResponseEntity<byte[]> getAllStations(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("all_stations.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllStationPdf().toByteArray());

    }

    @GetMapping(value = "/download/active/stations")
    public ResponseEntity<byte[]> getActiveStations(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_stations.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateActiveStationPdf().toByteArray());

    }

    @GetMapping(value = "/download/inactive/stations")
    public ResponseEntity<byte[]> getInactiveStations(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inactive_stations.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateInactiveStationPdf().toByteArray());

    }


    @GetMapping(value = "/download/all/schedules")
    public ResponseEntity<byte[]> getAllSchedules(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("schedules.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllSchedulesPdf().toByteArray());

    }


    @GetMapping(value = "/download/active/schedules")
    public ResponseEntity<byte[]> getActiveSchedules(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_schedules.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateActiveSchedulesPdf().toByteArray());

    }


    @GetMapping(value = "/download/inactive/schedules")
    public ResponseEntity<byte[]> getInactiveSchedules(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inactive_schedules.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateInactiveSchedulesPdf().toByteArray());

    }

    @GetMapping(value = "/download/all/stationmasters")
    public ResponseEntity<byte[]> getAllStationMasters(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("all_stationmasters.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllStationMastersPdf().toByteArray());

    }

    @GetMapping(value = "/download/active/stationmasters")
    public ResponseEntity<byte[]> getActiveStationMasters(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_stationmasters.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateActiveStationMastersPdf().toByteArray());

    }

    @GetMapping(value = "/download/inactive/stationmasters")
    public ResponseEntity<byte[]> getInactiveStationMasters(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inactive_stationmasters.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateInactiveStationMastersPdf().toByteArray());
    }


    @GetMapping(value = "/download/all/counters")
    public ResponseEntity<byte[]> getAllCounters(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("all_counters.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllCountersPdf().toByteArray());

    }

    @GetMapping(value = "/download/active/counters")
    public ResponseEntity<byte[]> getActiveCounters(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_counters.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateActiveCountersPdf().toByteArray());

    }

    @GetMapping(value = "/download/inactive/counters")
    public ResponseEntity<byte[]> getInactiveCounters(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inactive_counters.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateInactiveCountersPdf().toByteArray());
    }

    @GetMapping(value = "/download/all/employees")
    public ResponseEntity<byte[]> getAllEmployees(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("all_employees.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateAllEmployeesPdf().toByteArray());

    }

    @GetMapping(value = "/download/active/employees")
    public ResponseEntity<byte[]> getActiveEmployees(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("active_employees.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateActiveEmployeesPdf().toByteArray());

    }


    @GetMapping(value = "/download/inactive/employees")
    public ResponseEntity<byte[]> getInactiveEmployees(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inactive_employees.pdf")
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdfService.generateInactiveEmployeesPdf().toByteArray());

    }




}

package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.PayHereDto;
import lk.ijse.raillankaprobackend.service.PayHereService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/raillankapro/payments")
public class PaymentController {

    private final PayHereService payHereService;

    @PostMapping(value = "/generate-hash",params = "username")
    public ResponseEntity<ApiResponse<PayHereDto>> generateHash(@RequestParam("username") String username, @RequestBody PayHereDto payHereHashDto){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "payHere hash",
                payHereService.generateHash(username,payHereHashDto)
        ));
    }

}

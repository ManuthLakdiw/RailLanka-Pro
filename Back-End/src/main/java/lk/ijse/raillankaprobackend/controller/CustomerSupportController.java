package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.CustomerSupportDto;
import lk.ijse.raillankaprobackend.service.CustomerSupportService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/api/v1/raillankapro/customer/support")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerSupportController {

    private final CustomerSupportService customerSupportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> submitSupportRequest(@ModelAttribute CustomerSupportDto customerSupportDto) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "mail send for system creation",
                customerSupportService.sendCustomerSupportEmail(customerSupportDto)
        ));

    }

}

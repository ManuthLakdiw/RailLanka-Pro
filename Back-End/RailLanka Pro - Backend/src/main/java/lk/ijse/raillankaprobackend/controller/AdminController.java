package lk.ijse.raillankaprobackend.controller;

import lk.ijse.raillankaprobackend.dto.ChangePasswordDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.service.AdminService;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/raillankapro/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final AdminService adminService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/get/by",params = {"username"})
    public ResponseEntity<ApiResponse<StaffDto>> getAdminByUsername(@RequestParam("username") String username){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "fetch by userName",
                adminService.getAdminDetailsByUserName(username)
        ));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("change/password")
    public ResponseEntity<ApiResponse<Boolean>> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "password Changed",
                adminService.changePassword(changePasswordDto)
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/credentials")
    public ResponseEntity<ApiResponse<Boolean>> updateCredentials(@RequestBody StaffDto staffDto){
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "password Changed",
                adminService.updateAdminDetailsByUserName(staffDto)
        ));
    }


}

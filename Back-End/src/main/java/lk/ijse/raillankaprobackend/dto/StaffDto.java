package lk.ijse.raillankaprobackend.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StaffDto {
    private String id;
    private String firstname;
    private String lastname;
    private String userName;
    private String password;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDate dob;
    private String railwayStation;
    private int yearsOfExperience;
    private boolean active;
    private String role;
    private LocalDate joinDate;

    public StaffDto(String id, String firstname, String lastname, String userName, String password, String idNumber, String phoneNumber, String email, String address, LocalDate dob, String railwayStation, int yearsOfExperience, boolean active) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userName = userName;
        this.password = password;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.dob = dob;
        this.railwayStation = railwayStation;
        this.yearsOfExperience = yearsOfExperience;
        this.active = active;
    }
}

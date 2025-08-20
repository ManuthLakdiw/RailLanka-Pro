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
}

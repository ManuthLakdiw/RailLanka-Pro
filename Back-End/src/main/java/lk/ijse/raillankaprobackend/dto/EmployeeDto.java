package lk.ijse.raillankaprobackend.dto;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.EmployeePosition;
import lk.ijse.raillankaprobackend.entity.Station;
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
public class EmployeeDto {

    private String employeeId;
    private String firstname;
    private String lastname;
    private String idNumber;
    private String email;
    private LocalDate dateOfBirth;
    private String contactNumber;
    private String address;
    private String position;
    private boolean active;
    private String station;
}

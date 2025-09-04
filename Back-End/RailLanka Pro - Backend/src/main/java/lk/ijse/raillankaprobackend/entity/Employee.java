package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.EmployeePosition;
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
@Entity
@Builder
public class Employee {

    @Id
    private String employeeId;
    private String firstName;
    private String lastName;
    private String idNumber;
    private String email;
    private String contactNumber;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    private EmployeePosition position;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;



}

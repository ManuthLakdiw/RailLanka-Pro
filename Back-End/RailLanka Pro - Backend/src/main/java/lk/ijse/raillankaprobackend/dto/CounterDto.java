package lk.ijse.raillankaprobackend.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@NoArgsConstructor
@Getter
@Setter
public class CounterDto extends StaffDto {
    private String counterNumber;

    public CounterDto(String id, String firstname, String lastname, String userName,
                      String password, String idNumber, String phoneNumber,
                      String email, String address, LocalDate dob,
                      String railwayStation, int yearsOfExperience, boolean active,
                      String counterNumber) {

        super(id, firstname, lastname, userName, password, idNumber,
                phoneNumber, email, address, dob, railwayStation, yearsOfExperience, active);

        this.counterNumber = counterNumber;
    }
}


package lk.ijse.raillankaprobackend.dto;

import lk.ijse.raillankaprobackend.entity.IdType;
import lk.ijse.raillankaprobackend.entity.PassengerType;
import lombok.*;

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
public class PassengerDto {

    private String passengerId;
    private String title;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String passengerType;
    private String idType;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private String role;
    private boolean blocked;
}

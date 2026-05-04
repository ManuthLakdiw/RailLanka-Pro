package lk.ijse.raillankaprobackend.dto;

import lombok.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayeeInfoDto {
    private long payeeId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String orderId;
    private String nicOrPassport;

}

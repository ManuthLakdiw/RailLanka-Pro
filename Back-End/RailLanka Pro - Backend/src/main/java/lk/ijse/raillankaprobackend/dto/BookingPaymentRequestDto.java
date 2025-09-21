package lk.ijse.raillankaprobackend.dto;

import lombok.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingPaymentRequestDto {
    private Double amount;
    private String customerName;
    private String email;
    private String phone;
}

package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PayeeInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long payeeId;


    private String orderId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String identityNumber;

    @OneToOne(mappedBy = "payeeInfo")
    private TicketBookingPayment ticketBookingPayment;

}

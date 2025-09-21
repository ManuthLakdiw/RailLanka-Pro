package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class TicketBookingPayment {

    @Id
    private String ticketBookingPaymentId;

    private String paymentType;

    private double amount;

    @OneToOne
    private Ticket ticket;

    @OneToOne
    private PayeeInfo payeeInfo;

}

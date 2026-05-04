package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.TicketStatus;
import lombok.*;

import java.time.LocalDate;

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
public class Ticket {

    @Id
    private String ticketId;


    private int validPassengerCount;

    private int adultCount;

    private int childCount;

    private LocalDate expireAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @OneToOne
    private Booking booking;

    @OneToOne(mappedBy = "ticket")
    private TicketBookingPayment ticketBookingPayment;

}

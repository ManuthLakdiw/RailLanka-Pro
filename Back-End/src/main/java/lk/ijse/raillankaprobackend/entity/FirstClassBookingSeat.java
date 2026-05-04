package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.CarriageNumber;
import lk.ijse.raillankaprobackend.entity.Dtypes.RowLetter;
import lk.ijse.raillankaprobackend.entity.Dtypes.SeatPosition;
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
public class FirstClassBookingSeat {

    @Id
    private String seatId;

    @Enumerated(EnumType.STRING)
    private CarriageNumber carriageNumber;

    @Enumerated(EnumType.STRING)
    private RowLetter rowLetter;


    private SeatPosition seatPosition;

    private LocalDate travelDate;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Schedule schedule;
}

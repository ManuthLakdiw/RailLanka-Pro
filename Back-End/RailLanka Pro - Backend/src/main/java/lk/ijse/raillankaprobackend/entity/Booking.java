package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.TravelClass;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
public class Booking {

    @Id
    private String bookingId;


    private LocalDate travelDate;


    @Enumerated(EnumType.STRING)
    private TravelClass travelClass;


    private LocalDateTime bookedAt;


    @OneToOne(mappedBy = "booking")
    private Ticket ticket;

    @ManyToOne
    private Station departureStation;

    @ManyToOne
    private Station destinationStation;

    @ManyToOne
    private Schedule schedule;


    @ManyToOne
    private Passenger passenger;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<FirstClassBookingSeat> firstClassBookedSeats;










}

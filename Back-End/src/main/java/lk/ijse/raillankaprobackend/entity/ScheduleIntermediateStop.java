package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

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
public class ScheduleIntermediateStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stopId;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private LocalTime arrivalTime;
    private LocalTime departureTime;

    private int stopOrder;


}

package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.ScheduleFrequency;
import lombok.*;

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
public class Schedule {

    @Id
    private String scheduleId;
    private LocalTime mainDepartureTime;
    private LocalTime mainArrivalTime;

    @Enumerated(EnumType.STRING)
    private ScheduleFrequency scheduleFrequency;
    private String description;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "departure_station_id")
    private Station mainDepartureStation;

    @ManyToOne
    @JoinColumn(name = "arrival_station_id")
    private Station mainArrivalStation;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleIntermediateStop> stops;

}

package lk.ijse.raillankaprobackend.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

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
public class ScheduleDto {

    private String scheduleId;
    private LocalTime mainDepartureTime;
    private LocalTime mainArrivalTime;
    private String scheduleFrequency;
    private String description;
    private boolean status;
    private String trainName;
    private String trainId;
    private String trainType;
    private String departureStation;
    private String arrivalStation;
    private String duration;
    private List<ScheduleIntermediateStopDto> stops;
}

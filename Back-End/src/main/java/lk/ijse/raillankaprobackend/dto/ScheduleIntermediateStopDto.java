package lk.ijse.raillankaprobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleIntermediateStopDto {

    private long stopId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int stopOrder;
    private String stationId;

}

package lk.ijse.raillankaprobackend.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class TrainScheduleInfoDto {
    private String scheduleId;
    private String trainName;
    private String trainType;
    private String trainClass;
    private Boolean status;
    private String date;
    private String scheduleDescription;
    private String fullScheduleDuration;
    private String selectedDepartureStation;
    private String selectedDestinationStation;
    private String selectedScheduleDuration;
    private String scheduleFrequency;
    private String departureStationName;
    private String arrivalStationName;
    private String departureStationFacilities;
    private String arrivalStationFacilities;
    private String mainDepartureTime;
    private String mainArrivalTime;
    private String selectedDepartureStationDepartureTime;
    private String selectedArrivalStationArrivalTime;
    private List<IntermediateTrainScheduleInfoDto> intermediateStops;
    private AllCalculatedTicketPriceDto allCalculatedTicketPrice;

    @lombok.Getter
    @lombok.Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntermediateTrainScheduleInfoDto {
        private String stationName;
        private String stationFacilities;
        private String arrivalTime;
        private String departureTime;
        private Integer stopOrder;
    }

    @lombok.Getter
    @lombok.Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllCalculatedTicketPriceDto {
        private double firstClass;
        private double secondClass;
        private double thirdClass;

    }
}

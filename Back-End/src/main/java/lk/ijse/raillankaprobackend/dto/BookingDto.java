package lk.ijse.raillankaprobackend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private String bookingId;
    private String passengerId;
    private String scheduleId;
    private LocalDate travelDate;
    private String formattedTravelDate;
    private String bookingDate;
    private String travelClass;
    private String trainName;
    private String trainType;
    private int adultCount;
    private int childCount;
    private double totalAmount;
    private String formattedTotalAmount;
    private String departureTime;
    private String arrivalTime;
    private String departureStation;
    private String destinationStation;
    private String status;
    private String bookingTime;
    private List<SeatSelectionDto> selectedSeat;
    private String formatedselectedSeat;
    private PayeeInfoDto payeeInfo;

}

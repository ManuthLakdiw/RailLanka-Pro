package lk.ijse.raillankaprobackend.dto;

import lombok.*;

import java.time.LocalDate;


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
public class SearchTrainDto {
    String departureStation;
    String destinationStation;
    LocalDate date;
    int adultCount;
    int childCount;
}

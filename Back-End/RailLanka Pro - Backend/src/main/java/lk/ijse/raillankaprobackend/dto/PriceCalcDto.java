package lk.ijse.raillankaprobackend.dto;

import lombok.*;

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
public class PriceCalcDto {
    String departure;
    String destination;
    int adultCount;
    int childCount;
}

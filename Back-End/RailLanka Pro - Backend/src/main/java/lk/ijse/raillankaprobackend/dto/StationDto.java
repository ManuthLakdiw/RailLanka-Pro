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
public class StationDto {
    private String stationId;
    private String name;
    private String stationCode;
    private String district;
    private String province;
    private int noOfPlatforms;
    private String platformLength;
    private String otherFacilities;
    private boolean inService;
}

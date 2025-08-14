package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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
public class Station {
    @Id
    private String stationId;
    private String name;
    private String stationCode;
    private String district;
    private String province;
    private int noOfPlatforms;

    @Column(name = "platform_length (meter)")
    private long platformLength;
    private String otherFacilities;
    private boolean inService;
}

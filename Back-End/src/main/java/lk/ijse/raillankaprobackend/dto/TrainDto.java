package lk.ijse.raillankaprobackend.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lk.ijse.raillankaprobackend.entity.Dtypes.TrainCategory;
import lk.ijse.raillankaprobackend.entity.Dtypes.TrainType;
import lk.ijse.raillankaprobackend.entity.Station;
import lombok.*;

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
public class TrainDto {

    private String trainId;
    private String trainName;
    private String category;
    private String trainType;
    private String classes;
    private String specialFeatures;
    private String SpecialTrainType;
    private String cargoType;
    private Long capacity;
    private boolean active;
    private List<String> stations;
    private Long stopStationCount;

}

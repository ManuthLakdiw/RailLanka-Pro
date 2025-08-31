package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.SpecialTrainType;
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
public class SpecialTrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long specialId;

    @Enumerated(EnumType.STRING)
    private SpecialTrainType specialTrainType;
    private String specialFeatures;

    @OneToOne
    @JoinColumn(name = "train_id")
    private Train train;

}

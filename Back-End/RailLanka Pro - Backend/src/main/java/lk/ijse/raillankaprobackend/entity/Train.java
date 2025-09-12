package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.TrainCategory;
import lk.ijse.raillankaprobackend.entity.Dtypes.TrainType;
import lombok.*;

import java.util.List;

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
public class Train {

    @Id
    private String trainId;
    private String name;

    @Enumerated(EnumType.STRING)
    private TrainCategory category;

    @Enumerated(EnumType.STRING)
    private TrainType trainType;
    private String classes;
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "train_station",
            joinColumns = @JoinColumn(name = "train_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private List<Station> stations;

    @OneToOne(mappedBy = "train" , cascade = CascadeType.ALL)
    private GoodsTrain goodsTrain;

    @OneToOne(mappedBy = "train" , cascade = CascadeType.ALL)
    private SpecialTrain specialTrain;

    @OneToMany(mappedBy = "train")
    private List<Schedule> schedules;

}

package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.CargoType;
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
public class GoodsTrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long goodsId;

    @Enumerated(EnumType.STRING)
    private CargoType cargoType;

    @Column(name = "capacity (tons)")
    private long capacity;

    @OneToOne
    @JoinColumn(name = "train_id")
    private Train train;

}

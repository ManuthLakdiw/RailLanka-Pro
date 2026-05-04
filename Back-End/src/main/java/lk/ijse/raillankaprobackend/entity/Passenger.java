package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.IdType;
import lk.ijse.raillankaprobackend.entity.Dtypes.PassengerType;
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
public class Passenger {

    @Id
    private String passengerId;
    private String title;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private PassengerType passengerType;

    @Enumerated(EnumType.STRING)
    private IdType idtype;

    @Column(name = "id_number(NIC or PASSPORT)")
    private String idNumber;
    private String phoneNumber;
    private String email;
    private boolean blocked;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookings;

}

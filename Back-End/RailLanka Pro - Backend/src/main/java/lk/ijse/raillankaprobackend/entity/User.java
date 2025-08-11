package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    private String userId;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private SystemUserRole role;
    private LocalDate createdDate;

    @OneToOne(mappedBy = "user")
    private Passenger passenger;

    @OneToOne(mappedBy = "user")
    private Admin admin;

    @OneToOne(mappedBy = "user")
    private StationMaster stationMaster;

    @OneToOne(mappedBy = "user")
    private Counter counter;

    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshToken;
}

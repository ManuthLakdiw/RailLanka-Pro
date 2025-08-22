package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
public class Counter {

    @Id
    private String counterId;

    @Enumerated(EnumType.STRING)
    private CounterNumber counterNumber;
    private String firstname;
    private String lastname;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDate dob;
    private int yearsOfExperience;
    private boolean active;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private Station station;
}

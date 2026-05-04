package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
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
public class Admin {

    @Id
    private String adminId;
    private String title;
    private String firstname;
    private String lastname;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private boolean active;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}

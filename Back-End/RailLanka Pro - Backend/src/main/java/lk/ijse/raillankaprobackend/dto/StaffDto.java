package lk.ijse.raillankaprobackend.dto;

import lombok.*;

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
public class StaffDto {
    private String id;
    private String title;
    private String name;
    private String userName;
    private String password;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private boolean active;
}

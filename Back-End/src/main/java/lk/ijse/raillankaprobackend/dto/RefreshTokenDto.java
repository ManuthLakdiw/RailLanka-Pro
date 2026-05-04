package lk.ijse.raillankaprobackend.dto;

import lombok.*;

import java.time.Instant;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshTokenDto {
    private String refreshTokenId;
    private String token;
    private Instant expiryDate;
    private String user;
}

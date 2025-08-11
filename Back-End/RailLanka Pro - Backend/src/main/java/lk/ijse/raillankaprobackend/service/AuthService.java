package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.AuthDto;
import lk.ijse.raillankaprobackend.dto.AuthResponseDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface AuthService {
    AuthResponseDto authenticate(AuthDto authDto);

    AuthResponseDto reGenerateAccessTokenUsingRefreshToken(String refreshToken);
}

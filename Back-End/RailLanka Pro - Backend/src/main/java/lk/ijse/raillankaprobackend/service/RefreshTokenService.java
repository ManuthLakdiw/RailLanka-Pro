package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.RefreshTokenDto;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface RefreshTokenService {

    String generateRefreshToken(String username);

    String generateNewRefreshTokenID();

    Optional<RefreshTokenDto> findByToken(String token);

    RefreshTokenDto verifyExpiration(RefreshTokenDto refreshTokenDto);


}

package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.AuthDto;
import lk.ijse.raillankaprobackend.dto.AuthResponseDto;
import lk.ijse.raillankaprobackend.dto.RefreshTokenDto;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.AuthService;
import lk.ijse.raillankaprobackend.service.RefreshTokenService;
import lk.ijse.raillankaprobackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponseDto authenticate(AuthDto authDto) {
        User user = userRepository.findByUsername(authDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));


        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = refreshTokenService.generateRefreshToken(user.getUsername());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(authDto.getUsername())
                .build();
    }

    @Override
    public AuthResponseDto reGenerateAccessTokenUsingRefreshToken(String refreshToken) {
        Optional<RefreshTokenDto> optionalToken = refreshTokenService.findByToken(refreshToken);

        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Refresh token is not in database");
        }

        RefreshTokenDto refreshTokenDto = optionalToken.get();
        RefreshTokenDto verifiedToken = refreshTokenService.verifyExpiration(refreshTokenDto);

        String newAccessToken = jwtUtil.generateAccessToken(verifiedToken.getUser());

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();

    }
}

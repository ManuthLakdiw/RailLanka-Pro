package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.RefreshTokenDto;
import lk.ijse.raillankaprobackend.entity.RefreshToken;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.exception.TokenExpiredException;
import lk.ijse.raillankaprobackend.repository.RefreshTokenRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;

    @Value("${refresh.token.expiration.time}")
    private long expirationTime;



    @Override
    public String generateRefreshToken(String userName) {

        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshTokenId(generateNewRefreshTokenID())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(expirationTime))
                .user(user)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken.getToken();
    }

    @Override
    public String generateNewRefreshTokenID() {

        if (refreshTokenRepository.getLastRefreshTokenId().isPresent()){
            String lastRefreshTokenId = refreshTokenRepository.getLastRefreshTokenId().get();
            String[] split = lastRefreshTokenId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999){
                    throw new RuntimeException("All available Refresh Token IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("RFT%05d-%05d", prefixNumber, suffixNumber);
        }

        return "RFT00000-00001";
    }

    @Override
    public Optional<RefreshTokenDto> findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(rt -> RefreshTokenDto.builder()
                        .refreshTokenId(rt.getRefreshTokenId())
                        .token(rt.getToken())
                        .expiryDate(rt.getExpiryDate())
                        .user(rt.getUser().getUserName())
                        .build()
                );
    }
    @Override
    public RefreshTokenDto verifyExpiration(RefreshTokenDto refreshTokenDto) {
        if (refreshTokenDto.getExpiryDate().compareTo(Instant.now()) < 0){
            RefreshToken refreshTokenEntity = refreshTokenRepository.findById(refreshTokenDto.getRefreshTokenId())
                    .orElseThrow(() -> new RuntimeException("Refresh Token not found for deletion"));
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new TokenExpiredException("Refresh Token has expired. Please re-login");
        }
        return refreshTokenDto;
    }
}

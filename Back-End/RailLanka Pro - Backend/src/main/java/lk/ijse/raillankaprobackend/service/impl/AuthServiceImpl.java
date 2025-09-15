package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.AuthDto;
import lk.ijse.raillankaprobackend.dto.AuthResponseDto;
import lk.ijse.raillankaprobackend.dto.RefreshTokenDto;
import lk.ijse.raillankaprobackend.entity.Admin;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.repository.AdminRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.AuthService;
import lk.ijse.raillankaprobackend.service.EmailService;
import lk.ijse.raillankaprobackend.service.RefreshTokenService;
import lk.ijse.raillankaprobackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
    private final EmailService emailService;
    private final AdminRepository adminRepository;

    private final Map<String, String> otpStore = new HashMap<>();

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

    @Override
    public String sendVerificationCode(String email) {
        int otp = new Random().nextInt(90000) + 10000;

        otpStore.put(email, String.valueOf(otp));

        new Thread(() -> {
            emailService.sendOtpCode(
                    email,
                    String.valueOf(otp)
            );
        }).start();

        return "Email has been sent to " + email + " with OTP code. Please enter the OTP code to verify your email address. OTP code will expire in 10 minutes.";
    }

    @Override
    public boolean verifyVerificationCode(String email, String otp) {
        String savedOtp = otpStore.get(email);

        if (savedOtp != null && savedOtp.equals(otp)) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }

    @Override
    public Boolean resetPassword(AuthDto authDto) {
        Admin admin = adminRepository.findByEmail(
                authDto.getEmail()).orElseThrow(() -> new BadCredentialsException("Invalid email address"));

        admin.getUser().setPassword(passwordEncoder.encode(authDto.getPassword()));

        try {
            adminRepository.save(admin);
            return true;
        }catch (Exception e){
            return false;
        }

    }
}

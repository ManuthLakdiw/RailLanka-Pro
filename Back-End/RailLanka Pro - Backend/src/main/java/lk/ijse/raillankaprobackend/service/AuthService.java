package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.AuthDto;
import lk.ijse.raillankaprobackend.dto.AuthResponseDto;
import lk.ijse.raillankaprobackend.dto.PriceCalcDto;
import lk.ijse.raillankaprobackend.dto.TrainScheduleInfoDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface AuthService {
    AuthResponseDto authenticate(AuthDto authDto);

    AuthResponseDto reGenerateAccessTokenUsingRefreshToken(String refreshToken);

     String sendVerificationCode(String email);

     boolean verifyVerificationCode(String email , String otp);

    Boolean resetPassword(AuthDto authDto);

    TrainScheduleInfoDto.AllCalculatedTicketPriceDto calculateClassesTicketPice(String scheduleId , PriceCalcDto priceCalcDto);

    boolean validAccessToken(String accessToken);
}

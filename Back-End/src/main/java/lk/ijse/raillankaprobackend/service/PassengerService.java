package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.dto.ChangePasswordDto;
import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.PassengerDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PassengerService {

    String registerPassenger(PassengerDto passengerDto);

    String generateNewPassengerId();

    Page<PassengerDto> getAllPassengers(int pageNo, int pageSize);

    String changePassengerStatus(String passengerId, boolean status);

    Page<PassengerDto> filterPassengerByKeyword(String keyword, int pageNo, int pageSize);

    Page<PassengerDto> filterPassengerByStatus(boolean status, int pageNo, int pageSize);

    Page<PassengerDto> filterPassengerByPassengerType(String passengerType, int pageNo, int pageSize);

    PassengerDto getPassengerDetailsByPassengerId(String passengerId);

    String updatePassengerDetailsByUserName(String userName,PassengerDto passengerDto);

    PassengerDto getPassengerDetailsByUserName(String userName);

    boolean ChangePassword(ChangePasswordDto changePasswordDto);

    List<BookingDto> getBookingDetailsByUserName(String userName);

    String getPassengerIdByUserName(String userName);



}

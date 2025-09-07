package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.PassengerDto;
import org.springframework.data.domain.Page;

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

}

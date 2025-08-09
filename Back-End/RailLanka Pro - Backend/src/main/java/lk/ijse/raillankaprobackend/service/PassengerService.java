package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.PassengerDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PassengerService {

    String registerPassenger(PassengerDto passengerDto);

    String generateNewPassengerId();
}

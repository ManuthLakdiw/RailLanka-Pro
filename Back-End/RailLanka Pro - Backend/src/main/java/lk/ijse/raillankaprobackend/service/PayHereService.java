package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.PayHereDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PayHereService {

    PayHereDto generateHash(String userName, PayHereDto payHereHashDto);
}

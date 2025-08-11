package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.StaffDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface CounterService {

    String registerCounter(StaffDto staffDto);

    String generateNewCounterId();
}

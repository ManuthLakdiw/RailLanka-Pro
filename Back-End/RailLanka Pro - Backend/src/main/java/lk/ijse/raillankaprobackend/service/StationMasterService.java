package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface StationMasterService {
    String registerStationMaster(StaffDto staffDto);

    String generateNewStationMasterId();
}

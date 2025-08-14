package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.StationDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface StationService {

    String generateNewStationId();

    String registerStation(StationDto stationDto);
}

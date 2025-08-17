package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.StationDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface StationService {

    String generateNewStationId();

    String registerStation(StationDto stationDto);

    String formatProvinceName(String province);

    Page<StationDto> getAllStations(int pageNo, int pageSize);

    String changeStationInServiceStatus(String stationId, boolean status);

    Optional<StationDto> findStationById(String stationId);

}

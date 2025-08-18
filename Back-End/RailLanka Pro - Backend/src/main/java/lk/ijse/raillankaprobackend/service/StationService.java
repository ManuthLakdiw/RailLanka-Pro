package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.StationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    String updateStationDetails(StationDto stationDto);

    String deleteStation(String stationId);

    Page<StationDto> filterStationsByKeyword(String keyword, int pageNo, int PageSize);

}

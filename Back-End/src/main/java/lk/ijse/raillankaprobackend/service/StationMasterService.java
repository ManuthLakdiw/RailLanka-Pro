package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.dto.StationDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface StationMasterService {
    String registerStationMaster(StaffDto staffDto);

    String generateNewStationMasterId();

    Page<StaffDto> getAllStationMasters(int pageNo, int pageSize);

    List<String> getAllAssignedStations();

    String changeStationMasterStatus(String stationMasterId, boolean status);

    String deleteStationMaster(String stationMasterId);

    Page<StaffDto> filterStationMastersByKeyword(String keyword, int pageNo, int pageSize);

    String updateStationMasterDetails(StaffDto staffDto);

    Optional<StaffDto> findStationMasterById(String stationMasterId);

    long getAllStationMastersCount();

    long getActiveStationMastersCount();

    long getInactiveStationMastersCount();

    Double getAverageExperienceOfActiveStationMasters();

    Map<String, Long> getStationMasterCountByProvince();
}

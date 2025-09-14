package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.projection.StaffProjection;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.StationNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.service.StationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final ModelMapper modelMapper;

    @Override
    public String generateNewStationId() {
        if (stationRepository.getLastStationId().isPresent()){
            String lastId = stationRepository.getLastStationId().get();
            String[] split = lastId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException(
                            "All available Station IDs have been used. Please contact the system administrator");
                }
            }

            return String.format("STN%05d-%05d", prefixNumber, suffixNumber);
        }
        return "STN00000-00001";
    }


    @Transactional
    @Override
    public String registerStation(StationDto stationDto) {

        if (stationRepository.findByName(stationDto.getName()).isPresent()){
            throw new StationNameAlreadyExistsException("This station name is already taken. Please choose a different one.");
        }

        String name = stationDto.getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

        String district = stationDto.getDistrict();
        String formattedDistrict = district.substring(0, 1).toUpperCase() + district.substring(1).toLowerCase();

        String formattedProvince = formatProvinceName(stationDto.getProvince());
        Station station = Station.builder()
                .stationId(generateNewStationId())
                .name(formattedName)
                .stationCode(stationDto.getStationCode().toUpperCase())
                .province(formattedProvince)
                .district(formattedDistrict)
                .noOfPlatforms(stationDto.getNoOfPlatforms())
                .platformLength(Long.parseLong(stationDto.getPlatformLength()))
                .otherFacilities(stationDto.getOtherFacilities())
                .inService(true)
                .build();

        stationRepository.save(station);

        return "Station has been successfully registered.";
    }

    @Override
    public String formatProvinceName(String province) {
        if (province == null || province.trim().isEmpty()) {
            return province;
        }

        String trimmed = province.trim();

        String lowerTrimmed = trimmed.toLowerCase();

        if (lowerTrimmed.equals("sri lanka") || lowerTrimmed.equals("sri-lanka") || lowerTrimmed.equals("srilanka")) {
//            return trimmed.substring(0,1).toUpperCase() + trimmed.substring(1);
                return "Sri-Lanka";

        }

        String formatted = trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();

        if (!formatted.toLowerCase().endsWith("province")) {
            formatted += " province";
        }

        return formatted;
    }

    @Override
    public Page<StationDto> getAllStations(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Station> studentPage = stationRepository.findAll(pageable);

            return studentPage.map(station -> modelMapper.map(station, StationDto.class));

    }

    @Override
    public String changeStationInServiceStatus(String stationId, boolean status) {
        stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Station Id"));
        stationRepository.updateStationServiceStatus(stationId, status);

        return "Station has been successfully set to " + (status ? "In Service" : "Out of Service");


    }

    @Override
    public Optional<StationDto> findStationById(String stationId) {
        Optional<Station> station = stationRepository.findById(stationId);
        if (station.isPresent()){
            StationDto stationDto = modelMapper.map(station.get(), StationDto.class);
            return Optional.ofNullable(stationDto);
        }
       throw new IllegalArgumentException("Invalid Station Id");
    }

    @Override
    public String updateStationDetails(StationDto stationDto) {
        Station station = stationRepository.findById(stationDto.getStationId()).orElseThrow(
                () -> new IllegalArgumentException("Invalid Station Id"));
        if (!station.getName().equalsIgnoreCase(stationDto.getName())) {
            Optional<Station> existingStation = stationRepository.findByName(stationDto.getName());
            if (existingStation.isPresent()) {
                Station existing = existingStation.get();
                if (!existing.getStationId().equals(station.getStationId())) {
                    throw new StationNameAlreadyExistsException(
                            "This station name is already taken. Please choose a different one.");
                }
            }
        }


        String name = stationDto.getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

        station.setName(formattedName);
        station.setStationCode(stationDto.getStationCode().toUpperCase());
        station.setProvince(formatProvinceName(stationDto.getProvince()));
        station.setDistrict(stationDto.getDistrict());
        station.setNoOfPlatforms(stationDto.getNoOfPlatforms());
        station.setPlatformLength(Long.parseLong(stationDto.getPlatformLength()));
        station.setOtherFacilities(stationDto.getOtherFacilities());
        station.setInService(stationDto.isInService());
        stationRepository.save(station);
        return "station has been successfully updated.";
    }

    @Override
    public String deleteStation(String stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(
                () -> new IllegalArgumentException("Invalid Station Id"));
        stationRepository.delete(station);
        return "station has been successfully deleted.";
    }



    @Override
    public Page<StationDto> filterStationsByKeyword(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Station> stationsPage = stationRepository.filterStationsByKeyword(keyword, pageable);

        return stationsPage.map(station -> modelMapper.map(station, StationDto.class));

    }

    @Override
    public List<StationDto> getAllStationNamesAndCodes() {
        List<Station> allStations = stationRepository.findAll();
        return modelMapper.map(allStations, new TypeToken<List<StationDto>>(){}.getType());
    }

    @Override
    public List<StaffProjection> getStaffByStation(String name) {
        stationRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("Invalid Station Name"));

        return stationRepository.findAllStaffByStationName(name);
    }

    @Override
    public List<StaffProjection> getStaffByStationAndPosition(String name, String position) {
        stationRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("Invalid Station Name"));

        return stationRepository.findStaffByStationAndPosition(name,position);

    }

    @Override
    public List<StaffProjection> getStaffByStationAndKeyword(String name, String keyword) {
        stationRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("Invalid Station Name"));

        return stationRepository.findStaffByStationAndKeyword(name,keyword);

    }

    @Override
    public long getNumberOfStations() {
        return stationRepository.count();
    }

    @Override
    public long getNumberOfInServiceStations() {
        return stationRepository.countStationByInService(true);
    }

    @Override
    public long getNumberOfOutServiceStations() {
        return stationRepository.countStationByInService(false);
    }

    @Override
    public List<Map<String, Object>> countStationsByProvince() {
        return stationRepository.countStationsByProvince();
    }

    @Override
    public Map<String, Long> findTotalAndAssignedStationCounts() {
        List<Object[]> resultList = stationRepository.findTotalAndAssignedStationCounts();
        Object[] row = resultList.getFirst();

        Long total = ((Number) row[0]).longValue();
        Long assigned = ((Number) row[1]).longValue();

        return Map.of(
                "total", total,
                "assigned", assigned
        );
    }


}

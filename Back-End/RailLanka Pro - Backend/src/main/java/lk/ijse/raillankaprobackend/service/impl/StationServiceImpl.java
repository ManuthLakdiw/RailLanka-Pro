package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.StationDto;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    throw new IdGenerateLimitReachedException("All available Station IDs have been used. Please contact the system administrator");
                }
            }

            return String.format("STN%05d-%05d", prefixNumber, suffixNumber);
        }
        return "STN00000-00001";
    }

    @Transactional
    @Override
    public String registerStation(StationDto stationDto) {
        Station station = Station.builder()
                .stationId(generateNewStationId())
                .name(stationDto.getName())
                .stationCode(stationDto.getStationCode().toUpperCase())
                .province(stationDto.getProvince())
                .district(stationDto.getDistrict())
                .noOfPlatforms(stationDto.getNoOfPlatforms())
                .platformLength(Long.parseLong(stationDto.getPlatformLength()))
                .otherFacilities(stationDto.getOtherFacilities())
                .inService(true)
                .build();

        stationRepository.save(station);

        return "Station has been successfully registered.";
    }
}

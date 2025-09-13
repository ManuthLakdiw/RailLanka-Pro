package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.TrainDto;
import lk.ijse.raillankaprobackend.dto.TrainStationDto;
import lk.ijse.raillankaprobackend.entity.Train;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TrainService {

    String generateNewTrainId();

    String registerTrain(TrainDto trainDto);

    Page<TrainDto> getAllTrainsAndStopStationCount(int page, int pageSize);

    String formattedTrainName(String trainName);

    String changeTrainStatus(String trainId, boolean status);

    String deleteTrain(String trainId);

    Page<TrainDto> filterTrainsByKeyword(String keyword, int pageNo, int pageSize);

    Page<TrainDto> filterTrainsByCategory(String category, int pageNo, int pageSize);

    List<String> getStopingStationNamesByTrainId(String trainId);

    TrainDto getTrainsAndStationDetailsByTrainId(String trainId);

    String updateTrainDetails(TrainDto trainDto);

    List<TrainDto> getAllTrains();

    List<TrainStationDto> getAllStationsByTrainName(String trainName);

    long getAllTrainsCount();

    long getActiveTrainsCount();

    long getInactiveTrainsCount();

    Map<String,Long> getTrainTypeCounts();

}

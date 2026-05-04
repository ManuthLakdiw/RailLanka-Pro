package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.*;
import lk.ijse.raillankaprobackend.entity.Schedule;
import lk.ijse.raillankaprobackend.entity.Station;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface ScheduleService {

    String generateNewScheduleId();

    String registerSchedule(ScheduleDto scheduleDto);

    Page<ScheduleDto> getAllSchedule(int pageNo, int pageSize);

    String changeScheduleStatus(String scheduleId, boolean status);

    ScheduleDto getScheduleDetailsByScheduleId(String scheduleId);

    Page<ScheduleDto> filterScheduleByKeyWord(String keyword, int pageNo, int pageSize);

    String deleteSchedule(String scheduleId);

    Page<ScheduleDto> filterScheduleByStatus(boolean status, int pageNo, int pageSize);

    Page<ScheduleDto> filterScheduleByTrainName(String trainName, int pageNo, int pageSize);

    Page<ScheduleDto> filterScheduleByFrequency(String frequency, int pageNo, int pageSize);

    String updateScheduleDetails(ScheduleDto scheduleDto);

    long getActiveScheduleCount();

    long getInactiveScheduleCount();

    long getAllScheduleCount();

    double getAverageDailyTrips();

    Map<String,Long> getScheduleCountsByFrequencies();

    Page<TrainScheduleInfoDto> searchSchedules(SearchTrainDto searchTrainDto, int pageNo, int pageSize);

    Page<TrainScheduleInfoDto> searchSchedulesByTrainName(SearchTrainDto searchTrainDto, String trainName, int pageNo, int pageSize);

    Page<TrainScheduleInfoDto> searchSchedulesByTrainClass(SearchTrainDto searchTrainDto, String trainClass, int pageNo, int pageSize);










}

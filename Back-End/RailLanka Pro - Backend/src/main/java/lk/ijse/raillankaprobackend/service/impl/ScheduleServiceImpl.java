package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.ScheduleDto;
import lk.ijse.raillankaprobackend.dto.ScheduleIntermediateStopDto;
import lk.ijse.raillankaprobackend.entity.Dtypes.ScheduleFrequency;
import lk.ijse.raillankaprobackend.entity.Schedule;
import lk.ijse.raillankaprobackend.entity.ScheduleIntermediateStop;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.Train;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.ScheduleConflictException;
import lk.ijse.raillankaprobackend.repository.ScheduleRepository;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.repository.TrainRepository;
import lk.ijse.raillankaprobackend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    @Override
    public String generateNewScheduleId() {
        if ( scheduleRepository.getLastScheduleId().isPresent()) {
            Optional<String> lastScheduleId = scheduleRepository.getLastScheduleId();
            String[] split = lastScheduleId.get().split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;
                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available Schedule IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("SCH%05d-%05d", prefixNumber, suffixNumber);
        }
        return "SCH00000-00001";
    }

    @Transactional
    @Override
    public String registerSchedule(ScheduleDto scheduleDto) {
        System.out.println(scheduleDto.getTrainName());
        Train train = trainRepository.findByName(scheduleDto.getTrainName())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        Station departureStation = stationRepository.findByName(scheduleDto.getDepartureStation())
                .orElseThrow(() -> new RuntimeException("Departure Station not found"));


        Station arrivalStation = stationRepository.findByName(scheduleDto.getArrivalStation())
                .orElseThrow(() -> new RuntimeException("Arrival Station not found"));

        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
                train,
                scheduleDto.getMainDepartureTime(),
                scheduleDto.getMainArrivalTime()
        );

        if (!conflicts.isEmpty()) {
            throw new ScheduleConflictException("Conflict detected: Another schedule overlaps for this train.");
        }

        Schedule schedule = Schedule.builder()
                .scheduleId(generateNewScheduleId())
                .train(train)
                .mainDepartureStation(departureStation)
                .mainArrivalStation(arrivalStation)
                .mainDepartureTime(scheduleDto.getMainDepartureTime())
                .mainArrivalTime(scheduleDto.getMainArrivalTime())
                .description(scheduleDto.getDescription())
                .scheduleFrequency(ScheduleFrequency.valueOf(scheduleDto.getScheduleFrequency()))
                .status(true)

                .build();

        List<ScheduleIntermediateStop> stops = scheduleDto.getStops().stream().map(stopDto -> {
            Station station = stationRepository.findByName(stopDto.getStationId())
                    .orElseThrow(() -> new RuntimeException("Stop Station not found"));

            return ScheduleIntermediateStop.builder()
                    .schedule(schedule)
                    .station(station)
                    .stopOrder(stopDto.getStopOrder())
                    .arrivalTime(stopDto.getArrivalTime())
                    .departureTime(stopDto.getDepartureTime())
                    .build();
        }).toList();

        schedule.setStops(stops);

        scheduleRepository.save(schedule);

        return "Schedule has been successfully registered.";
    }

    @Override
    public Page<ScheduleDto> getAllSchedule(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Schedule> schedulePage = scheduleRepository.findAll(pageable);

        return getScheduleDtos(schedulePage);
    }

    @Override
    public String changeScheduleStatus(String scheduleId, boolean status) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalArgumentException("Schedule not found for ID: " + scheduleId));

        schedule.setStatus(status);
        scheduleRepository.save(schedule);
        return "Schedule has been successfully set to " + (status ? "Active" : "Inactive");
    }

    @Override
    public ScheduleDto getScheduleDetailsByScheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for ID: " + scheduleId));


        Duration duration = Duration.between(
                schedule.getMainDepartureTime(),
                schedule.getMainArrivalTime()
        );

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        String formattedDuration = hours + " hours " + minutes + " minutes";

        return ScheduleDto.builder()
                .scheduleId(schedule.getScheduleId())
                .trainName(schedule.getTrain().getName())
                .trainId(schedule.getTrain().getTrainId())
                .trainType(schedule.getTrain().getTrainType().name())
                .departureStation(schedule.getMainDepartureStation().getName())
                .arrivalStation(schedule.getMainArrivalStation().getName())
                .mainDepartureTime(schedule.getMainDepartureTime())
                .mainArrivalTime(schedule.getMainArrivalTime())
                .description(schedule.getDescription())
                .scheduleFrequency(schedule.getScheduleFrequency().name())
                .status(schedule.isStatus())
                .duration(formattedDuration)
                .stops(schedule.getStops().stream().map(stop -> new ScheduleIntermediateStopDto(
                        stop.getStopId(),
                        stop.getArrivalTime(),
                        stop.getDepartureTime(),
                        stop.getStopOrder(),
                        stop.getStation().getName()
                )).toList())
                .build();
    }

    @Override
    public Page<ScheduleDto> filterScheduleByKeyWord(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Schedule> schedulePage = scheduleRepository.filterSchedulesByKeyword(keyword,pageable);

        return getScheduleDtos(schedulePage);
    }

    @Override
    public String deleteSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for ID: " + scheduleId));

        scheduleRepository.delete(schedule);
        return "Schedule has been successfully deleted.";
    }

    @Override
    public Page<ScheduleDto> filterScheduleByStatus(boolean status, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Schedule> schedulePage = scheduleRepository.findSchedulesByStatus(status,pageable);

        return getScheduleDtos(schedulePage);

    }

    @Override
    public Page<ScheduleDto> filterScheduleByTrainName(String trainName, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        Train train = trainRepository.findByName(trainName).orElseThrow(
                () -> new IllegalArgumentException("Train not found for name: " + trainName));

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Schedule> schedulePage = scheduleRepository.findSchedulesByTrain(train,pageable);

        return getScheduleDtos(schedulePage);
    }

    @Override
    public Page<ScheduleDto> filterScheduleByFrequency(String frequency, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Schedule> schedulePage = scheduleRepository.findSchedulesByScheduleFrequency(ScheduleFrequency.valueOf(frequency),pageable);

        return getScheduleDtos(schedulePage);
    }

    @Override
    public String updateScheduleDetails(ScheduleDto scheduleDto) {
        System.out.println(scheduleDto.getScheduleFrequency());
        Schedule existingSchedule = scheduleRepository.findById(scheduleDto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        Train train = trainRepository.findByName(scheduleDto.getTrainName())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        // 3. Find Departure & Arrival stations
        Station departureStation = stationRepository.findByName(scheduleDto.getDepartureStation())
                .orElseThrow(() -> new RuntimeException("Departure Station not found"));

        Station arrivalStation = stationRepository.findByName(scheduleDto.getArrivalStation())
                .orElseThrow(() -> new RuntimeException("Arrival Station not found"));

        // 4. Conflict check (skip current schedule itself)
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
                        train,
                        scheduleDto.getMainDepartureTime(),
                        scheduleDto.getMainArrivalTime()
                ).stream()
                .filter(s -> !s.getScheduleId().equals(scheduleDto.getScheduleId()))
                .toList();

        if (!conflicts.isEmpty()) {
            throw new ScheduleConflictException("Conflict detected: Another schedule overlaps for this train.");
        }

        existingSchedule.setTrain(train);
        existingSchedule.setMainDepartureStation(departureStation);
        existingSchedule.setMainArrivalStation(arrivalStation);
        existingSchedule.setMainDepartureTime(scheduleDto.getMainDepartureTime());
        existingSchedule.setMainArrivalTime(scheduleDto.getMainArrivalTime());
        existingSchedule.setDescription(scheduleDto.getDescription());
        existingSchedule.setScheduleFrequency(ScheduleFrequency.valueOf(scheduleDto.getScheduleFrequency()));
        existingSchedule.setStatus(scheduleDto.isStatus());

        existingSchedule.getStops().clear(); // remove old stops
        List<ScheduleIntermediateStop> stops = scheduleDto.getStops().stream().map(stopDto -> {
            Station station = stationRepository.findByName(stopDto.getStationId())
                    .orElseThrow(() -> new RuntimeException("Stop Station not found"));

            return ScheduleIntermediateStop.builder()
                    .schedule(existingSchedule)
                    .station(station)
                    .stopOrder(stopDto.getStopOrder())
                    .arrivalTime(stopDto.getArrivalTime())
                    .departureTime(stopDto.getDepartureTime())
                    .build();
        }).toList();

        existingSchedule.getStops().addAll(stops);

        scheduleRepository.save(existingSchedule);

        return "Schedule has been successfully updated.";


    }

    private Page<ScheduleDto> getScheduleDtos(Page<Schedule> schedulePage) {
        return schedulePage.map(schedule -> {
            Duration duration = Duration.between(
                    schedule.getMainDepartureTime(),
                    schedule.getMainArrivalTime()
            );

            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;

            String formattedDuration = hours + "h " + minutes + "m";

            return ScheduleDto.builder()
                    .scheduleId(schedule.getScheduleId())
                    .trainName(schedule.getTrain().getName())
                    .trainId(schedule.getTrain().getTrainId())
                    .departureStation(schedule.getMainDepartureStation().getName())
                    .arrivalStation(schedule.getMainArrivalStation().getName())
                    .mainDepartureTime(schedule.getMainDepartureTime())
                    .mainArrivalTime(schedule.getMainArrivalTime())
                    .description(schedule.getDescription())
                    .scheduleFrequency(schedule.getScheduleFrequency().name())
                    .status(schedule.isStatus())
                    .duration(formattedDuration)
                    .stops(schedule.getStops().stream().map(stop -> new ScheduleIntermediateStopDto(
                            stop.getStopId(),
                            stop.getArrivalTime(),
                            stop.getDepartureTime(),
                            stop.getStopOrder(),
                            stop.getStation().getName()
                    )).toList())
                    .build();
        });
    }


}

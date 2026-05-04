package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.*;
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
import lk.ijse.raillankaprobackend.service.TicketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final TicketPriceService  ticketPriceService;

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

    @Override
    public long getActiveScheduleCount() {
        return scheduleRepository.countScheduleByStatus(true);
    }

    @Override
    public long getInactiveScheduleCount() {
        return scheduleRepository.countScheduleByStatus(false);
    }

    @Override
    public long getAllScheduleCount() {
        return scheduleRepository.count();
    }

    @Override
    public double getAverageDailyTrips() {
        return scheduleRepository.findAverageDailyTrips();
    }

    @Override
    public Map<String, Long> getScheduleCountsByFrequencies() {
        List<Object[]> scheduleCountsByFrequency = scheduleRepository.getScheduleCountsByFrequency();
        Map<String, Long> countsMap = new HashMap<>();

        for (Object[] scheduleCount : scheduleCountsByFrequency) {
            ScheduleFrequency frequencyEnum = (ScheduleFrequency) scheduleCount[0];
            String frequency = frequencyEnum.name();

            Long count = (Long) scheduleCount[1];

            countsMap.put(frequency, count);
        }

        return countsMap;
    }

    @Override
    public Page<TrainScheduleInfoDto> searchSchedules(SearchTrainDto searchTrainDto, int pageNo, int pageSize) {
        Station depStation = stationRepository.findByName(searchTrainDto.getDepartureStation())
                .orElseThrow(() -> new IllegalArgumentException("Departure Station not found"));

        Station desStation = stationRepository.findByName(searchTrainDto.getDestinationStation())
                .orElseThrow(() -> new IllegalArgumentException("Arrival Station not found"));

        List<Schedule> allSchedules = scheduleRepository.findSchedulesWithRelatedDetails(
                searchTrainDto.getDepartureStation(),
                searchTrainDto.getDestinationStation(),
                searchTrainDto.getDate()
        );

        List<TrainScheduleInfoDto> filteredDtos = allSchedules.stream()
                .filter(schedule -> isTrainRunningOn(searchTrainDto.getDate(), schedule.getScheduleFrequency().name()))
                .map(s -> {
                    String durationBetweenStations = getDurationBetweenStations(
                            s,
                            searchTrainDto.getDepartureStation(),
                            searchTrainDto.getDestinationStation()
                    );
                    if (durationBetweenStations == null) {
                        return null;
                    }

                    TrainScheduleInfoDto.AllCalculatedTicketPriceDto fullTicketPrices = ticketPriceService.calculatePrice(
                            s,
                            PriceCalcDto.builder()
                                    .departure(depStation.getName())
                                    .destination(desStation.getName())
                                    .adultCount(searchTrainDto.getAdultCount())
                                    .childCount(searchTrainDto.getChildCount())
                                    .build()
                    );
                    return TrainScheduleInfoDto.builder()
                            .scheduleId(s.getScheduleId())
                            .trainName(s.getTrain().getName())
                            .trainType(s.getTrain().getTrainType().name())
                            .trainClass(s.getTrain().getClasses())
                            .status(s.isStatus())
                            .date(DateTimeFormatter.ofPattern("MMM d, yyyy").format(searchTrainDto.getDate()))
                            .scheduleDescription(s.getDescription())
                            .fullScheduleDuration(getDuration(s.getMainDepartureTime(), s.getMainArrivalTime()))
                            .selectedDepartureStation(formatStationName(searchTrainDto.getDepartureStation()))
                            .selectedDestinationStation(formatStationName(searchTrainDto.getDestinationStation()))
                            .selectedScheduleDuration(durationBetweenStations)
                            .scheduleFrequency(s.getScheduleFrequency().name())
                            .departureStationName(s.getMainDepartureStation().getName())
                            .arrivalStationName(s.getMainArrivalStation().getName())
                            .departureStationFacilities(s.getMainDepartureStation().getOtherFacilities())
                            .arrivalStationFacilities(s.getMainArrivalStation().getOtherFacilities())
                            .mainDepartureTime(formatTime(s.getMainDepartureTime()))
                            .mainArrivalTime(formatTime(s.getMainArrivalTime()))
                            .selectedDepartureStationDepartureTime(formatTime(getStationDepartureTime(s, searchTrainDto.getDepartureStation())))
                            .selectedArrivalStationArrivalTime(formatTime(getStationArrivalTime(s, searchTrainDto.getDestinationStation())))
                            .intermediateStops(s.getStops().stream()
                                    .map(st -> new TrainScheduleInfoDto.IntermediateTrainScheduleInfoDto(
                                            st.getStation().getName(),
                                            st.getStation().getOtherFacilities(),
                                            formatTime(st.getArrivalTime()),
                                            formatTime(st.getDepartureTime()),
                                            st.getStopOrder()
                                    )).collect(Collectors.toList()))
                            .allCalculatedTicketPrice(fullTicketPrices)
                            .build();
                })
                .filter(e -> e != null)
                .collect(Collectors.toList());

        int start = Math.min((pageNo - 1) * pageSize, filteredDtos.size());
        int end = Math.min(start + pageSize, filteredDtos.size());
        List<TrainScheduleInfoDto> pageContent = filteredDtos.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredDtos.size());
    }


    @Override
    public Page<TrainScheduleInfoDto> searchSchedulesByTrainName(SearchTrainDto searchTrainDto, String trainName, int pageNo, int pageSize) {
        System.out.println(trainName);

        Station depStation = stationRepository.findByName(searchTrainDto.getDepartureStation())
                .orElseThrow(() -> new IllegalArgumentException("Departure Station not found"));


        Station desStation = stationRepository.findByName(searchTrainDto.getDestinationStation())
                .orElseThrow(() -> new IllegalArgumentException("Arrival Station not found"));

        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        List<Schedule> schedules = scheduleRepository.findSchedulesWithRelatedDetailsByTrainName(
                searchTrainDto.getDepartureStation(),
                searchTrainDto.getDestinationStation(),
                trainName,
                searchTrainDto.getDate()
        );

        List<TrainScheduleInfoDto> dtos = schedules.stream()
                .filter(schedule -> isTrainRunningOn(searchTrainDto.getDate(), schedule.getScheduleFrequency().name()))
                .map(s -> {

                    String durationBetweenStations = getDurationBetweenStations(
                            s,
                            searchTrainDto.getDepartureStation(),
                            searchTrainDto.getDestinationStation()
                    );
                    if (durationBetweenStations == null) {
                        return null;
                    }

                    TrainScheduleInfoDto.AllCalculatedTicketPriceDto fullTicketPrices = ticketPriceService.calculatePrice(
                            s,
                            PriceCalcDto.builder()
                                    .departure(depStation.getName())
                                    .destination(desStation.getName())
                                    .adultCount(searchTrainDto.getAdultCount())
                                    .childCount(searchTrainDto.getChildCount())
                                    .build()
                    );
                    return TrainScheduleInfoDto.builder()
                            .scheduleId(s.getScheduleId())
                            .trainName(s.getTrain().getName())
                            .trainType(s.getTrain().getTrainType().name())
                            .trainClass(s.getTrain().getClasses())
                            .status(s.isStatus())
                            .date(DateTimeFormatter.ofPattern("MMM d, yyyy").format(searchTrainDto.getDate()))
                            .scheduleDescription(s.getDescription())
                            .fullScheduleDuration(getDuration(s.getMainDepartureTime(), s.getMainArrivalTime()))
                            .selectedDepartureStation(formatStationName(searchTrainDto.getDepartureStation()))
                            .selectedDestinationStation(formatStationName(searchTrainDto.getDestinationStation()))
                            .selectedScheduleDuration(durationBetweenStations)
                            .scheduleFrequency(s.getScheduleFrequency().name())
                            .departureStationName(s.getMainDepartureStation().getName())
                            .arrivalStationName(s.getMainArrivalStation().getName())
                            .departureStationFacilities(s.getMainDepartureStation().getOtherFacilities())
                            .arrivalStationFacilities(s.getMainArrivalStation().getOtherFacilities())
                            .mainDepartureTime(formatTime(s.getMainDepartureTime()))
                            .mainArrivalTime(formatTime(s.getMainArrivalTime()))
                            .selectedDepartureStationDepartureTime(formatTime(getStationDepartureTime(s, searchTrainDto.getDepartureStation())))
                            .selectedArrivalStationArrivalTime(formatTime(getStationArrivalTime(s, searchTrainDto.getDestinationStation())))
                            .intermediateStops(s.getStops().stream()
                                    .map(st -> new TrainScheduleInfoDto.IntermediateTrainScheduleInfoDto(
                                            st.getStation().getName(),
                                            st.getStation().getOtherFacilities(),
                                            formatTime(st.getArrivalTime()),
                                            formatTime(st.getDepartureTime()),
                                            st.getStopOrder()
                                    )).collect(Collectors.toList()))
                            .allCalculatedTicketPrice(fullTicketPrices)
                            .build();
                })
                .filter(e -> e != null)
                .toList();

        int start = Math.min((pageNo - 1) * pageSize, dtos.size());
        int end = Math.min(start + pageSize, dtos.size());
        List<TrainScheduleInfoDto> pageContent = dtos.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), dtos.size());
    }

    @Override
    public Page<TrainScheduleInfoDto> searchSchedulesByTrainClass(SearchTrainDto searchTrainDto, String trainClass, int pageNo, int pageSize) {
        Station depStation = stationRepository.findByName(searchTrainDto.getDepartureStation())
                .orElseThrow(() -> new IllegalArgumentException("Departure Station not found"));


        Station desStation = stationRepository.findByName(searchTrainDto.getDestinationStation())
                .orElseThrow(() -> new IllegalArgumentException("Arrival Station not found"));

        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }

        List<Schedule> schedules = scheduleRepository.findSchedulesWithRelatedDetailsByClass(
                searchTrainDto.getDepartureStation(),
                searchTrainDto.getDestinationStation(),
                trainClass,
                searchTrainDto.getDate()

        );

        List<TrainScheduleInfoDto> dtos = schedules.stream()
                .filter(schedule -> isTrainRunningOn(searchTrainDto.getDate(), schedule.getScheduleFrequency().name()))
                .map(s -> {

                    String durationBetweenStations = getDurationBetweenStations(
                            s,
                            searchTrainDto.getDepartureStation(),
                            searchTrainDto.getDestinationStation()
                    );
                    if (durationBetweenStations == null) {
                        return null;
                    }

                    TrainScheduleInfoDto.AllCalculatedTicketPriceDto fullTicketPrices = ticketPriceService.calculatePrice(
                            s,
                            PriceCalcDto.builder()
                                    .departure(depStation.getName())
                                    .destination(desStation.getName())
                                    .adultCount(searchTrainDto.getAdultCount())
                                    .childCount(searchTrainDto.getChildCount())
                                    .build()
                    );
                    return TrainScheduleInfoDto.builder()
                            .scheduleId(s.getScheduleId())
                            .trainName(s.getTrain().getName())
                            .trainType(s.getTrain().getTrainType().name())
                            .trainClass(s.getTrain().getClasses())
                            .status(s.isStatus())
                            .date(DateTimeFormatter.ofPattern("MMM d, yyyy").format(searchTrainDto.getDate()))
                            .scheduleDescription(s.getDescription())
                            .fullScheduleDuration(getDuration(s.getMainDepartureTime(), s.getMainArrivalTime()))
                            .selectedDepartureStation(formatStationName(searchTrainDto.getDepartureStation()))
                            .selectedDestinationStation(formatStationName(searchTrainDto.getDestinationStation()))
                            .selectedScheduleDuration(durationBetweenStations)
                            .scheduleFrequency(s.getScheduleFrequency().name())
                            .departureStationName(s.getMainDepartureStation().getName())
                            .arrivalStationName(s.getMainArrivalStation().getName())
                            .departureStationFacilities(s.getMainDepartureStation().getOtherFacilities())
                            .arrivalStationFacilities(s.getMainArrivalStation().getOtherFacilities())
                            .mainDepartureTime(formatTime(s.getMainDepartureTime()))
                            .mainArrivalTime(formatTime(s.getMainArrivalTime()))
                            .selectedDepartureStationDepartureTime(formatTime(getStationDepartureTime(s, searchTrainDto.getDepartureStation())))
                            .selectedArrivalStationArrivalTime(formatTime(getStationArrivalTime(s, searchTrainDto.getDestinationStation())))
                            .intermediateStops(s.getStops().stream()
                                    .map(st -> new TrainScheduleInfoDto.IntermediateTrainScheduleInfoDto(
                                            st.getStation().getName(),
                                            st.getStation().getOtherFacilities(),
                                            formatTime(st.getArrivalTime()),
                                            formatTime(st.getDepartureTime()),
                                            st.getStopOrder()
                                    )).collect(Collectors.toList()))
                            .allCalculatedTicketPrice(fullTicketPrices)
                            .build();
                })
                .filter(e -> e != null)
                .toList();

        int start = Math.min((pageNo - 1) * pageSize, dtos.size());
        int end = Math.min(start + pageSize, dtos.size());
        List<TrainScheduleInfoDto> pageContent = dtos.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), dtos.size());
    }




    private LocalTime getStationDepartureTime(Schedule schedule, String stationName) {
        if (schedule.getMainDepartureStation().getName().equalsIgnoreCase(stationName)) {
            return schedule.getMainDepartureTime();
        }
        return schedule.getStops().stream()
                .filter(st -> st.getStation().getName().equalsIgnoreCase(stationName))
                .map(st -> st.getDepartureTime())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Departure station not found"));
    }

    private LocalTime getStationArrivalTime(Schedule schedule, String stationName) {
        if (schedule.getMainArrivalStation().getName().equalsIgnoreCase(stationName)) {
            return schedule.getMainArrivalTime();
        }
        return schedule.getStops().stream()
                .filter(st -> st.getStation().getName().equalsIgnoreCase(stationName))
                .map(st -> st.getArrivalTime())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Destination station not found"));
    }

    private String formatStationName(String station) {
        return station.toUpperCase().charAt(0) + station.substring(1).toLowerCase();
    }

    private String formatTime(LocalTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh.mm a");
        return localTime.format(formatter);
    }

    private String getDurationBetweenStations(Schedule schedule, String departureStation, String destinationStation) {
        LocalTime departureTime = null;
        LocalTime arrivalTime = null;

        // 1. Departure station check
        if (schedule.getMainDepartureStation().getName().equalsIgnoreCase(departureStation)) {
            departureTime = schedule.getMainDepartureTime();
        } else {
            departureTime = schedule.getStops().stream()
                    .filter(st -> st.getStation().getName().equalsIgnoreCase(departureStation))
                    .map(st -> st.getDepartureTime())
                    .findFirst()
                    .orElse(null);
        }

        // 2. Destination station check
        if (schedule.getMainArrivalStation().getName().equalsIgnoreCase(destinationStation)) {
            arrivalTime = schedule.getMainArrivalTime();
        } else {
            arrivalTime = schedule.getStops().stream()
                    .filter(st -> st.getStation().getName().equalsIgnoreCase(destinationStation))
                    .map(st -> st.getArrivalTime())
                    .findFirst()
                    .orElse(null);
        }

        if (departureTime == null) {
            throw new IllegalArgumentException("Departure stop not found");
        }
        if (arrivalTime == null) {
            throw new IllegalArgumentException("Destination stop not found");
        }

        System.out.println("Departure time: " + departureTime);
        System.out.println("Arrival time: " + arrivalTime);

        Duration duration = Duration.between(departureTime, arrivalTime);

        if (duration.isNegative()) {
            System.out.println("Ignoring schedule: destination comes before departure");
            return null;
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return hours + " h " + minutes + " m";
    }


    private String getDuration(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(
                startTime,
                endTime
        );
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return hours + " hours " + minutes + " minutes";
    }
    private boolean isTrainRunningOn(LocalDate date, String frequency) {
        DayOfWeek day = date.getDayOfWeek();

        return switch (frequency) {
            case "DAILY" -> true;
            case "WEEK_DAYS" -> !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            case "WEEK_ENDS" -> (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            default -> true;
        };
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

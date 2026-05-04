package lk.ijse.raillankaprobackend.util;

import lk.ijse.raillankaprobackend.entity.Schedule;
import lk.ijse.raillankaprobackend.entity.ScheduleIntermediateStop;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Component
@RequiredArgsConstructor
public class StationSequenceUtil {
    private final StationRepository stationRepository;

    public List<Station> getStationSequence(Schedule schedule, String departureStationName, String destinationStationName) {
        Station departureStation = stationRepository.findByName(departureStationName)
                .orElseThrow(() -> new IllegalArgumentException("Departure Station not found"));
        Station destinationStation = stationRepository.findByName(destinationStationName)
                .orElseThrow(() -> new IllegalArgumentException("Destination Station not found"));

        List<Station> stationSequence = new ArrayList<>();
        stationSequence.add(schedule.getMainDepartureStation());

        List<ScheduleIntermediateStop> stops = schedule.getStops().stream()
                .sorted(Comparator.comparingInt(stop -> stop.getStopOrder()))
                .collect(Collectors.toList());

        for (ScheduleIntermediateStop stop : stops) {
            stationSequence.add(stop.getStation());
        }

        stationSequence.add(schedule.getMainArrivalStation());

        int startIndex = stationSequence.indexOf(departureStation);
        int endIndex = stationSequence.indexOf(destinationStation);
        System.out.println(departureStationName);
        System.out.println(destinationStationName);
        System.out.println("///////////////////////");
        for (Station station : stationSequence){
            System.out.println(station.getName());
        }

        System.out.println("start index" +startIndex);
        System.out.println("end index" + endIndex);

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            throw new IllegalArgumentException("Invalid departure or destination station for this schedule");
        }

        return stationSequence.subList(startIndex, endIndex + 1);
    }

}

package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.SeatSelectionDto;
import lk.ijse.raillankaprobackend.entity.Dtypes.CarriageNumber;
import lk.ijse.raillankaprobackend.entity.FirstClassBookingSeat;
import lk.ijse.raillankaprobackend.entity.Schedule;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.FirstClassBookingSeatRepository;
import lk.ijse.raillankaprobackend.repository.ScheduleRepository;
import lk.ijse.raillankaprobackend.service.FirstClassBookingSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class FirstClassBookingSeatServiceImpl implements FirstClassBookingSeatService {

    private final FirstClassBookingSeatRepository firstClassBookingSeatRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public String generateNewFirstClassBookingSeatId() {
        String lastId = firstClassBookingSeatRepository.getLastBookingId().orElse("BKS00000-00001");
        String[] split = lastId.split("-");
        int prefixNumber = Integer.parseInt(split[0].substring(3));
        int suffixNumber = Integer.parseInt(split[1]);
        suffixNumber++;
        if (suffixNumber > 99999){
            suffixNumber = 1;
            prefixNumber++;
            if (prefixNumber > 99999){
                throw new IdGenerateLimitReachedException("All available First Class Booking Seat IDs have been used. Please contact the system administrator");
            }
        }
        return  String.format("BKS%05d-%05d", prefixNumber, suffixNumber);
    }

    @Override
    public FirstClassBookingSeat saveFirstClassBookingSeat(FirstClassBookingSeat firstClassBookingSeat) {
        return firstClassBookingSeatRepository.save(firstClassBookingSeat);
    }

    @Override
    public List<SeatSelectionDto> getAllBookingSeatsByTravelDateAndSchedule(LocalDate travelDate, String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new RuntimeException("Schedule not found"));
        List<FirstClassBookingSeat> byTravelDateAndSchedule = firstClassBookingSeatRepository.findByTravelDateAndSchedule(travelDate, schedule);
        for (FirstClassBookingSeat fcb : byTravelDateAndSchedule) {
            System.out.println(fcb.getSeatPosition() +"=" + fcb.getCarriageNumber());
        }

        return byTravelDateAndSchedule.stream()
                .map(fcb -> {
                    String carriage = fcb.getCarriageNumber() == CarriageNumber.ONE ? "1" : "2";

                    String row = fcb.getRowLetter().name();

                    String position = switch (fcb.getSeatPosition()) {
                        case ONE -> "1";
                        case TWO -> "2";
                        case THREE -> "3";
                        case FOUR -> "4";
                    };

                    return SeatSelectionDto.builder()
                            .carriage(carriage)
                            .seat(row + position)
                            .build();
                })
                .toList();


    }
}

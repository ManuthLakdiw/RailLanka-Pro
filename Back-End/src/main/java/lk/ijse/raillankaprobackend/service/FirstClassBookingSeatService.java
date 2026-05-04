package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.SeatSelectionDto;
import lk.ijse.raillankaprobackend.entity.FirstClassBookingSeat;
import lk.ijse.raillankaprobackend.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface FirstClassBookingSeatService {

    String generateNewFirstClassBookingSeatId();

    FirstClassBookingSeat saveFirstClassBookingSeat(FirstClassBookingSeat firstClassBookingSeat);

    List<SeatSelectionDto> getAllBookingSeatsByTravelDateAndSchedule(LocalDate travelDate, String scheduleId);
}

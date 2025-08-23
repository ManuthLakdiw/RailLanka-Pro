package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface CounterService {

    String registerCounter(CounterDto counterDto);

    String generateNewCounterId();

    List<String> getCounterNumberByStationName(String stationName);

    Page<CounterDto> getAllCounters(int pageNo, int pageSize);

    String changeCounterStatus(String counterId, boolean status);

    String deleteCounter(String counterId);

    Page<CounterDto> filterCountersByKeyword(String keyword, int pageNo, int pageSize);

    Optional<CounterDto> findCounterById(String counterId);

    String updateCounterDetails(CounterDto counterDto);
}

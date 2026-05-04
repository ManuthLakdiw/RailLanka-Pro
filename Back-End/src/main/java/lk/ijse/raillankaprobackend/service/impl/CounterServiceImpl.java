package lk.ijse.raillankaprobackend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.CounterNumber;
import lk.ijse.raillankaprobackend.entity.Dtypes.SystemUserRole;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.CounterRepository;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.CounterService;
import lk.ijse.raillankaprobackend.service.EmailService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class CounterServiceImpl implements CounterService {

    private final CounterRepository counterRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StationRepository stationRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public String registerCounter(CounterDto counterDto) {

        System.out.println(counterDto.getCounterNumber());

        if (userRepository.findByUsername(counterDto.getUserName()).isPresent()){
            throw new UserNameAlreadyExistsException("This username is already taken. Please choose a different one.");
        }

        Station station = stationRepository.findByName(counterDto.getRailwayStation()).orElseThrow(
                () -> new IllegalArgumentException("This railway station does not exist."));


        User user = User.builder()
                .userId(userService.generateNewUserId())
                .username(counterDto.getUserName())
                .password(passwordEncoder.encode(counterDto.getPassword()))
                .role(SystemUserRole.COUNTER)
                .createdDate(LocalDate.now())
                .build();

        userRepository.save(user);

        String formattedFirstName = counterDto.getFirstname().substring(0, 1).toUpperCase() +
                counterDto.getFirstname().substring(1).toLowerCase();

        String formattedLastName = counterDto.getLastname().substring(0, 1).toUpperCase() +
                counterDto.getLastname().substring(1).toLowerCase();

        Counter counter = Counter.builder()
                .counterId(generateNewCounterId())
                .firstname(formattedFirstName)
                .lastname(formattedLastName)
                .idNumber(counterDto.getIdNumber())
                .phoneNumber(counterDto.getPhoneNumber())
                .email(counterDto.getEmail())
                .address(counterDto.getAddress())
                .dob(counterDto.getDob())
                .yearsOfExperience(counterDto.getYearsOfExperience())
                .counterNumber(CounterNumber.valueOf(counterDto.getCounterNumber()))
                .active(true)
                .user(user)
                .station(station)
                .build();
        counterRepository.save(counter);

        new Thread(() -> {
            emailService.sendCounterCredentials(
                    "counter-credential-email",
                    counterDto ,
                    counter.getCounterId()
                    );
        }).start();

        return "Counter has been registered successfully";
    }

    @Override
    public String generateNewCounterId() {
        if (counterRepository.getLastCounterId().isPresent()){
            String lastCounterId = counterRepository.getLastCounterId().get();
            String[] split = lastCounterId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available Counter IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("CTR%05d-%05d", prefixNumber, suffixNumber);
        }
        return "CTR00000-00001";

    }

    @Override
    public List<String> getCounterNumberByStationName(String stationName) {

        if (stationRepository.findByName(stationName).isEmpty()){
            throw new IllegalArgumentException("This railway station does not exist.");
        }

        return counterRepository.findCounterNumberByStationName(stationName);
    }

    @Override
    public Page<CounterDto> getAllCounters(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Counter> counterPage = counterRepository.findAll(pageable);



        return counterPage.map(counter -> {
            String phoneNumber = counter.getPhoneNumber();
            String formattedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
            CounterDto dto = new CounterDto();
            dto.setId(counter.getCounterId());
            dto.setFirstname(counter.getFirstname());
            dto.setLastname(counter.getLastname());
            dto.setIdNumber(counter.getIdNumber());
            dto.setPhoneNumber(formattedPhoneNumber);
            dto.setEmail(counter.getEmail());
            dto.setAddress(counter.getAddress());
            dto.setDob(counter.getDob());
            dto.setYearsOfExperience(counter.getYearsOfExperience());
            dto.setActive(counter.isActive());
            dto.setCounterNumber(counter.getCounterNumber().toString());

            if (counter.getUser() != null) {
                dto.setUserName(counter.getUser().getUsername());
            }

            if (counter.getStation() != null) {
                dto.setRailwayStation(counter.getStation().getName()+","+counter.getStation().getStationCode());
            }
            return dto;
        });
    }

    @Override
    public String changeCounterStatus(String counterId, boolean status) {
        counterRepository.findById(counterId).orElseThrow(
                () -> new IllegalArgumentException("Counter not found for ID: " + counterId));
        counterRepository.updateCounterStatus(counterId, status);

        return "Counter has been successfully set to " + (status ? "Active" : "Inactive");
    }

    @Override
    public String deleteCounter(String counterId) {
        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Station Master not found for ID: " + counterId));

        if (counter.getUser() != null) {
            userRepository.delete(counter.getUser());
        }

        counterRepository.delete(counter);

        return "Counter has been successfully deleted.";
    }

    @Override
    public Page<CounterDto> filterCountersByKeyword(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Counter> counterPage = counterRepository.filterCountersByKeyword(keyword, pageable);

        return counterPage.map(counter -> {
            String phoneNumber = counter.getPhoneNumber();
            String formattedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
            CounterDto dto = new CounterDto();
            dto.setId(counter.getCounterId());
            dto.setFirstname(counter.getFirstname());
            dto.setLastname(counter.getLastname());
            dto.setIdNumber(counter.getIdNumber());
            dto.setPhoneNumber(formattedPhoneNumber);
            dto.setEmail(counter.getEmail());
            dto.setAddress(counter.getAddress());
            dto.setDob(counter.getDob());
            dto.setYearsOfExperience(counter.getYearsOfExperience());
            dto.setCounterNumber(String.valueOf(counter.getCounterNumber()));
            dto.setActive(counter.isActive());


            if (counter.getUser() != null) {
                dto.setUserName(counter.getUser().getUsername());
            }

            if (counter.getStation() != null) {
                dto.setRailwayStation(counter.getStation().getName()+","+counter.getStation().getStationCode());
            }
            return dto;
        });
    }

    @Override
    public Optional<CounterDto> findCounterById(String counterId) {
        Counter counter = counterRepository.findById(counterId).orElseThrow(
                () -> new IllegalArgumentException("Counter not found for ID: " + counterId));

        CounterDto dto = new CounterDto();

        dto.setId(counter.getCounterId());
        dto.setFirstname(counter.getFirstname());
        dto.setLastname(counter.getLastname());
        dto.setIdNumber(counter.getIdNumber());
        dto.setPhoneNumber(counter.getPhoneNumber());
        dto.setEmail(counter.getEmail());
        dto.setAddress(counter.getAddress());
        dto.setDob(counter.getDob());
        dto.setYearsOfExperience(counter.getYearsOfExperience());
        dto.setCounterNumber(counter.getCounterNumber().toString());
        dto.setActive(counter.isActive());
        dto.setUserName(counter.getUser().getUsername());
        dto.setRailwayStation(counter.getStation().getName());

        return Optional.of(dto);

    }

    @Override
    public String updateCounterDetails(CounterDto counterDto) {
        if (counterRepository.findById(counterDto.getId()).isPresent()){
            Counter counter = counterRepository.findById(counterDto.getId()).get();
            counter.setFirstname(counterDto.getFirstname());
            counter.setLastname(counterDto.getLastname());
            counter.setIdNumber(counterDto.getIdNumber());
            counter.setPhoneNumber(counterDto.getPhoneNumber());
            counter.setEmail(counterDto.getEmail());
            counter.setAddress(counterDto.getAddress());
            counter.setDob(counterDto.getDob());
            counter.setYearsOfExperience(counterDto.getYearsOfExperience());
            counter.setCounterNumber(CounterNumber.valueOf(counterDto.getCounterNumber()));
            counter.setActive(counterDto.isActive());
            counter.setStation(stationRepository.findByName(counterDto.getRailwayStation()).orElseThrow(
                    () -> new IllegalArgumentException("This railway station does not exist.")));
            counterRepository.save(counter);
            return "Counter details has been updated successfully";

        }
        throw new IllegalArgumentException("Counter not found for ID: " + counterDto.getId());

    }

    @Override
    public Map<String, Long> getCounterStaffCount() {
        return Map.of(
                "total", counterRepository.count(),
                "active", counterRepository.countCounterByActive(true),
                "inactive", counterRepository.countCounterByActive(false)
        );

    }

    @Override
    public List<Map<String, Object>> getCounterCountByProvince() {
        List<Object[]> results = counterRepository.findCounterCountByProvince();

        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("province", (String) row[0]);
                    map.put("counterStaffCount", ((Number) row[1]).longValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}

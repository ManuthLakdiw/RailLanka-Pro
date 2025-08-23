package lk.ijse.raillankaprobackend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.CounterRepository;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.CounterService;
import lk.ijse.raillankaprobackend.service.EmailService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class CounterServiceImpl implements CounterService {

    private final CounterRepository counterRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StationRepository stationRepository;
    private final EmailService emailService;

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
                    "CounterRegTemplate",
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
}

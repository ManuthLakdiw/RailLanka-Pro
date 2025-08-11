package lk.ijse.raillankaprobackend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.entity.Counter;
import lk.ijse.raillankaprobackend.entity.StationMaster;
import lk.ijse.raillankaprobackend.entity.SystemUserRole;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.CounterRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.CounterService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    @Transactional
    @Override
    public String registerCounter(StaffDto staffDto) {
        if (userRepository.findByUsername(staffDto.getUserName()).isPresent()){
            throw new UserNameAlreadyExistsException("User name already exists");
        }

        User user = User.builder()
                .userId(userService.generateNewUserId())
                .username(staffDto.getUserName())
                .password(passwordEncoder.encode(staffDto.getPassword()))
                .role(SystemUserRole.COUNTER)
                .createdDate(LocalDate.now())
                .build();

        userRepository.save(user);

        Counter counter = Counter.builder()
                .counterId(generateNewCounterId())
                .title(staffDto.getTitle())
                .name(staffDto.getName())
                .idNumber(staffDto.getIdNumber())
                .phoneNumber(staffDto.getPhoneNumber())
                .email(staffDto.getEmail())
                .active(true)
                .user(user)
                .build();
        counterRepository.save(counter);

        return "Counter Registered Successfully";
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
}

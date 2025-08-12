package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.entity.Admin;
import lk.ijse.raillankaprobackend.entity.StationMaster;
import lk.ijse.raillankaprobackend.entity.SystemUserRole;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.StationMasterRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.StationMasterService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Service
@RequiredArgsConstructor
public class StationMasterServiceImpl implements StationMasterService {

    private final StationMasterRepository stationMasterRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public String registerStationMaster(StaffDto staffDto) {
        if (userRepository.findByUsername(staffDto.getUserName()).isPresent()){
            throw new UserNameAlreadyExistsException("This username is already taken. Please choose a different one.");
        }

        User user = User.builder()
                .userId(userService.generateNewUserId())
                .username(staffDto.getUserName())
                .password(passwordEncoder.encode(staffDto.getPassword()))
                .role(SystemUserRole.STATION_MASTER)
                .createdDate(LocalDate.now())
                .build();

        userRepository.save(user);


        StationMaster stationMaster = StationMaster.builder()
                .stationMasterId(generateNewStationMasterId())
                .title(staffDto.getTitle())
                .name(staffDto.getName())
                .idNumber(staffDto.getIdNumber())
                .phoneNumber(staffDto.getPhoneNumber())
                .email(staffDto.getEmail())
                .active(true)
                .user(user)
                .build();

        stationMasterRepository.save(stationMaster);

        return "Station Master Registered Successfully";
    }

    @Override
    public String generateNewStationMasterId() {

        if (stationMasterRepository.getLastStationMasterId().isPresent()) {
            String lastStationMasterId = stationMasterRepository.getLastStationMasterId().get();
            String[] split = lastStationMasterId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;
                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available Station Master IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("STM%05d-%05d", prefixNumber, suffixNumber);
        }
        return "STM00000-00001";
    }
}

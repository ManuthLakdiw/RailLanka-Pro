package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.EmployeePosition;
import lk.ijse.raillankaprobackend.entity.Dtypes.SystemUserRole;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.EmployeeRepository;
import lk.ijse.raillankaprobackend.repository.StationMasterRepository;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.EmailService;
import lk.ijse.raillankaprobackend.service.EmployeeService;
import lk.ijse.raillankaprobackend.service.StationMasterService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final StationRepository stationRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public String registerStationMaster(StaffDto staffDto) {
        if (userRepository.findByUsername(staffDto.getUserName()).isPresent()){
            throw new UserNameAlreadyExistsException("This username is already taken. Please choose a different one.");
        }

        Station station = stationRepository.findByName(staffDto.getRailwayStation()).orElseThrow(
                () -> new IllegalArgumentException("This railway station does not exist."));

        User user = User.builder()
                .userId(userService.generateNewUserId())
                .username(staffDto.getUserName())
                .password(passwordEncoder.encode(staffDto.getPassword()))
                .role(SystemUserRole.STATION_MASTER)
                .createdDate(LocalDate.now())
                .build();

        userRepository.save(user);

        String formattedFirstName = staffDto.getFirstname().substring(0, 1).toUpperCase() +
                staffDto.getFirstname().substring(1).toLowerCase();

        String formattedLastName = staffDto.getLastname().substring(0, 1).toUpperCase() +
                staffDto.getLastname().substring(1).toLowerCase();


        StationMaster stationMaster = StationMaster.builder()
                .stationMasterId(generateNewStationMasterId())
                .firstname(formattedFirstName)
                .lastname(formattedLastName)
                .idNumber(staffDto.getIdNumber())
                .phoneNumber(staffDto.getPhoneNumber())
                .email(staffDto.getEmail())
                .address(staffDto.getAddress())
                .dob(staffDto.getDob())
                .yearsOfExperience(staffDto.getYearsOfExperience())
                .active(true)
                .user(user)
                .station(station)
                .build();

        stationMasterRepository.save(stationMaster);

        new Thread(() -> {
            emailService.sendStationMasterCredentials(
                    "StationMasterRegTemplate",
                    staffDto ,
                    stationMaster.getStationMasterId()
            );

        }).start();

        return "Station Master has been Registered Successfully";

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

    @Override
    public Page<StaffDto> getAllStationMasters(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<StationMaster> stationMasterPage = stationMasterRepository.findAll(pageable);



        return stationMasterPage.map(stationMaster -> {
            String phoneNumber = stationMaster.getPhoneNumber();
            String formattedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
            StaffDto dto = new StaffDto();
            dto.setId(stationMaster.getStationMasterId());
            dto.setFirstname(stationMaster.getFirstname());
            dto.setLastname(stationMaster.getLastname());
            dto.setIdNumber(stationMaster.getIdNumber());
            dto.setPhoneNumber(formattedPhoneNumber);
            dto.setEmail(stationMaster.getEmail());
            dto.setAddress(stationMaster.getAddress());
            dto.setDob(stationMaster.getDob());
            dto.setYearsOfExperience(stationMaster.getYearsOfExperience());
            dto.setActive(stationMaster.isActive());

            if (stationMaster.getUser() != null) {
                dto.setUserName(stationMaster.getUser().getUsername());
            }

            if (stationMaster.getStation() != null) {
                dto.setRailwayStation(stationMaster.getStation().getName()+","+stationMaster.getStation().getStationCode());
            }
            return dto;
        });

    }

    @Override
    public List<String> getAllAssignedStations() {
        return stationMasterRepository.getAllAssignedStations();
    }

    @Override
    public String changeStationMasterStatus(String stationMasterId, boolean status) {
        stationMasterRepository.findById(stationMasterId)
                .orElseThrow(() -> new IllegalArgumentException("Station Master not found for ID: " + stationMasterId));
        stationMasterRepository.updateStationMasterStatus(stationMasterId, status);

        return "Station Master has been successfully set to " + (status ? "Active" : "Inactive");

    }

    @Override
    @Transactional
    public String deleteStationMaster(String stationMasterId) {
        StationMaster stationMaster = stationMasterRepository.findById(stationMasterId)
                .orElseThrow(() -> new IllegalArgumentException("Station Master not found for ID: " + stationMasterId));

        if (stationMaster.getUser() != null) {
            userRepository.delete(stationMaster.getUser());
        }

        if (stationMaster.getStation() != null) {
            stationMaster.getStation().setStationMaster(null);
        }

        stationMasterRepository.delete(stationMaster);

        return "Station Master has been successfully deleted.";
    }

    @Override
    public Page<StaffDto> filterStationMastersByKeyword(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<StationMaster> stationMasterPage = stationMasterRepository.filterStationsByKeyword(keyword, pageable);

        return stationMasterPage.map(stationMaster -> {
            String phoneNumber = stationMaster.getPhoneNumber();
            String formattedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
            StaffDto dto = new StaffDto();
            dto.setId(stationMaster.getStationMasterId());
            dto.setFirstname(stationMaster.getFirstname());
            dto.setLastname(stationMaster.getLastname());
            dto.setIdNumber(stationMaster.getIdNumber());
            dto.setPhoneNumber(formattedPhoneNumber);
            dto.setEmail(stationMaster.getEmail());
            dto.setAddress(stationMaster.getAddress());
            dto.setDob(stationMaster.getDob());
            dto.setYearsOfExperience(stationMaster.getYearsOfExperience());
            dto.setActive(stationMaster.isActive());

            if (stationMaster.getUser() != null) {
                dto.setUserName(stationMaster.getUser().getUsername());
            }

            if (stationMaster.getStation() != null) {
                dto.setRailwayStation(stationMaster.getStation().getName()+","+stationMaster.getStation().getStationCode());
            }
            return dto;
        });
    }

    @Override
    public String updateStationMasterDetails(StaffDto staffDto) {
        if (stationMasterRepository.findById(staffDto.getId()).isPresent()){
            StationMaster stationMaster = stationMasterRepository.findById(staffDto.getId()).get();

            Station station = stationRepository.findByName(staffDto.getRailwayStation()).orElseThrow(
                    () -> new IllegalArgumentException("This railway station does not exist."));

            String formattedFirstName = staffDto.getFirstname().substring(0, 1).toUpperCase() +
                    staffDto.getFirstname().substring(1).toLowerCase();

            String formattedLastName = staffDto.getLastname().substring(0, 1).toUpperCase() +
                    staffDto.getLastname().substring(1).toLowerCase();

            stationMaster.setFirstname(formattedFirstName);
            stationMaster.setLastname(formattedLastName);
            stationMaster.setIdNumber(staffDto.getIdNumber());
            stationMaster.setDob(staffDto.getDob());
            stationMaster.setPhoneNumber(staffDto.getPhoneNumber());
            stationMaster.setEmail(staffDto.getEmail());
            stationMaster.setAddress(staffDto.getAddress());
            stationMaster.setYearsOfExperience(staffDto.getYearsOfExperience());
            stationMaster.setStation(station);
            stationMaster.setActive(staffDto.isActive());

            stationMasterRepository.save(stationMaster);

            return "Station Master details updated successfully.";
        }
        throw new IllegalArgumentException("Station Master not found for ID: " + staffDto.getId());
    }

    @Override
    public Optional<StaffDto> findStationMasterById(String stationMasterId) {
        StationMaster stationMaster = stationMasterRepository.findById(stationMasterId).orElseThrow(
                () -> new IllegalArgumentException("Station Master not found for ID: " + stationMasterId));

        StaffDto dto = new StaffDto();


        dto.setId(stationMaster.getStationMasterId());
        dto.setFirstname(stationMaster.getFirstname());
        dto.setLastname(stationMaster.getLastname());
        dto.setIdNumber(stationMaster.getIdNumber());
        dto.setPhoneNumber(stationMaster.getPhoneNumber());
        dto.setEmail(stationMaster.getEmail());
        dto.setAddress(stationMaster.getAddress());
        dto.setDob(stationMaster.getDob());
        dto.setYearsOfExperience(stationMaster.getYearsOfExperience());
        dto.setActive(stationMaster.isActive());

        if (stationMaster.getUser() != null) {
            dto.setUserName(stationMaster.getUser().getUsername());
        }

        if (stationMaster.getStation() != null) {
            dto.setRailwayStation(stationMaster.getStation().getName());
        }

        return Optional.of(dto);


    }

    @Override
    public long getAllStationMastersCount() {
        return stationMasterRepository.count();
    }

    @Override
    public long getActiveStationMastersCount() {
        return stationMasterRepository.countStationMasterByActive(true);
    }

    @Override
    public long getInactiveStationMastersCount() {
        return stationMasterRepository.countStationMasterByActive(false);
    }

    @Override
    public Double getAverageExperienceOfActiveStationMasters() {
        return stationMasterRepository.findAverageExperienceOfActiveMasters();
    }

    @Override
    public Map<String, Long> getStationMasterCountByProvince() {
        List<Object[]> results = stationMasterRepository.findStationMasterCountByProvince();
        Map<String, Long> provinceCounts = new HashMap<>();

        for (Object[] row : results) {
            String province = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            provinceCounts.put(province, count);
        }

        return provinceCounts;
    }

}

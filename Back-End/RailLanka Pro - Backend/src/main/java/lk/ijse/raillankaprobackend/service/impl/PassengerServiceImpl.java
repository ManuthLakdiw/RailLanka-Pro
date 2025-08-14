package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.PassengerRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.PassengerService;
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
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public String registerPassenger(PassengerDto passengerDto) {

        if (userRepository.findByUsername(passengerDto.getUsername()).isPresent()){
            throw new UserNameAlreadyExistsException("This username is already taken. Please choose a different one.");
        }

        User user = User.builder()
                .userId(userService.generateNewUserId())
                .username(passengerDto.getUsername())
                .password(passwordEncoder.encode(passengerDto.getPassword()))
                .role(SystemUserRole.PASSENGER)
                .createdDate(LocalDate.now())
                .build();

        userRepository.save(user);

        IdType idTypeForPassenger;
        if (passengerDto.getPassengerType().equals(PassengerType.FOREIGN.name())) {
            idTypeForPassenger = IdType.PASSPORT;
        } else {
            idTypeForPassenger = IdType.NIC;
        }

        Passenger passenger = Passenger.builder()
                .passengerId(generateNewPassengerId())
                .title(passengerDto.getTitle())
                .firstName(passengerDto.getFirstName())
                .lastName(passengerDto.getLastName())
                .passengerType(PassengerType.valueOf(passengerDto.getPassengerType()))
                .idtype(idTypeForPassenger)
                .idNumber(passengerDto.getIdNumber())
                .phoneNumber(passengerDto.getPhoneNumber())
                .email(passengerDto.getEmail())
                .blocked(false)
                .user(user)
                .build();

        passengerRepository.save(passenger);

        return "Your account has been created successfully.";
    }


    @Override
    public String generateNewPassengerId() {

        if (passengerRepository.getLastPassengerId().isPresent()){
            String lastPassengerId = passengerRepository.getLastPassengerId().get();
            String[] split = lastPassengerId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);
            suffixNumber++;
            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;
                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available Passenger IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("PSG%05d-%05d", prefixNumber, suffixNumber);

        }

        return "PSG00000-00001";
    }
}

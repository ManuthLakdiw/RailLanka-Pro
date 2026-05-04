package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.dto.ChangePasswordDto;
import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.*;
import lk.ijse.raillankaprobackend.exception.EmailAlreadyExistsException;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.BookingRepository;
import lk.ijse.raillankaprobackend.repository.PassengerRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.PassengerService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    private final BookingRepository bookingRepository;


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
                .firstName(formattedName(passengerDto.getFirstName()))
                .lastName(formattedName(passengerDto.getLastName()))
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


    private String formattedName (String name){
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
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

    @Override
    public Page<PassengerDto> getAllPassengers(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Passenger> passengerPage = passengerRepository.findAll(pageable);

        return getPassengerDtos(passengerPage);

    }

    @Override
    public String changePassengerStatus(String passengerId, boolean status) {
        Passenger passenger = passengerRepository.findById(passengerId).
                orElseThrow(() -> new IllegalArgumentException("Passenger not found for ID: " + passengerId));

        passenger.setBlocked(status);
        passengerRepository.save(passenger);

        return "Passenger has been successfully set to " + (status ? "Block" : "Active") + " status." ;

    }

    @Override
    public Page<PassengerDto> filterPassengerByKeyword(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Passenger> passengerPage = passengerRepository.filterPassengerByKeyword(keyword,pageable);

        return getPassengerDtos(passengerPage);
    }

    @Override
    public Page<PassengerDto> filterPassengerByStatus(boolean status, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Passenger> passengerPage = passengerRepository.findPassengerByBlocked(status,pageable);

        return getPassengerDtos(passengerPage);

    }

    @Override
    public Page<PassengerDto> filterPassengerByPassengerType(String passengerType, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Passenger> passengerByPassengerType = passengerRepository
                .findPassengerByPassengerType(PassengerType.valueOf(passengerType), pageable);

        return getPassengerDtos(passengerByPassengerType);
    }

    @Override
    public PassengerDto getPassengerDetailsByPassengerId(String passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found for ID: " + passengerId));
        String formattedPhoneNumber = passenger.getPhoneNumber().substring(0, 3) + "-" + passenger.getPhoneNumber().substring(3);
        return PassengerDto.builder()
                .passengerId(passenger.getPassengerId())
                .title(passenger.getTitle()+".")
                .firstName(formattedName(passenger.getFirstName()))
                .lastName(formattedName(passenger.getLastName()))
                .passengerType(passenger.getPassengerType().name())
                .idType(passenger.getIdtype().name())
                .idNumber(passenger.getIdNumber())
                .phoneNumber(formattedPhoneNumber)
                .email(passenger.getEmail())
                .blocked(passenger.isBlocked())
                .username(passenger.getUser().getUsername())
                .build();
    }
    @Transactional
    @Override
    public String updatePassengerDetailsByUserName(String userName, PassengerDto passengerDto) {
        User user = userRepository.findByUsername(userName).
                orElseThrow(() -> new IllegalArgumentException("User not found for username: " + userName));
        String newEmail = passengerDto.getEmail();


        Optional<Passenger> existingPassengerByEmail = passengerRepository.findByEmail(newEmail);
        if (existingPassengerByEmail.isPresent() && !existingPassengerByEmail.get().getPassengerId().equals(user.getPassenger().getPassengerId())) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        user.getPassenger().setTitle(passengerDto.getTitle());
        user.getPassenger().setFirstName(formattedName(passengerDto.getFirstName()));
        user.getPassenger().setLastName(formattedName(passengerDto.getLastName()));
        user.getPassenger().setPhoneNumber(passengerDto.getPhoneNumber());
        user.getPassenger().setEmail(passengerDto.getEmail());

        userRepository.save(user);

        return "Profile has been updated successfully.";
    }

    @Override
    public PassengerDto getPassengerDetailsByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + userName));
        return PassengerDto.builder()
                .passengerId(user.getPassenger().getPassengerId())
                .title(user.getPassenger().getTitle()+".")
                .firstName(formattedName(user.getPassenger().getFirstName()))
                .lastName(formattedName(user.getPassenger().getLastName()))
                .email(user.getPassenger().getEmail())
                .passengerType(user.getPassenger().getPassengerType().name())
                .idType(user.getPassenger().getIdtype().name())
                .idNumber(user.getPassenger().getIdNumber())
                .phoneNumber(user.getPassenger().getPhoneNumber())
                .username(user.getUsername())
                .build();
    }

    @Override
    public boolean ChangePassword(ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(changePasswordDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + changePasswordDto.getUsername()));

        String currentPassword = changePasswordDto.getCurrentPassword();
        String password = user.getPassword();

        if (passwordEncoder.matches(currentPassword, password)){
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public List<BookingDto> getBookingDetailsByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + userName));
        List<BookingDto> bookingDtoList = new ArrayList<>();
        bookingRepository.findByPassenger(user.getPassenger()).forEach(booking -> {
            String formattedTravelClass;
            TravelClass travelClass = booking.getTravelClass();
            formattedTravelClass = switch (travelClass){
                case FIRST -> "1st Class";
                case SECOND -> "2nd Class";
                case THIRD -> "3rd Class";
            };

            String formattedTrainType;
            TrainType trainType = booking.getSchedule().getTrain().getTrainType();
            formattedTrainType = switch (trainType) {
                case EXPRESS -> "Express Train";
                case NORMAL -> "Normal Train";
                case INTERCITY -> "Intercity Train";
                case SPECIAL -> "Special Train";
            };

            String status = "";
            LocalDate bookingDate = booking.getBookedAt().toLocalDate();
            LocalDate today = LocalDate.now();

            if (bookingDate.isEqual(today)) {
                status = "Today";
            } else if (bookingDate.isBefore(today)) {
                status = "Completed";
            } else if (bookingDate.isAfter(today)) {
                status = "Upcoming";
            }

            BookingDto bookingDto = BookingDto.builder()
                    .bookingId(booking.getBookingId())
                    .departureStation(booking.getDepartureStation().getName())
                    .destinationStation(booking.getDestinationStation().getName())
                    .trainName(booking.getSchedule().getTrain().getName())
                    .trainType(formattedTrainType)
                    .travelClass(formattedTravelClass)
                    .bookingDate(formatBookingDate(booking.getBookedAt()))
                    .bookingTime(formatBookingTime(booking.getBookedAt()))
                    .status(!status.isEmpty() ? status : "no status")
                    .build();

            bookingDtoList.add(bookingDto);

        });

        return bookingDtoList;
    }

    @Override
    public String getPassengerIdByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + userName));
        return user.getPassenger().getPassengerId();
    }

    public String formatBookingDate(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    public String formatBookingTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    private Page<PassengerDto> getPassengerDtos(Page<Passenger> passengerPage) {
        return passengerPage.map(passenger -> {
            String formattedPhoneNumber = passenger.getPhoneNumber().substring(0, 3) + "-" + passenger.getPhoneNumber().substring(3);
            if (passenger.getPhoneNumber().equalsIgnoreCase("N/A")){
                formattedPhoneNumber =  "N/A";
            }

            return PassengerDto.builder()
                    .passengerId(passenger.getPassengerId())
                    .title(passenger.getTitle()+".")
                    .firstName(formattedName(passenger.getFirstName()))
                    .lastName(formattedName(passenger.getLastName()))
                    .passengerType(passenger.getPassengerType().name())
                    .idType(passenger.getIdtype().name())
                    .idNumber(passenger.getIdNumber())
                    .phoneNumber(formattedPhoneNumber)
                    .email(passenger.getEmail())
                    .blocked(passenger.isBlocked())
                    .username(passenger.getUser().getUsername())
                    .build();
        });
    }
}

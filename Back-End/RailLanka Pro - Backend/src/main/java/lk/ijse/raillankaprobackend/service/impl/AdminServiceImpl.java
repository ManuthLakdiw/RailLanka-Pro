package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.ChangePasswordDto;
import lk.ijse.raillankaprobackend.dto.PassengerDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.entity.Admin;
import lk.ijse.raillankaprobackend.entity.Dtypes.SystemUserRole;
import lk.ijse.raillankaprobackend.entity.Passenger;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.exception.EmailAlreadyExistsException;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.UserNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.AdminRepository;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.AdminService;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public String registerAdmin(StaffDto staffDto) {

        if (userRepository.findByUsername(staffDto.getUserName()).isPresent()){
            throw new UserNameAlreadyExistsException("This username is already taken. Please choose a different one.");
        }

        User user = User.builder()
                .userId(userService.generateNewUserId())
                .username(staffDto.getUserName())
                .password(passwordEncoder.encode(staffDto.getPassword()))
                .role(SystemUserRole.ADMIN)
                .createdDate(LocalDate.now())
                .build();

        userRepository.save(user);

        String formattedFirstName = staffDto.getFirstname().substring(0, 1).toUpperCase() +
                staffDto.getFirstname().substring(1).toLowerCase();

        String formattedLastName = staffDto.getLastname().substring(0, 1).toUpperCase() +
                staffDto.getLastname().substring(1).toLowerCase();

        Admin admin = Admin.builder()
                .adminId(generateNewAdminId())
                .firstname(formattedFirstName)
                .lastname(formattedLastName)
                .idNumber(staffDto.getIdNumber())
                .phoneNumber(staffDto.getPhoneNumber())
                .email(staffDto.getEmail())
                .active(true)
                .user(user)
                .build();

        adminRepository.save(admin);

        return "Admin Registered Successfully";
    }

    @Override
    public String generateNewAdminId() {
        if (adminRepository.getLastAdminId().isPresent()){
            String lastAdminId = adminRepository.getLastAdminId().get();
            String[] split = lastAdminId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available Admin IDs have been used. Please contact the system administrator");
                }
            }


            return String.format("ADM%05d-%05d", prefixNumber, suffixNumber);

        }
        return "ADM00000-00001";

    }

    @Override
    public StaffDto getAdminDetailsByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found for username: " + userName));

        return StaffDto.builder()
                .id(user.getAdmin().getAdminId())
                .firstname(formattedName(user.getAdmin().getFirstname()))
                .lastname(formattedName(user.getAdmin().getLastname()))
                .email(user.getAdmin().getEmail())
                .phoneNumber(formattedPhoneNumber(user.getAdmin().getPhoneNumber()))
                .role("Admin")
                .active(user.getAdmin().isActive())
                .railwayStation("Head Office")
                .userName(user.getUsername())
                .joinDate(user.getCreatedDate())
                .build();
    }

    @Override
    public boolean changePassword(ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(changePasswordDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + changePasswordDto.getUsername()));

        String currentPassword = user.getPassword();
        String password = changePasswordDto.getCurrentPassword();
        if (passwordEncoder.matches(password, currentPassword)) {
            System.out.println("same");
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAdminDetailsByUserName(StaffDto staffDto) {
        User user = userRepository.findByUsername(staffDto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found for username: " + staffDto.getUserName()));

        Optional<Admin> existingAdminByEmail = adminRepository.findByEmail(staffDto.getEmail());
        if (existingAdminByEmail.isPresent() && !existingAdminByEmail.get().getAdminId().equals(user.getAdmin().getAdminId())) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        user.getAdmin().setFirstname(formattedName(staffDto.getFirstname()));
        user.getAdmin().setLastname(formattedName(staffDto.getLastname()));
        user.getAdmin().setPhoneNumber(staffDto.getPhoneNumber());
        user.getAdmin().setEmail(staffDto.getEmail());

        adminRepository.save(user.getAdmin());
        return true;
    }

    private String formattedName(String name){
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private String formattedPhoneNumber(String phoneNumber){
        String countryCode = "+94";
        String part1 = phoneNumber.substring(1, 3);
        String part2 = phoneNumber.substring(3, 6);
        String part3 = phoneNumber.substring(6);

        return countryCode + " " + part1 + " " + part2 + " " + part3;
    }
}

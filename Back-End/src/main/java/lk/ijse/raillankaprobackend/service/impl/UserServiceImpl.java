package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public String generateNewUserId() {

        if (userRepository.getLastUserid().isPresent()){

            String lastUserId = userRepository.getLastUserid().get();
            String[] split = lastUserId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available User IDs have been used. Please contact the system administrator");
                }
            }

            return String.format("USR%05d-%05d", prefixNumber, suffixNumber);
        }
        return "USR00000-00001";
    }
}

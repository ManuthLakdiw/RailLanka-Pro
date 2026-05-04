package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface UserRepository extends JpaRepository <User,String> {

    Optional<User> findByUsername(String userName);

    @Query(value = "SELECT user_id FROM user ORDER BY user_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastUserid();
}

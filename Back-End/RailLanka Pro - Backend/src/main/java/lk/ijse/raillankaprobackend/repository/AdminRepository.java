package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface AdminRepository extends JpaRepository<Admin,String> {

    @Query(value = "SELECT admin_id FROM admin ORDER BY admin_id DESC LIMIT 1", nativeQuery = true)
    Optional<String> getLastAdminId();
}

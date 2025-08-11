package lk.ijse.raillankaprobackend.repository;

import lk.ijse.raillankaprobackend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {

    Optional<RefreshToken> findByToken(String token);

    @Query(value = "SELECT refresh_token_id FROM refresh_token ORDER BY refresh_token_id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<String> getLastRefreshTokenId();
}

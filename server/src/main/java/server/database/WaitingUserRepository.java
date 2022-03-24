package server.database;

import commons.entities.MultiplayerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitingUserRepository extends JpaRepository<MultiplayerUser, Long> {
    boolean existsByUsername(String username);

    List<MultiplayerUser> findByGameIDIsNull();
}
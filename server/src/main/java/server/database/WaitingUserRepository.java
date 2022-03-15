package server.database;

import commons.entities.MultiplayerUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingUserRepository extends JpaRepository<MultiplayerUser, Long> {
    boolean existsByUsername(String username);
}
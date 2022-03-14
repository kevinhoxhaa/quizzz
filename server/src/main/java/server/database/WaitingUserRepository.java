package server.database;

import commons.entities.MultiplayerUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingUserRepository extends JpaRepository<MultiplayerUser, Long> {
    //@Query(value = "SELECT COUNT(*)>0 FROM USER WHERE USERNAME = ?1", nativeQuery = true)
    boolean existsByUsername(String username);
}
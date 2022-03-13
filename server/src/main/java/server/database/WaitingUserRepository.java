package server.database;

import commons.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingUserRepository extends JpaRepository<User, Long> {
    //@Query(value = "SELECT COUNT(*)>0 FROM USER WHERE USERNAME = ?1 AND SOLO_PLAYER = FALSE", nativeQuery = true)
    boolean existsUserByUsername(String username);
}
package server.database;

import commons.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingUserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);
}
package server.database;

import commons.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoloUserRepository extends JpaRepository<User, Long> {
}

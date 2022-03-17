package server.database;

import commons.entities.SoloUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoloUserRepository extends JpaRepository<SoloUser, Long> {
}

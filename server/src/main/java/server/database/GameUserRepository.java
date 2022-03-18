package server.database;

import commons.entities.MultiplayerUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameUserRepository extends JpaRepository<MultiplayerUser, Long> {
}

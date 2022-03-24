package server.database;

import commons.entities.MultiplayerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameUserRepository extends JpaRepository<MultiplayerUser, Long> {
    @Query(value = "SELECT u FROM MultiplayerUser u WHERE u.gameID=:gameID")
    List<MultiplayerUser> findByGameID(@Param("gameID") Long gameID);
}

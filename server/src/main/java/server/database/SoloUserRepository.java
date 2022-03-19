package server.database;

import commons.entities.SoloUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SoloUserRepository extends JpaRepository<SoloUser, Long> {
    @Query(value = "SELECT * FROM SOLO_USER ORDER BY POINTS DESC", nativeQuery = true)
    List<SoloUser> sortUserByDescendingOrder();
}

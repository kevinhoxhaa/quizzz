package server.database;

import commons.entities.SoloUser;
import commons.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface SoloUserRepository extends JpaRepository<SoloUser, Long> {
    @Query(value = "SELECT USERNAME, POINTS FROM USER ORDER BY POINTS DESC", nativeQuery = true)
    ArrayList<User> sortUserByDescendingOrder(ArrayList<User> users);
}

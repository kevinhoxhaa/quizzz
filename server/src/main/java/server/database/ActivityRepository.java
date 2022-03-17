package server.database;

import commons.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query(value = "SELECT * FROM Activity ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<Activity> getRandomList(@Param("size") int size);
}

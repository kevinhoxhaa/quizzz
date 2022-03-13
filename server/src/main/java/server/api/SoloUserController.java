package server.api;

import commons.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.SoloUserRepository;

@RestController
@RequestMapping("/api/solousers")
public class SoloUserController {

    private final SoloUserRepository soloRepo;

    public SoloUserController(SoloUserRepository soloRepo) {
        this.soloRepo = soloRepo;
    }

    /**
     * Saves a user to the user repository for solo games.
     * If the username is null or empty, however, the user will not be saved.
     * @param user The user that needs to saved in the user repository for solo games.
     * @return A response entity with a corresponding message (was the user saved or was
     * the username incorrect).
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<User> addSolo(@RequestBody User user) {
        if (isNullOrEmpty(user.username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User saved = soloRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

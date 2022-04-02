package server.api;

import commons.entities.MultiplayerUser;
import commons.entities.SoloUser;
import commons.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.SoloUserRepository;
import server.database.WaitingUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Random random;
    private final WaitingUserRepository waitingRepo;
    private final SoloUserRepository soloRepo;

    public UserController(Random random, WaitingUserRepository waitingRepo, SoloUserRepository soloRepo) {
        this.random = random;
        this.waitingRepo = waitingRepo;
        this.soloRepo = soloRepo;
    }

    /**
     * Retrieves all users in the waiting room a user with a particular id
     * is
     * This endpoint is supposed to be used in the waiting room using standard
     * polling to retrieve all users and check if the game has been started
     * @return the users in a waiting room
     */
    @GetMapping(path = { "", "/" })
    public List<MultiplayerUser> getAll() {
        return waitingRepo.findByGameIDIsNull();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<MultiplayerUser>> getById(@PathVariable("id") long id) {
        if (id < 0 || !waitingRepo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(waitingRepo.findById(id));
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<MultiplayerUser> addMultiplayerUser(@RequestBody MultiplayerUser user) {
// || isNullOrEmpty(server) has to be added
        if (isNullOrEmpty(user.username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean existsByUsername = false;
        List<MultiplayerUser> waitingUsers = waitingRepo.findByGameIDIsNull();
        for(MultiplayerUser u : waitingUsers) {
            if(u.username.equals(user.username)) {
                existsByUsername = true;
            }
        }

        if(existsByUsername) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MultiplayerUser saved = waitingRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    /**
     * Saves a user to the user repository for solo games.
     * If the username is null or empty, however, the user will not be saved.
     * @param user The user that needs to saved in the user repository for solo games.
     * @return A response entity with a corresponding message (was the user saved or was
     * the username incorrect).
     */
    @PostMapping(path = {"/solo"})
    public ResponseEntity<SoloUser> addSoloUser(@RequestBody SoloUser user) {
        if (isNullOrEmpty(user.username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SoloUser saved = soloRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates the username of a user with a given ID if present
     * in the repository. Otherwise, creates a new user entity with that
     * username and ID in the repository.
     * If the username is null or empty returns a response with a
     * FORBIDDEN status code.
     * @param user the user to update in the database
     * @return the updated user
     */
    @PutMapping(path = { "", "/" })
    public ResponseEntity<MultiplayerUser> update(@RequestBody MultiplayerUser user) {
// || isNullOrEmpty(server) has to be added
        if (isNullOrEmpty(user.username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MultiplayerUser saved = waitingRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @GetMapping("rnd")
    public ResponseEntity<User> getRandom() {
        var idx = random.nextInt((int) waitingRepo.count());
        return ResponseEntity.ok(waitingRepo.getById((long) idx));
    }

    /**
     * Checks if the ID is valid and exists and if so, the User with the given ID will be deleted
     * from the repository and an OK response will be sent back. Otherwise, a BAD_REQUEST response
     * will be sent.
     * @param id The ID of the User that needs to be deleted from the repository.
     * @return A response that shows if the deletion was successful or not.
     */
    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<MultiplayerUser> deleteMultiplayerUser(@PathVariable("id") long id) {
        if (id < 0 || !waitingRepo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        MultiplayerUser removed = waitingRepo.findById(id).get();
        waitingRepo.deleteById(id);
        return ResponseEntity.ok(removed);
    }

    /**
     * This endpoint is supposed to be used to get all the users from the solo user repository
     * in a descending points order to generate the solo game results page`s leaderboard consisting
     * of the username and the corresponding score for it
     * @return arraylist of users with their corresponding score
     */
    @GetMapping(path={"/solo/leaderboard"})
    public List<SoloUser> getAllScores(){
        return soloRepo.sortUserByDescendingOrder();
    }
}
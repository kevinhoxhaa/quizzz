package server.api;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import commons.entities.User;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import server.database.WaitingUserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Random random;
    private final WaitingUserRepository repo;

    public UserController(Random random, WaitingUserRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    @GetMapping(path = { "", "/" })
    public List<User> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id));
    }

    /**
     * Retrieves all users in the waiting room a user with a particular id
     * is
     * This endpoint is supposed to be used in the waiting room using standard
     * polling to retrieve all users and check if the game has been started
     * If a game with that user has been started, the user won't be in
     * the waiting room and the response will be NO_CONTENT
     * @param id the id of the user, the teammates of which to retrieve
     * @return the teammates of a user
     */
    @GetMapping("/{id}/all")
    public ResponseEntity<List<User>> getAllById(@PathVariable("id") long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if(!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<User> add(@RequestBody User user) {
// || isNullOrEmpty(server) has to be added
        if (isNullOrEmpty(user.username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(repo.existsUserByUsername(user.username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User saved = repo.save(user);
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
    public ResponseEntity<User> update(@RequestBody User user) {
// || isNullOrEmpty(server) has to be added
        if (isNullOrEmpty(user.username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User saved = repo.save(user);
        return ResponseEntity.ok(saved);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @GetMapping("rnd")
    public ResponseEntity<User> getRandom() {
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(repo.getById((long) idx));
    }

    /**
     * Checks if the ID is valid and exists and if so, the User with the given ID will be deleted
     * from the repository and an OK response will be sent back. Otherwise, a BAD_REQUEST response
     * will be sent.
     * @param id The ID of the User that needs to be deleted from the repository.
     * @return A response that shows if the deletion was successful or not.
     */
    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<User> delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
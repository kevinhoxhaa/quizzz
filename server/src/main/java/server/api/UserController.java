package server.api;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import commons.User;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Random random;
    private final UserRepository repo;

    public UserController(Random random, UserRepository repo) {
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
        System.out.println(saved);
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
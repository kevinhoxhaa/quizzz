package server.api;

import commons.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.QuestionRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final Random random;
    private final QuestionRepository repo;

    /**
     * Constructs a new question controller object
     * @param random the random generator of questions
     * @param repo the question repository
     */
    public QuestionController(Random random, QuestionRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Checks whether a String is null or empty
     * @param s the string to check
     * @return true if the string is null or empty
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Retrieves all questions from the repository and sends them
     * to the client
     * @return a list of all questions in the repository
     */
    @GetMapping(path = { "", "/" })
    public List<Question> getAll() {
        return repo.findAll();
    }

    /**
     * Saves a question sent by the client to the question repository
     * @param question the question to save
     * @return the saved question entity
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Question> add(@RequestBody Question question) {
// || isNullOrEmpty(server) has to be added
        if (isNullOrEmpty(question.title)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (isNullOrEmpty(question.source)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(question.consumption < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Question saved = repo.save(question);
        return ResponseEntity.ok(saved);
    }

    /**
     * Retrieves a question by a given id and
     * returns a bad request if a question with that
     * id does not exist
     * @param id the id of the question to retrieve
     * @return the requested question
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Question>> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id));
    }

    /**
     * Retrieves a random question from the questions repo
     * @return the random question requested
     */
    @GetMapping("/rnd")
    public ResponseEntity<Optional<Question>> getRandom() {
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(repo.findById((long) idx));
    }

    /**
     * Deletes a question from the database if a question
     * with the given id exists. Otherwise, returns a bad request
     * @param id the id of the question to delete
     * @return the deleted question if it exists
     */
    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<Question> delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

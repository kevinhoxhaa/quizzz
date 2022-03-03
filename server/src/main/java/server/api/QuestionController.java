package server.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.QuestionRepository;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
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
}

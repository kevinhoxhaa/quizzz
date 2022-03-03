package server.api;

import commons.Question;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.QuestionRepository;

import java.util.List;
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

    /**
     * Retrieves all questions from the repository and sends them
     * to the client
     * @return a list of all questions in the repository
     */
    @GetMapping(path = { "", "/" })
    public List<Question> getAll() {
        return repo.findAll();
    }
}

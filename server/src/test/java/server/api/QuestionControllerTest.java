package server.api;

import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.Random;

public class QuestionControllerTest {

    private static final long NUMBER = 5;

    public int nextInt;
    private MyRandom random;
    private TestQuestionRepository repo;

    private QuestionController sut;

    private Question getQuestion(String title, long consumption, String source) {
        return new Question(title, consumption, source);
    }

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestQuestionRepository();
        sut = new QuestionController(random, repo);
    }

    @Test
    public void getAllReturnsList() {
        assertNotNull(sut.getAll());
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getQuestion(null, NUMBER, "random"));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotAddEmptySource() {
        var actual = sut.add(getQuestion("random", NUMBER, ""));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotAddNegativeConsumption() {
        var actual = sut.add(getQuestion("random", -NUMBER, "random"));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void addIncreasesQuestionsSize() {
        var actual = sut.add(getQuestion("random", NUMBER, "random"));
        assertEquals(1, repo.questions.size());
    }

    @Test
    public void getByIdRetrievesQuestion() {
        Question question = getQuestion("random", NUMBER, "random");
        var expected = sut.add(question);
        var actual = sut.getById(expected.getBody().id);
        assertTrue(actual.getBody().isPresent());
        assertEquals(expected.getBody(), actual.getBody().get());
    }

    @Test
    public void cannotGetNegativeId() {
        Question question = getQuestion("random", NUMBER, "random");
        var actual = sut.getById(-NUMBER);
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void cannotGetNonexistentId() {
        Question question = getQuestion("random", NUMBER, "random");
        var actual = sut.getById(NUMBER);
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}

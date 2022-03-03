package server.api;

import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.Random;

public class QuestionControllerTest {

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
        var actual = sut.add(getQuestion(null, 5, "random"));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotAddEmptySource() {
        var actual = sut.add(getQuestion("random", 5, ""));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotAddNegativeConsumption() {
        var actual = sut.add(getQuestion("random", -5, "random"));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void addIncreasesQuestionsSize() {
        var actual = sut.add(getQuestion("random", 5, "random"));
        assertEquals(1, repo.questions.size());
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

package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Random;

public class QuestionControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestQuestionRepository repo;

    private QuestionController sut;

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

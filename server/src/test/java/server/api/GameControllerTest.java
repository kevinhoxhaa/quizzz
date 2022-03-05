package server.api;

import commons.entities.Activity;
import commons.entities.User;
import commons.models.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameControllerTest {

    private static final long NUMBER = 5;

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }

    public int nextInt;
    private MyRandom random;
    private GameState gameState;
    private TestUserRepository waitingRepo;
    private TestActivityRepository activityRepo;
    private TestGameUserRepository userRepo;

    private GameController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        gameState = new GameState();
        waitingRepo = new TestUserRepository();
        activityRepo = new TestActivityRepository();
        userRepo = new TestGameUserRepository();
        sut = new GameController(random, gameState, waitingRepo, activityRepo, userRepo);

        for(int i = 0; i < NUMBER; i++) {
            activityRepo.save(getActivity("title", NUMBER, "src"));
        }

        for(int i = 0; i < NUMBER; i++) {
            waitingRepo.save(getUser("name"));
        }
    }

    private Activity getActivity(String title, long consumption, String source) {
        return new Activity(title, consumption, source);
    }

    private static User getUser(String q) {
        return new User(q);
    }

    @Test
    public void constructorCreatesController() {
        assertNotNull(sut);
    }

    @Test
    public void startGameReturnsValidIndex() {
        assertTrue(sut.startGame((int) NUMBER).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void startGameMovesUsers() {
        sut.startGame((int) NUMBER);
        assertEquals(0, waitingRepo.count());
        assertEquals(NUMBER, userRepo.count());
    }

    @Test
    public void getQuestionReturnsValidQuestion() {
        sut.startGame((int) NUMBER);
        assertTrue(sut.getQuestion(0, (int) NUMBER - 1)
                .getStatusCode().is2xxSuccessful());
    }

    @Test
    public void getQuestionReturnsBadRequestOnInvalidGame() {
        sut.startGame((int) NUMBER);
        assertTrue(sut.getQuestion(1, (int) NUMBER - 1)
                .getStatusCode().is4xxClientError());
    }

    @Test
    public void getQuestionReturnsBadRequestOnInvalidQuestion() {
        sut.startGame((int) NUMBER);
        assertTrue(sut.getQuestion(0, (int) NUMBER)
                .getStatusCode().is4xxClientError());
    }
}

package server.api;

import commons.entities.Activity;
import commons.entities.User;
import commons.models.ConsumptionQuestion;
import commons.models.GameList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
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
    private GameList gameList;
    private TestUserRepository waitingRepo;
    private TestActivityRepository activityRepo;
    private TestGameUserRepository userRepo;

    private GameController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        gameList = new GameList();
        waitingRepo = new TestUserRepository();
        activityRepo = new TestActivityRepository();
        userRepo = new TestGameUserRepository();
        sut = new GameController(random, gameList, waitingRepo, activityRepo, userRepo);

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

    @Test
    public void postAnswerReturnsValidResponse() {
        sut.startGame((int) NUMBER);
        assertNotNull(sut.postAnswer(0, 0, 0,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"))));
    }

    @Test
    public void postAnswerReturnsErrorOnInvalidGame() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<User>> actual = (ResponseEntity<List<User>>) sut.postAnswer(
                (int) NUMBER, 0, 0,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"))).getResult();
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postAnswerReturnsErrorOnInvalidUser() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<User>> actual = (ResponseEntity<List<User>>) sut.postAnswer(
                0, NUMBER, 0,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"))).getResult();
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postAnswerReturnsErrorOnInvalidQuestion() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<User>> actual = (ResponseEntity<List<User>>) sut.postAnswer(
                0, 0, (int) NUMBER,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"))).getResult();
        assertTrue(actual.getStatusCode().is4xxClientError());
    }
}

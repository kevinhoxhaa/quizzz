package server.api;

import commons.entities.Activity;
import commons.entities.MultiplayerUser;
import commons.entities.User;
import commons.models.ConsumptionQuestion;
import commons.models.Game;
import commons.models.GameList;
import commons.models.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    private TestWaitingUserRepository waitingUserRepo;
    private TestActivityRepository activityRepo;
    private TestGameUserRepository gameUserRepo;

    private GameController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        gameList = new GameList();
        waitingUserRepo = new TestWaitingUserRepository();
        activityRepo = new TestActivityRepository();
        gameUserRepo = new TestGameUserRepository();
        sut = new GameController(random, gameList, waitingUserRepo, activityRepo, gameUserRepo);

        for(int i = 0; i < NUMBER; i++) {
            activityRepo.save(getActivity("title" + i, NUMBER, "src" + i));
        }

        for(int i = 0; i < NUMBER; i++) {
            waitingUserRepo.save(getUser("name" + i));
        }
    }

    private Activity getActivity(String title, long consumption, String source) {
        return new Activity(title, consumption, source);
    }

    private static MultiplayerUser getUser(String q) {
        return new MultiplayerUser(q);
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
        assertEquals(0, waitingUserRepo.findByGameIDIsNull().size());
        assertEquals(NUMBER, gameUserRepo.count());
    }

    @Test
    public void addRestartUserReturnsBadRequestOnInvalidGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.addRestartUser(gameIndex+1, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void addRestartUserReturnsBadRequestOnNegativeGameIndex() {
        sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.addRestartUser(-1, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void addRestartUserReturnsBadRequestOnInvalidUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.addRestartUser(gameIndex, NUMBER).getStatusCode().is4xxClientError());
    }

    @Test
    public void addRestartUserReturnsBadRequestOnNegativeUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.addRestartUser(gameIndex, -1).getStatusCode().is4xxClientError());
    }

    @Test
    public void addRestartUserReturnsOkRequestOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.addRestartUser(gameIndex, 1).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void addRestartUserReturnsCorrectListOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertEquals(List.of((long) 1), sut.addRestartUser(gameIndex, 1).getBody());
    }


    @Test
    public void deleteRestartUserReturnsBadRequestOnInvalidGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.deleteRestartUser(gameIndex+1, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteRestartUserReturnsBadRequestOnNegativeGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.deleteRestartUser(-1, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteRestartUserReturnsBadRequestOnInvalidUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.deleteRestartUser(gameIndex, 2).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteRestartUserReturnsBadRequestOnNegativeUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.deleteRestartUser(gameIndex, -1).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteRestartUserReturnsOkRequestOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.deleteRestartUser(gameIndex, 1).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void deleteRestartUserReturnsCorrectListOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        sut.addRestartUser(gameIndex, 2);
        assertEquals(List.of((long) 2), sut.deleteRestartUser(gameIndex, 1).getBody());
    }

    @Test
    public void deleteUserReturnsBadRequestOnInvalidGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.deleteUserFromGame(gameIndex+1, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteUserReturnsBadRequestOnNegativeGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.deleteUserFromGame(-1, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteUserReturnsBadRequestOnInvalidUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.deleteUserFromGame(gameIndex, NUMBER).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteUserReturnsBadRequestOnNegativeUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.deleteUserFromGame(gameIndex, -1).getStatusCode().is4xxClientError());
    }

    @Test
    public void deleteUserReturnsOkRequestOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        assertTrue(sut.deleteUserFromGame(gameIndex, NUMBER-1).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void deleteUserReturnsCorrectListOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        List<Long> expected = new ArrayList<>();
        for (int i = 0; i < NUMBER-1; i++) {
            expected.add((long) i);
        }
        assertEquals(expected, sut.deleteUserFromGame(gameIndex, NUMBER-1).getBody());
    }

    @Test
    public void restartGameReturnsBadRequestOnInvalidGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.restartGame(gameIndex+1, (int) NUMBER, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void restartGameReturnsBadRequestOnNegativeGameIndex() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.restartGame(-1, (int) NUMBER, 1).getStatusCode().is4xxClientError());
    }

    @Test
    public void restartGameReturnsBadRequestOnInvalidUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.restartGame(gameIndex, (int) NUMBER, 2).getStatusCode().is4xxClientError());
    }

    @Test
    public void restartGameReturnsBadRequestOnNegativeUserId() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.restartGame(gameIndex, (int) NUMBER, -1).getStatusCode().is4xxClientError());
    }

    @Test
    public void restartGameReturnsOkRequestOnValidParameters() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        assertTrue(sut.restartGame(gameIndex, (int) NUMBER, 1).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void restartGameDeletesCorrectRestartUser() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        sut.addRestartUser(gameIndex, 1);
        sut.addRestartUser(gameIndex, 2);
        sut.restartGame(gameIndex, (int) NUMBER, 1);
        assertEquals(List.of((long) 2), sut.getGameList().getBody().getGames()
                .get((long) gameIndex).getRestartUserIds());
    }

    @Test
    public void restartGameSetsNewQuestionsOnFirstRequest() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        Game game = sut.getGameList().getBody().getGames().get((long) gameIndex);
        List<Question> oldQuestions = game.getQuestions();
        sut.addRestartUser(gameIndex, 1);
        sut.restartGame(gameIndex, (int) NUMBER, 1);
        assertNotEquals(oldQuestions, game.getQuestions());
    }

    @Test
    public void restartGameDoesNotSetNewQuestionsOnSecondRequest() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        Game game = sut.getGameList().getBody().getGames().get((long) gameIndex);
        sut.addRestartUser(gameIndex, 1);
        sut.addRestartUser(gameIndex, 2);
        sut.restartGame(gameIndex, (int) NUMBER, 1);
        List<Question> expected = game.getQuestions();
        sut.restartGame(gameIndex, (int) NUMBER, 2);
        assertEquals(expected, game.getQuestions());
    }

    @Test
    public void restartGameSetsNewQuestionsOnSecondRestart() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        Game game = sut.getGameList().getBody().getGames().get((long) gameIndex);
        sut.addRestartUser(gameIndex, 1);
        sut.restartGame(gameIndex, (int) NUMBER, 1);
        List<Question> oldQuestions = game.getQuestions();
        sut.addRestartUser(gameIndex, 1);
        sut.restartGame(gameIndex, (int) NUMBER, 1);
        assertNotEquals(oldQuestions, game.getQuestions());
    }

    @Test
    public void restartGameReturnsFirstQuestion() {
        Integer gameIndex = sut.startGame((int) NUMBER).getBody();
        Game game = sut.getGameList().getBody().getGames().get((long) gameIndex);
        sut.addRestartUser(gameIndex, 1);
        Question returned = sut.restartGame(gameIndex, (int) NUMBER, 1).getBody();
        Question expected = game.getQuestions().get(0);
        assertEquals(expected, returned);
    }

    @Test
    public void findGameIndexFindsIndex() {
        sut.startGame((int) NUMBER);
        User user = gameUserRepo.users.get(0);
        assertEquals(0, sut.findGameIndex(user.id).getBody());
    }

    @Test
    public void findGameIndexReturnsBadRequestOnInvalidIndex() {
        sut.startGame((int) NUMBER);
        assertTrue(sut.findGameIndex(-NUMBER).getStatusCode().is4xxClientError());
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
        assertNotNull(sut.postAnswer(0, 0, 0,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random)));
    }

    @Test
    public void postAnswerReturnsErrorOnInvalidGame() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual = (ResponseEntity<List<MultiplayerUser>>) sut.postAnswer(
                (int) NUMBER, 0, 0,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postAnswerReturnsErrorOnInvalidUser() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual = (ResponseEntity<List<MultiplayerUser>>) sut.postAnswer(
                0, NUMBER, 0,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postAnswerReturnsErrorOnInvalidQuestion() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual = (ResponseEntity<List<MultiplayerUser>>) sut.postAnswer(
                0, 0, (int) NUMBER,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postAnswerReturnsExpectationFailedIfNotAllUsersHaveAnswered() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual = (ResponseEntity<List<MultiplayerUser>>) sut.postAnswer(
                0, 0, 1,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertEquals(HttpStatus.EXPECTATION_FAILED, actual.getStatusCode());
    }

    @Test
    public void postDoublePointsAnswerReturnsValidResponse() {
        sut.startGame((int) NUMBER);
        assertNotNull(sut.postDoublePointsAnswer(0, 0, 0,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random)));
    }

    @Test
    public void postDoublePointsAnswerReturnsErrorOnInvalidGame() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual =
                (ResponseEntity<List<MultiplayerUser>>) sut.postDoublePointsAnswer(
                (int) NUMBER, 0, 0,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postDoublePointsAnswerReturnsErrorOnInvalidUser() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual =
                (ResponseEntity<List<MultiplayerUser>>) sut.postDoublePointsAnswer(
                0, NUMBER, 0,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postDoublePointsAnswerReturnsErrorOnInvalidQuestion() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual =
                (ResponseEntity<List<MultiplayerUser>>) sut.postDoublePointsAnswer(
                0, 0, (int) NUMBER,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void postDoublePointsAnswerReturnsExpectationFailedIfNotAllUsersHaveAnswered() {
        sut.startGame((int) NUMBER);
        ResponseEntity<List<MultiplayerUser>> actual =
                (ResponseEntity<List<MultiplayerUser>>) sut.postDoublePointsAnswer(
                0, 0, 1,0L,
                new ConsumptionQuestion(getActivity("title", NUMBER, "src"), random));
        assertEquals(HttpStatus.EXPECTATION_FAILED, actual.getStatusCode());
    }

    @Test
    public void getRankingReturnsSortedUsers() {
        sut.startGame((int) NUMBER);
        var actual = sut.getRanking(0);
        assertNotNull(actual.getBody());
    }
}

package server.api;

import commons.models.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Random;

public class GameControllerTest {
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
        sut = new GameController(random, gameState, waitingRepo, activityRepo, userRepo);
    }

    @Test
    public void constructorCreatesController() {
        assertNotNull(sut);
    }
}

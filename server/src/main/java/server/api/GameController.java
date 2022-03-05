package server.api;

import commons.models.GameState;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ActivityRepository;
import server.database.GameUserRepository;
import server.database.UserRepository;

import java.util.Random;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final Random random;
    private final GameState gameState;
    private final UserRepository waitingRepo;
    private final ActivityRepository activityRepo;
    private final GameUserRepository userRepo;

    /**
     * Constructs a game controller with the given repositories
     * and the game state object stored on the server
     * @param random random generator
     * @param gameState game state object
     * @param waitingRepo waiting user repository
     * @param activityRepo activity repository
     * @param userRepo user repository
     */
    public GameController(Random random, GameState gameState, UserRepository waitingRepo,
                          ActivityRepository activityRepo, GameUserRepository userRepo) {
        this.random = random;
        this.waitingRepo = waitingRepo;
        this.userRepo = userRepo;
        this.activityRepo = activityRepo;
        this.gameState = gameState;
    }
}

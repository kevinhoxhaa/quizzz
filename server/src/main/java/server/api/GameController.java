package server.api;

import commons.entities.Activity;
import commons.entities.User;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.EstimationQuestion;
import commons.models.Game;
import commons.models.GameState;
import commons.models.Question;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ActivityRepository;
import server.database.GameUserRepository;
import server.database.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final int CHOICE_COUNT = 4;
    private static final double CONSUMPTION_LIMIT = 0.25;
    private static final double ESTIMATION_LIMIT = 0.5;
    private static final double CHOICE_LIMIT = 0.75;

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

    /**
     * Returns a random question based on the activities
     * in the database
     * @return a random question
     */
    private Question generateQuestion() {
        double type = random.nextDouble();

        if(type < CONSUMPTION_LIMIT) {
            return generateConsumptionQuestion();
        } else if(type < ESTIMATION_LIMIT) {
            return generateEstimationQuestion();
        } else if(type < CHOICE_LIMIT) {
            return generateChoiceQuestion();
        } else {
            return generateComparisonQuestion();
        }
    }

    /**
     * Returns a random consumption question based on the
     * activities in the database
     * @return random consumption question
     */
    private ConsumptionQuestion generateConsumptionQuestion() {
        long idx = random.nextInt((int) activityRepo.count());
        Optional<Activity> activity = activityRepo.findById(idx);
        return new ConsumptionQuestion(activity.get());
    }

    /**
     * Returns a random estimation question based on the
     * activities in the database
     * @return random estimation question
     */
    private EstimationQuestion generateEstimationQuestion() {
        long idx = random.nextInt((int) activityRepo.count());
        Optional<Activity> activity = activityRepo.findById(idx);
        return new EstimationQuestion(activity.get());
    }

    /**
     * Returns a random choice question based on the
     * activities in the database
     * @return random choice question
     */
    private ChoiceQuestion generateChoiceQuestion() {
        List<Activity> activities = new ArrayList<>();
        for(int i = 0; i < CHOICE_COUNT; i++) {
            long idx = random.nextInt((int) activityRepo.count());
            Optional<Activity> activity = activityRepo.findById(idx);
            activities.add(activity.get());
        }
        return new ChoiceQuestion(activities);
    }

    /**
     * Returns a random comparison question based on the
     * activities in the database
     * @return random comparison question
     */
    private ComparisonQuestion generateComparisonQuestion() {
        long idx = random.nextInt((int) activityRepo.count());
        Optional<Activity> firstActivity = activityRepo.findById(idx);
        idx = random.nextInt((int) activityRepo.count());
        Optional<Activity> secondActivity = activityRepo.findById(idx);
        return new ComparisonQuestion(firstActivity.get(), secondActivity.get());
    }

    /**
     * Creates a new game object with a specified index in the
     * game state stored on the server
     * Deletes all users from the waiting room repo and moves them
     * to the game repo
     * Generates a number of random questions for the given game and
     * returns the game index in the game state
     * @param count the number of random questions to generate
     * @return the game index in the game state, so that the
     * game is easily identifiable later from the client
     */
    @GetMapping(path = { "", "/start/{count}" })
    public Integer startGame(@PathVariable("count") int count) {
        Game game = new Game();

        List<User> users = waitingRepo.findAll();
        userRepo.saveAll(users);
        waitingRepo.deleteAll();

        users.forEach(u -> game.getUserIds().add(u.id));
        for(int i = 0; i < count; i++) {
            game.getQuestions().add(generateQuestion());
        }

        gameState.getGames().add(game);
        return gameState.getGames().indexOf(game);
    }
}

package server.api;

import commons.entities.Activity;
import commons.entities.User;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.EstimationQuestion;
import commons.models.Game;
import commons.models.GameList;
import commons.models.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.ActivityRepository;
import server.database.GameUserRepository;
import server.database.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final int CHOICE_COUNT = 4;
    private static final int THREAD_COUNT = 5;
    private static final double CONSUMPTION_LIMIT = 0.25;
    private static final double ESTIMATION_LIMIT = 0.5;
    private static final double CHOICE_LIMIT = 0.75;
    private static final long TIME_INTERVAL = 500;
    private static final long TIMEOUT = 5000;

    private final Random random;
    private final GameList gameList;
    private final UserRepository waitingRepo;
    private final ActivityRepository activityRepo;
    private final GameUserRepository userRepo;
    private ExecutorService threads = Executors.newFixedThreadPool(THREAD_COUNT);

    /**
     * Constructs a game controller with the given repositories
     * and the game state object stored on the server
     * @param random random generator
     * @param gameList game state object
     * @param waitingRepo waiting user repository
     * @param activityRepo activity repository
     * @param userRepo user repository
     */
    public GameController(Random random, GameList gameList, UserRepository waitingRepo,
                          ActivityRepository activityRepo, GameUserRepository userRepo) {
        this.random = random;
        this.waitingRepo = waitingRepo;
        this.userRepo = userRepo;
        this.activityRepo = activityRepo;
        this.gameList = gameList;
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
     * Returns true if all users in a given list of users
     * have answered a question with a particular number
     * @param userIds the ids of the users to validate
     * @param questionNumber the question number
     * @return true if all users have answered at least
     * that number of questions
     */
    private boolean allUsersHaveAnswered(List<Long> userIds, int questionNumber) {
        for(Long id : userIds) {
            User user = userRepo.findById(id).get();
            if(user.correctAnswers < questionNumber) {
                return false;
            }
        }
        return true;
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
    @GetMapping(path =  "/start/{count}")
    public ResponseEntity<Integer> startGame(@PathVariable("count") int count) {
        Game game = new Game();

        if(waitingRepo.count() == 0) {
            return ResponseEntity.badRequest().build();
        }

        List<User> users = waitingRepo.findAll();
        userRepo.saveAll(users);
        waitingRepo.deleteAll();

        users.forEach(u -> game.getUserIds().add(u.id));
        for(int i = 0; i < count; i++) {
            game.getQuestions().add(generateQuestion());
        }

        gameList.getGames().add(game);
        return ResponseEntity.ok(gameList.getGames().indexOf(game));
    }

    /**
     * Retrieves the requested question from the game state object
     * and sends it to the user
     * Returns a bad request if the game or question index
     * is invalid
     * @param gameIndex the index of the game
     * @param questionIndex the index of the question
     * @return the requested question
     */
    @GetMapping(path =  "/{gameIndex}/question/{questionIndex}")
    public ResponseEntity<Question> getQuestion(@PathVariable(name = "gameIndex") int gameIndex,
                                @PathVariable(name = "questionIndex") int questionIndex) {
        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(game.getQuestions().get(questionIndex));
    }

    /**
     * Adds the user answer points to the database and
     * returns the number of users who have answered the last
     * question correctly
     * @param gameIndex the index of the game
     * @param userId the id of the answering user
     * @param questionIndex the question index
     * @param answeredQuestion the answered question with recorded points
     * @return the list of users who have answered the last question
     * correctly
     */
    @PostMapping(path =  "/{gameIndex}/user/{userId}/question/{questionIndex}")
    public DeferredResult<ResponseEntity<List<User>>>
    postAnswer(@PathVariable(name = "gameIndex") int gameIndex,
               @PathVariable(name = "userId") long userId,
               @PathVariable(name = "questionIndex") int questionIndex,
               @RequestBody Question answeredQuestion) {
        DeferredResult<ResponseEntity<List<User>>> output = new DeferredResult<>();
        if(!userRepo.existsById(userId)) {
            output.setResult(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            return output;
        }

        if(gameIndex >= gameList.getGames().size()) {
            output.setResult(ResponseEntity.badRequest().build());
            return output;
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            output.setResult(ResponseEntity.badRequest().build());
            return output;
        }

        User user = userRepo.findById(userId).get();
        user.points += answeredQuestion.getPoints();
        user.totalAnswers += 1;
        user.correctAnswers += answeredQuestion.getPoints() == 0 ? 0 : 1;
        user.lastAnswerCorrect = answeredQuestion.getPoints() > 0;

        threads.execute(() -> {
            try {
                long timeAwait = 0;
                do {
                    if (timeAwait >= TIMEOUT) {
                        throw new InterruptedException();
                    }
                    Thread.sleep(TIME_INTERVAL);
                    timeAwait += TIME_INTERVAL;
                } while (!allUsersHaveAnswered(game.getUserIds(), questionIndex + 1));
            } catch(InterruptedException ex) {
                output.setResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
            }
        });

        // If no next question, return FORBIDDEN and handle
        // game end on the client
        if(questionIndex + 1 >= game.getQuestions().size()) {
            output.setResult(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
            return output;
        }

        List<User> rightUsers = new ArrayList<>();
        for(long id : game.getUserIds()) {
            User u = userRepo.findById(id).get();
            if(u.lastAnswerCorrect) {
                rightUsers.add(u);
            }
            u.lastAnswerCorrect = false; // Reset last correct answer for next question
        }

        return output;
    }
}

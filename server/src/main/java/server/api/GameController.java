package server.api;

import commons.entities.Activity;
import commons.entities.MultiplayerUser;
import commons.entities.User;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.EstimationQuestion;
import commons.models.Game;
import commons.models.GameList;
import commons.models.Question;
import commons.models.SoloGame;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ActivityRepository;
import server.database.GameUserRepository;
import server.database.WaitingUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final int CHOICE_COUNT = 4;
    private static final double CONSUMPTION_LIMIT = 0.25;
    private static final double ESTIMATION_LIMIT = 0.5;
    private static final double CHOICE_LIMIT = 0.75;

    private final Random random;
    private final GameList gameList;
    private final WaitingUserRepository waitingUserRepo;
    private final ActivityRepository activityRepo;
    private final GameUserRepository gameUserRepo;

    /**
     * Constructs a game controller with the given repositories
     * and the game state object stored on the server
     * @param random random generator
     * @param gameList game state object
     * @param waitingUserRepo waiting user repository
     * @param activityRepo activity repository
     * @param gameUserRepo user repository
     */
    public GameController(Random random, GameList gameList, WaitingUserRepository waitingUserRepo,
                          ActivityRepository activityRepo, GameUserRepository gameUserRepo) {
        this.random = random;
        this.waitingUserRepo = waitingUserRepo;
        this.gameUserRepo = gameUserRepo;
        this.activityRepo = activityRepo;
        this.gameList = gameList;
    }

    private Activity getRandomActivity() {
        return activityRepo.getRandomList(1).get(0);
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
        return new ConsumptionQuestion(getRandomActivity(), new Random());
    }

    /**
     * Returns a random estimation question based on the
     * activities in the database
     * @return random estimation question
     */
    private EstimationQuestion generateEstimationQuestion() {
        return new EstimationQuestion(getRandomActivity());
    }

    /**
     * Returns a random choice question based on the
     * activities in the database
     * @return random choice question
     */
    private ChoiceQuestion generateChoiceQuestion() {
        List<Activity> activities = new ArrayList<>();
        for(int i = 0; i < CHOICE_COUNT; i++) {
            activities.add(getRandomActivity());
        }
        return new ChoiceQuestion(activities);
    }

    /**
     * Returns a random comparison question based on the
     * activities in the database
     * @return random comparison question
     */
    private ComparisonQuestion generateComparisonQuestion() {
        Activity firstActivity = getRandomActivity();
        Activity secondActivity = getRandomActivity();
        return new ComparisonQuestion(firstActivity, secondActivity);
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
        System.out.println("Users in game: " + userIds);
        for(Long id : userIds) {
            User user = gameUserRepo.findById(id).get();
            if(user.totalAnswers < questionNumber) {
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

        if(waitingUserRepo.findByGameIDIsNull().size() == 0) {
            return ResponseEntity.badRequest().build();
        }

        List<MultiplayerUser> users = waitingUserRepo.findByGameIDIsNull();
        for(MultiplayerUser user : users) {
            user.gameID = (long) gameList.getGames().size();
        }
        users.forEach(u -> gameUserRepo.save(u));

        users = gameUserRepo.findByGameID((long) gameList.getGames().size());
        users.forEach(u -> game.getUserIds().add(u.id));

//        waitingUserRepo.deleteAll();

        for(int i = 0; i < count; i++) {
            game.getQuestions().add(generateQuestion());
        }

        gameList.getGames().add(game);
        return ResponseEntity.ok(gameList.getGames().indexOf(game));
    }

    /**
     * Generates a new game object for a solo game
     * @param count the number of random questions to generate
     * @return the game itself, as everything concerning the game will happen on the client side
     */
    @GetMapping(path =  "/startSolo/{count}")
    public ResponseEntity<SoloGame> startSoloGame(@PathVariable("count") int count) {
        SoloGame game = new SoloGame();

        for(int i = 0; i < count; i++) {
            game.getQuestions().add(generateQuestion());
        }

        gameList.getGames().add(game);
        return ResponseEntity.ok(game);
    }

    @GetMapping(path = "/find/{userId}")
    public ResponseEntity<Integer> findGameIndex(@PathVariable("userId") long userId) {
        if(userId < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Game> games = gameList.getGames();
        int index = -1;

        for(int i = 0; i < games.size(); i++) {
            if(games.get(i).getUserIds().contains(userId)) {
                index = i;
                break;
            }
        }

        return ResponseEntity.ok(index);
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
     * Retrieves the type of the requested question from the game state object
     * and sends it to the user
     * Returns a bad request if the game or question index
     * is invalid
     * @param gameIndex the index of the game
     * @param questionIndex the index of the question
     * @return the requested question
     */
    @GetMapping(path =  "/{gameIndex}/questionType/{questionIndex}")
    public ResponseEntity<String> getQuestionType(
            @PathVariable(name = "gameIndex") int gameIndex,
                                                @PathVariable(name = "questionIndex") int questionIndex) {
        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(game.getQuestions().get(questionIndex).getType().name());
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
    @GetMapping(path =  "/{gameIndex}/consumption/{questionIndex}")
    public ResponseEntity<ConsumptionQuestion> getConsumptionQuestion(
            @PathVariable(name = "gameIndex") int gameIndex,
                                                @PathVariable(name = "questionIndex") int questionIndex) {
        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Sent question with answer: " + game.getQuestions().get(questionIndex).getUserAnswer().generateAnswer());

        return ResponseEntity.ok((ConsumptionQuestion) game.getQuestions().get(questionIndex));
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
    @GetMapping(path =  "/{gameIndex}/estimation/{questionIndex}")
    public ResponseEntity<EstimationQuestion> getEstimationQuestion(
            @PathVariable(name = "gameIndex") int gameIndex,
                                                @PathVariable(name = "questionIndex") int questionIndex) {
        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Sent question with answer: " + game.getQuestions().get(questionIndex).getUserAnswer().generateAnswer());

        return ResponseEntity.ok((EstimationQuestion) game.getQuestions().get(questionIndex));
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
    @GetMapping(path =  "/{gameIndex}/choice/{questionIndex}")
    public ResponseEntity<ChoiceQuestion> getChoiceQuestion(
            @PathVariable(name = "gameIndex") int gameIndex,
                                                @PathVariable(name = "questionIndex") int questionIndex) {
        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Sent question with answer: " + game.getQuestions().get(questionIndex).getUserAnswer().generateAnswer());

        return ResponseEntity.ok((ChoiceQuestion) game.getQuestions().get(questionIndex));
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
    @GetMapping(path =  "/{gameIndex}/comparison/{questionIndex}")
    public ResponseEntity<ComparisonQuestion> getComparisonQuestion(
            @PathVariable(name = "gameIndex") int gameIndex,
                                                @PathVariable(name = "questionIndex") int questionIndex) {
        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Sent question with answer: " + game.getQuestions().get(questionIndex).getUserAnswer().generateAnswer());

        return ResponseEntity.ok((ComparisonQuestion) game.getQuestions().get(questionIndex));
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
    public ResponseEntity<List<MultiplayerUser>>
    postAnswer(@PathVariable(name = "gameIndex") int gameIndex,
               @PathVariable(name = "userId") long userId,
               @PathVariable(name = "questionIndex") int questionIndex,
               @RequestBody Question answeredQuestion) {
        if(!gameUserRepo.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(gameIndex >= gameList.getGames().size()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameList.getGames().get(gameIndex);

        if(questionIndex >= game.getQuestions().size()) {
            return ResponseEntity.badRequest().build();
        }

        MultiplayerUser user = gameUserRepo.findById(userId).get();
        System.out.println("Question index: " + questionIndex + " User answers: " + user.totalAnswers);
        if(user.totalAnswers <= questionIndex) {
            user.points += answeredQuestion.calculatePoints();
            user.totalAnswers += 1;
            user.correctAnswers += answeredQuestion.calculatePoints() == 0 ? 0 : 1;
            user.lastAnswerCorrect = answeredQuestion.hasCorrectUserAnswer();
            System.out.println("User answer: " + answeredQuestion.getUserAnswer().generateAnswer());
            gameUserRepo.save(user);
        }

        if(!allUsersHaveAnswered(game.getUserIds(), questionIndex + 1)) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }

        List<MultiplayerUser> rightUsers = new ArrayList<>();
        for(long id : game.getUserIds()) {
            MultiplayerUser u = gameUserRepo.findById(id).get();
            if(u.lastAnswerCorrect) {
                rightUsers.add(u);
            }
        }
        System.out.println("Correct users: " + rightUsers);

        return ResponseEntity.ok(rightUsers);
    }

    /**
     * Adds the user consumption answer points to the database and
     * returns the number of users who have answered the last
     * question correctly
     * @param gameIndex the index of the game
     * @param userId the id of the answering user
     * @param questionIndex the question index
     * @param answeredQuestion the answered question with recorded points
     * @return the list of users who have answered the last question
     * correctly
     */
    @PostMapping(path =  "/{gameIndex}/user/{userId}/consumption/{questionIndex}")
    public ResponseEntity<List<MultiplayerUser>>
    postConsumptionAnswer(@PathVariable(name = "gameIndex") int gameIndex,
               @PathVariable(name = "userId") long userId,
               @PathVariable(name = "questionIndex") int questionIndex,
               @RequestBody ConsumptionQuestion answeredQuestion) {
        return postAnswer(gameIndex, userId, questionIndex, answeredQuestion);
    }

    /**
     * Adds the user estimation answer points to the database and
     * returns the number of users who have answered the last
     * question correctly
     * @param gameIndex the index of the game
     * @param userId the id of the answering user
     * @param questionIndex the question index
     * @param answeredQuestion the answered question with recorded points
     * @return the list of users who have answered the last question
     * correctly
     */
    @PostMapping(path =  "/{gameIndex}/user/{userId}/estimation/{questionIndex}")
    public ResponseEntity<List<MultiplayerUser>>
    postEstimationAnswer(@PathVariable(name = "gameIndex") int gameIndex,
                          @PathVariable(name = "userId") long userId,
                          @PathVariable(name = "questionIndex") int questionIndex,
                          @RequestBody EstimationQuestion answeredQuestion) {
        return postAnswer(gameIndex, userId, questionIndex, answeredQuestion);
    }

    /**
     * Adds the user choice answer points to the database and
     * returns the number of users who have answered the last
     * question correctly
     * @param gameIndex the index of the game
     * @param userId the id of the answering user
     * @param questionIndex the question index
     * @param answeredQuestion the answered question with recorded points
     * @return the list of users who have answered the last question
     * correctly
     */
    @PostMapping(path =  "/{gameIndex}/user/{userId}/choice/{questionIndex}")
    public ResponseEntity<List<MultiplayerUser>>
    postChoiceAnswer(@PathVariable(name = "gameIndex") int gameIndex,
                          @PathVariable(name = "userId") long userId,
                          @PathVariable(name = "questionIndex") int questionIndex,
                          @RequestBody ChoiceQuestion answeredQuestion) {
        return postAnswer(gameIndex, userId, questionIndex, answeredQuestion);
    }

    /**
     * Adds the user comparison answer points to the database and
     * returns the number of users who have answered the last
     * question correctly
     * @param gameIndex the index of the game
     * @param userId the id of the answering user
     * @param questionIndex the question index
     * @param answeredQuestion the answered question with recorded points
     * @return the list of users who have answered the last question
     * correctly
     */
    @PostMapping(path =  "/{gameIndex}/user/{userId}/comparison/{questionIndex}")
    public ResponseEntity<List<MultiplayerUser>>
    postComparisonAnswer(@PathVariable(name = "gameIndex") int gameIndex,
                          @PathVariable(name = "userId") long userId,
                          @PathVariable(name = "questionIndex") int questionIndex,
                          @RequestBody ComparisonQuestion answeredQuestion) {
        return postAnswer(gameIndex, userId, questionIndex, answeredQuestion);
    }
}

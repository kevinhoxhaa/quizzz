package client.scenes;

import client.utils.ServerUtils;
import commons.entities.MultiplayerUser;
import commons.models.Emoji;
import commons.models.EstimationQuestion;
import commons.models.Question;
import commons.utils.QuestionType;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MultiplayerGameCtrl {
    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;
    private static final double OPACITY = 0.5;
    private static final double STANDARD_SIZE = 1.0;
    public static final double RGB_VALUE = (double) 244/255;

    private List<Color> colors;
    private boolean answeredQuestion = false;

    private Timer answerTimer;

    private MainCtrl mainCtrl;
    private ServerUtils server;

    private int gameIndex;
    private MultiplayerUser user;

    private String serverUrl;
    private int answerCount;

    private Scene mcQuestion;
    private MultiplayerQuestionCtrl mcQuestionCtrl;

    private Scene estimationQuestion;
    private EstimationQuestionCtrl estimationQuestionCtrl;

    private Scene answer;
    private MultiplayerAnswerCtrl answerCtrl;

    private Scene ranking;
    private RankingCtrl rankingCtrl;

    private boolean isAvailableDoublePoints = true;
    private boolean isActiveDoublePoints;

    private List<String> usedJokers;

    // TODO: add results and resultsCtrl

    /**
     * Constructs a multiplayer game controller with the given
     * specific scenes and controllers
     * @param mainCtrl the app main controller
     * @param server the app server utils
     * @param mcQuestion the mc question controller-scene pair
     * @param estimationQuestion the estimation question controller-scene pair
     * @param answer the answer controller-scene pair
     * @param ranking the ranking controller-scene pair
     * @param gameIndex the index of the multiplayer game
     */
    public MultiplayerGameCtrl(int gameIndex, MainCtrl mainCtrl, ServerUtils server,
                               Pair<MultiplayerQuestionCtrl, Scene> mcQuestion,
                               Pair<EstimationQuestionCtrl, Scene> estimationQuestion,
                               Pair<MultiplayerAnswerCtrl, Scene> answer,
                               Pair<RankingCtrl, Scene> ranking) {
        this.gameIndex = gameIndex;
        this.mainCtrl = mainCtrl;

        this.server = server;
        this.user = (MultiplayerUser) mainCtrl.getUser();
        user.unansweredQuestions = 0;
        this.colors = new ArrayList<>();

        this.usedJokers=new ArrayList<>();

        this.answerTimer = new Timer();

        this.serverUrl = mainCtrl.getServerUrl();
        this.answerCount = 0;
        isActiveDoublePoints=false;

        this.mcQuestionCtrl = mcQuestion.getKey();
        mcQuestionCtrl.setGameCtrl(this);
        this.mcQuestion = mcQuestion.getValue();

        this.estimationQuestionCtrl = estimationQuestion.getKey();
        estimationQuestionCtrl.setGameCtrl(this);
        this.estimationQuestion = estimationQuestion.getValue();

        this.answerCtrl = answer.getKey();
        answerCtrl.setGameCtrl(this);
        this.answer = answer.getValue();

        this.rankingCtrl = ranking.getKey();
        rankingCtrl.setGameCtrl(this);
        this.ranking = ranking.getValue();
    }

    public MultiplayerGameCtrl(){

    }

    /**
     * Polls the first question and initialises
     * the game loop
     * Initiates the websocket connection with the client
     * for receiving emojis
     * Resets the jokers
     */
    public void startGame() {
        server.connect(serverUrl);

        registerForEmojis(estimationQuestionCtrl);
        registerForEmojis(answerCtrl);
        registerForEmojis(mcQuestionCtrl);
        registerForHalfTime();
        Question firstQuestion = fetchQuestion();
        showQuestion(firstQuestion);
        resetAllJokers();
    }

    /**
     * Polls the next question from the server using the
     * answers counter
     * @return the next question
     */
    public Question fetchQuestion() {
        return server.getQuestion(serverUrl, gameIndex, answerCount);
    }

    /**
     * - Posts an answered question to the server, updates the
     * answer colours and redirects to the answer screen
     * - Gives double points if the joker isn't available
     * - Sets the joker as "available" after it is used,
     * even though it won't be possible to use it again
     * @param answeredQuestion the answered question to post
     */
    public void postAnswer(Question answeredQuestion) {
        if(getIsActiveDoublePoints()){
            user.points += 2*answeredQuestion.calculatePoints();
            setIsActiveDoublePoints(false);
        }
        else{
            user.points += answeredQuestion.calculatePoints();
        }
        answerTimer = new Timer();
        answerTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            List<MultiplayerUser> correctUsers = fetchCorrectUsers(answeredQuestion);

                            Platform.runLater(() -> {
                                showAnswer(answeredQuestion, correctUsers);
                            });
                            answerTimer.cancel();
                        } catch (WebApplicationException e) {
                            System.out.println(e.getResponse());
                            // Not all users have answered
                        } catch(NullPointerException e) {
                            // Handle no users issue
                            System.out.println(e.getMessage());
                            Platform.runLater(() -> {
                                showAnswer(answeredQuestion, new ArrayList<>());
                            });
                            answerTimer.cancel();
                        }
                    }
                }, POLLING_DELAY, POLLING_INTERVAL);
    }

    /**
     * Returns the correct users to an answered question
     * @param answeredQuestion the answered question
     * @return the correctly answered users
     * @throws WebApplicationException if the question has not been
     * answered by everyone
     */
    public List<MultiplayerUser> fetchCorrectUsers(Question answeredQuestion) throws WebApplicationException {
        if(isActiveDoublePoints){
            return server.answerQuestion(serverUrl, gameIndex,
                    mainCtrl.getUser().id, answerCount, answeredQuestion);
        }
        else{
            return server.answerDoublePointsQuestion(serverUrl, gameIndex,
                    mainCtrl.getUser().id, answerCount, answeredQuestion);
        }
    }

    /**
     * Shows the answer screen displaying an answered question
     * and the users that have correctly answered that question
     * @param answeredQuestion the answered question
     * @param correctUsers the correct users list
     */
    public void showAnswer(Question answeredQuestion, List<MultiplayerUser> correctUsers) {
        answerCtrl.updateQuestionNumber();
        answerCount++;

        if (answeredQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }

        answerCtrl.updateCircleColor(colors);
        answerCtrl.setup(answeredQuestion, correctUsers);
        mainCtrl.getPrimaryStage().setTitle("Answer screen");
        mainCtrl.getPrimaryStage().setScene(answer);
    }

    /**
     * Visualises a question on one of the question
     * screens according to the question type
     * @param question the question to visualise
     */
    public void showQuestion(Question question) {
        System.out.println("Received question answer: " + question.getUserAnswer().generateAnswer());
        if (question.getType() == QuestionType.ESTIMATION) {
            showEstimationQuestion((EstimationQuestion) question);
            return;
        }

        showMultipleChoiceQuestion(question);
    }

    /**
     * Sets the scene in the primary stage to the multiple choice
     * question screen
     * @param question the question to visualise
     */
    public void showMultipleChoiceQuestion(Question question) {
        mcQuestionCtrl.updateCircleColor(colors);
        mcQuestionCtrl.resetHighlight();
        mcQuestionCtrl.highlightCurrentCircle();
        mcQuestionCtrl.setup(question);
        mcQuestionCtrl.resetAnswerColors();
        mcQuestionCtrl.updateQuestionNumber();

        mcQuestionCtrl.resetAnswerColors();
        mcQuestionCtrl.enableAnswers();
        mcQuestionCtrl.startTimer();
        mcQuestionCtrl.setStartTime();
        mainCtrl.getPrimaryStage().setTitle("Question screen");
        mainCtrl.getPrimaryStage().setScene(mcQuestion);
    }

    /**
     * Sets the scene in the primary stage to the estimation screen
     *
     * @param question the estimation question to visualise
     */
    public void showEstimationQuestion(EstimationQuestion question) {
        mainCtrl.getPrimaryStage().setTitle("Estimation");
        mainCtrl.getPrimaryStage().setScene(estimationQuestion);
        estimationQuestionCtrl.startTimer();
        estimationQuestionCtrl.setup(question);
    }

    /**
     * Polls the ranked users for the current game from
     * the server
     * @return the ranked users
     */
    public List<MultiplayerUser> fetchRanking() {
        // TODO: fetch users ranked by points from server
        return new ArrayList<>();
    }

    /**
     * Displays the list of ranked users on the ranking page
     * @param rankedUsers the list of ranked users to display
     */
    public void showRanking(List<MultiplayerUser> rankedUsers) {
        // TODO: handle passed multiplayer users
        rankingCtrl.updateCircleColor(colors);
        rankingCtrl.updateQuestionNumber();
        mainCtrl.getPrimaryStage().setTitle("Ranking Screen");
        mainCtrl.getPrimaryStage().setScene(ranking);
        rankingCtrl.startTimer();
    }

    /**
     * Displays the list of ranked users on the game
     * results page
     * @param rankedUsers the list of ranked users to display
     */
    public void showResults(List<MultiplayerUser> rankedUsers) {
        // TODO: display list of ranked users on results screen
    }

    /**
     * Getter for the answeredQuestion flag
     *
     * @return boolean value for the flag
     */

    public boolean getAnsweredQuestion() {
        return this.answeredQuestion;
    }

    /**
     * Setter for the answeredQuestion flag
     *
     * @param answeredQuestion the boolean to be set to
     */

    public void setAnsweredQuestion ( boolean answeredQuestion ) {
        this.answeredQuestion = answeredQuestion;
    }

    /**
     * Returns the current answer count
     * @return the current answer count
     */
    public int getAnswerCount() {
        return answerCount;
    }

    /**
     * Returns the current user
     * @return the current user
     */
    public MultiplayerUser getUser() {
        return user;
    }

    /**
     * Returns the list of used jokers
     * @return usedJokers
     */
    public List<String> getUsedJokers() {
        return usedJokers;
    }

    /**
     * Returns the current server url
     * @return the game server url
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Returns the game index
     * @return the game index
     */
    public int getGameIndex() {
        return gameIndex;
    }

    /**
     * Register for emojis in a particular controller
     * @param ctrl the controller to register for emojis to
     */
    public void registerForEmojis(EmojiController ctrl) {
        server.registerForMessages("/topic/emoji/" + gameIndex, Emoji.class, ctrl::displayEmoji);
    }

    public void registerForHalfTime () {
        server.registerForMessages( "/topic/halfTime/" + gameIndex,
                MultiplayerUser.class ,
                (user) -> mainCtrl.halfTime(user) );
    }

    /**
     * Send an emoji to the server
     * @param e the emoji image to send
     */
    public void sendEmoji(ImageView e) {
        String[] imageComponents = e.getImage().getUrl().split("/");
        String imageName = imageComponents[imageComponents.length - 1];
        String username = user.username;
        server.send(
                "/app/emoji/" + gameIndex,
                new Emoji(imageName, username)
        );
    }

    /**
     * A getter that returns true/false whether the Double Points joker is activated this round
     * @return isActiveDoublePoints, which shows whether the DP joker is being used
     */
    public boolean getIsActiveDoublePoints(){
        return isActiveDoublePoints;
    }
    /**
     * Sets the isActiveDoublePoints to either true or false
     * @param active
     */
    public void setIsActiveDoublePoints(boolean active){
        isActiveDoublePoints=active;
    }

    /**
     * This method is called when a joker is clicked.
     * It disables the joker for further use and shows an image when the button is clicked.
     * @param joker
     * @param image
     */
    public void useJoker(StackPane joker, ImageView image){
        image.setVisible(true);
        disableJokerButton(joker);
        usedJokers.add(joker.idProperty().getValue());
    }

    /**
     * Disables the joker so it can't be used again
     * @param joker
     */
    public void disableJokerButton(StackPane joker){
        joker.setBackground(new Background(
                new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY)));
        joker.setOpacity(OPACITY);
        joker.setOnMouseClicked(null);
        joker.setCursor(Cursor.DEFAULT);
    }

    /**
     * Enables the disabled jokers for the next game.
     * @param joker
     */
    public void enableJoker(StackPane joker){
        joker.setBackground(new Background(
                new BackgroundFill(Color.color(RGB_VALUE, RGB_VALUE, RGB_VALUE), CornerRadii.EMPTY, Insets.EMPTY)));
        joker.setOpacity(STANDARD_SIZE);
        joker.setCursor(Cursor.HAND);
    }

    /**
     * Resets all jokers at the start of the game, so they can be used again.
     */
    public void resetAllJokers(){
        mcQuestionCtrl.resetDoublePoints();
        estimationQuestionCtrl.resetDoublePoints();
        //TODO: Reset all the other jokers
    }
}

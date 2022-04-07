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
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Pair;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MultiplayerGameCtrl {


    private List<Color> colors;

    private boolean answeredQuestion = false;
    private StompSession.Subscription emojiSubscription;
    private StompSession.Subscription halfTimeSubscription;

    private Timer answerTimer;

    private MainCtrl mainCtrl;
    private ServerUtils server;

    private MultiplayerUser user;

    private String serverUrl;
    private int answerCount;

    private Scene mcQuestion;
    private MultiplayerQuestionCtrl mcQuestionCtrl;

    private Scene estimationQuestion;
    private MultiplayerEstimationQuestionCtrl multiplayerEstimationQuestionCtrl;

    private Scene answer;
    private MultiplayerAnswerCtrl answerCtrl;

    private Scene ranking;
    private RankingCtrl rankingCtrl;

    private Scene results;
    private MultiplayerResultsCtrl resultsCtrl;

    private boolean isActiveDoublePoints;
    private boolean isActiveRemoveIncorrect;

    private List<String> usedJokers;

    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;
    private static final double OPACITY = 0.5;
    private static final double STANDARD_SIZE = 1.0;
    protected static final int KICK_AT_X_QUESTIONS = 3;

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
     * @param results The results controller-scene pair.
     */
    public MultiplayerGameCtrl(MainCtrl mainCtrl, ServerUtils server,
                               Pair<MultiplayerQuestionCtrl, Scene> mcQuestion,
                               Pair<MultiplayerEstimationQuestionCtrl, Scene> estimationQuestion,
                               Pair<MultiplayerAnswerCtrl, Scene> answer,
                               Pair<RankingCtrl, Scene> ranking,
                               Pair<MultiplayerResultsCtrl, Scene> results) {
        this.mainCtrl = mainCtrl;

        this.server = server;
        this.user = (MultiplayerUser) mainCtrl.getUser();
        user.unansweredQuestions = 0;
        this.colors = new ArrayList<>();

        this.usedJokers=new ArrayList<>();

        this.answerTimer = new Timer();

        this.serverUrl = mainCtrl.getServerUrl();
        this.answerCount = 0;
        isActiveDoublePoints = false;
        isActiveRemoveIncorrect = false;

        this.mcQuestionCtrl = mcQuestion.getKey();
        mcQuestionCtrl.setGameCtrl(this);
        this.mcQuestion = mcQuestion.getValue();

        this.multiplayerEstimationQuestionCtrl = estimationQuestion.getKey();
        multiplayerEstimationQuestionCtrl.setGameCtrl(this);
        this.estimationQuestion = estimationQuestion.getValue();

        this.answerCtrl = answer.getKey();
        answerCtrl.setGameCtrl(this);
        this.answer = answer.getValue();

        this.rankingCtrl = ranking.getKey();
        rankingCtrl.setGameCtrl(this);
        this.ranking = ranking.getValue();

        this.resultsCtrl = results.getKey();
        resultsCtrl.setGameCtrl(this);
        this.results = results.getValue();
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

        registerForEmojis(multiplayerEstimationQuestionCtrl);
        registerForEmojis(answerCtrl);
        registerForEmojis(mcQuestionCtrl);
        registerForHalfTime();
        resetAllJokers();
        mainCtrl.resetStreak();
        mainCtrl.setStreakScore(0L);
        setIsActiveDoublePoints(false);
        user.unansweredQuestions = 0;

        Question firstQuestion = fetchQuestion();
        showQuestion(firstQuestion);
    }

    /**
     * Polls the next question from the server using the
     * answers counter
     * @return the next question
     */
    public Question fetchQuestion() {
        return server.getQuestion(serverUrl, mainCtrl.getGameIndex(), answerCount);
    }

    /**
     * - Posts an answered question to the server, updates the
     * answer colours and redirects to the answer screen
     * - Gives double points if the joker isn't available
     * - Sets the joker as "available" after it is used,
     * even though it won't be possible to use it again
     * Introduced a streak factor to the point calculation method
     * to make the game more interesting and competitive.
     * @param answeredQuestion the answered question to post
     */
    public void postAnswer(Question answeredQuestion) {
        answerTimer = new Timer();
        answerTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            System.out.println("STREAK: "+ mainCtrl.getStreakScore());
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
            System.out.println("UTILS: "+mainCtrl.getStreakScore());
                setIsActiveDoublePoints(false);
            return server.answerDoublePointsQuestion(serverUrl, mainCtrl.getGameIndex(),
                    mainCtrl.getUser().id, answerCount, answeredQuestion,
                    mainCtrl.getStreakScore());
        }
        else{
            return server.answerQuestion(serverUrl, mainCtrl.getGameIndex(),
                    mainCtrl.getUser().id, answerCount, answeredQuestion,
                    mainCtrl.getStreakScore());
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

        mainCtrl.updateQuestionCounters(answerCtrl, colors);
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
        mainCtrl.updateQuestionCounters(mcQuestionCtrl, colors);

        mcQuestionCtrl.setup(question);
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
        mainCtrl.updateQuestionCounters(multiplayerEstimationQuestionCtrl, colors);
        multiplayerEstimationQuestionCtrl.setup(question);

        multiplayerEstimationQuestionCtrl.startTimer();
        multiplayerEstimationQuestionCtrl.setStartTime();
        mainCtrl.getPrimaryStage().setTitle("Estimation question screen");
        mainCtrl.getPrimaryStage().setScene(estimationQuestion);
    }

    /**
     * Polls the ranked users for the current game from
     * the server
     * @return the ranked users
     */
    public List<MultiplayerUser> fetchRanking() {
        return server.getRanking(serverUrl, mainCtrl.getGameIndex());
    }

    /**
     * Displays the list of ranked users on the ranking page
     * @param rankedUsers the list of ranked users to display
     */
    public void showRanking(List<MultiplayerUser> rankedUsers) {
        mainCtrl.updateQuestionCounters(rankingCtrl, colors);
        mainCtrl.getPrimaryStage().setTitle("Ranking Screen");
        mainCtrl.getPrimaryStage().setScene(ranking);
        rankingCtrl.setup(rankedUsers);
        rankingCtrl.startTimer();
    }

    /**
     * Displays the list of ranked users on the game
     * results page
     * @param rankedUsers the list of ranked users to display
     */
    public void showResults(List<MultiplayerUser> rankedUsers) {
        resultsCtrl.setup(rankedUsers);
        mainCtrl.updateQuestionCounters(resultsCtrl, colors);
        mainCtrl.getPrimaryStage().setTitle("Results Screen");
        mainCtrl.getPrimaryStage().setScene(results);
        resultsCtrl.startTimer();
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
        return mainCtrl.getGameIndex();
    }

    /**
     * Register for emojis in a particular controller
     * @param ctrl the controller to register for emojis to
     */
    public void registerForEmojis(EmojiController ctrl) {
        emojiSubscription = server.registerForMessages(
                "/topic/emoji/" + mainCtrl.getGameIndex(),
                Emoji.class,
                ctrl::displayEmoji
        );
    }

    public void registerForHalfTime () {
        halfTimeSubscription = server.registerForMessages( "/topic/halfTime/" + mainCtrl.getGameIndex(),
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
                "/app/emoji/" + mainCtrl.getGameIndex(),
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
        isActiveDoublePoints = active;
    }

    /**
     * A getter that returns true/false whether the Remove Incorrect Answer joker is activated this round
     * @return isActiveRemoveIncorrect, which shows whether the RIA joker is being used
     */
    public boolean getIsActiveRemoveIncorrect(){
        return isActiveRemoveIncorrect;
    }

    /**
     * Sets the isActiveRemoveIncorrect to either true or false
     * @param active
     */
    public void setIsActiveRemoveIncorrect(boolean active){
        isActiveRemoveIncorrect = active;
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
                new BackgroundFill(Color.web("#D6EAF8"), CornerRadii.EMPTY, Insets.EMPTY)));
        joker.setOpacity(STANDARD_SIZE);
    }

    /**
     * Resets all jokers at the start of the game, so they can be used again.
     */
    public void resetAllJokers(){
        this.usedJokers = new ArrayList<>();
        mcQuestionCtrl.resetDoublePoints();
        mcQuestionCtrl.resetReduceTime();
        multiplayerEstimationQuestionCtrl.resetDoublePoints();
        multiplayerEstimationQuestionCtrl.resetReduceTime();
        mcQuestionCtrl.resetRemoveIncorrect();
        //TODO: Reset all the other jokers
    }

    /**
     * Hides all emojis from the game
     */
    public void hideEmojis() {
        answerCtrl.hideEmoji();
        mcQuestionCtrl.hideEmoji();
        multiplayerEstimationQuestionCtrl.hideEmoji();
    }

    /**
     * Removes the emoji subscription from the current session
     */
    public void unregisterForEmojis() {
        if(server.getSession().isConnected()) {
            emojiSubscription.unsubscribe();
        }
        emojiSubscription = null;
    }

    /**
     * Send emojis to the server on emoji click.
     * @param emojiPane the emoji pane to enable
     */
    public void enableEmojis(GridPane emojiPane){
        emojiPane.getChildren().forEach(n -> {
            if(n instanceof ImageView) {
                ImageView e = (ImageView) n;
                e.setOnMouseClicked(event -> sendEmoji(e));

                String[] parts = e.getImage().getUrl().split("/");
                String emojiPath = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));
                emojiPath = emojiPath.substring(
                        0, emojiPath.length() - "classes/java/main/".length())
                        + "resources/main/client/images/" + parts[parts.length - 1];

                e.setImage(new Image(emojiPath));
            }
        });
    }

    /**
     * Disable emoji clicks
     * @param emojiPane the emoji pane to disable
     */
    public void disableEmojis(GridPane emojiPane) {
        emojiPane.getChildren().forEach(n -> {
            if(n instanceof ImageView) {
                ImageView e = (ImageView) n;
                e.setOnMouseClicked(null);
            }
        });
    }

    /**
     * Visualise emoji on the screen
     * @param emoji the emoji to visualise
     * @param emojiImage the image to change
     * @param emojiText the text to change
     */
    public void displayEmoji(Emoji emoji, ImageView emojiImage, Text emojiText) {
        String emojiPath = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));
        emojiPath = emojiPath.substring(
                0, emojiPath.length() - "classes/java/main/".length())
                + "resources/main/client/images/" + emoji.getImageName();
        emojiImage.setImage(new Image(emojiPath));
        emojiText.setText(emoji.getUsername());
    }

    public void redirectFromQuestion(){
        MultiplayerUser user = getUser();
        if (!getAnsweredQuestion()) {
            user.unansweredQuestions++;
            if (user.unansweredQuestions == KICK_AT_X_QUESTIONS) {
                try {
                    server.removeMultiplayerUserFromGame(server.getURL(), mainCtrl.getGameIndex(), user.id);
                } catch(WebApplicationException e) {
                    System.out.println("User to remove not found!");
                }

                mainCtrl.killThread();

                if(server.getSession() != null && server.getSession().isConnected()) {
                    unregisterForEmojis();
                    server.getSession().disconnect();
                }
                hideEmojis();
                mainCtrl.showHome();
                mainCtrl.bindUser(null);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle ("Kicked :(");
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("You've been kicked for not answering 3 question in a row!");
                alert.show();

                return;
            }
        } else {
            user.unansweredQuestions = 0;
        }

        setAnsweredQuestion(false);
    }

    public void unregisterForHalfTime() {
        if ( server.getSession().isConnected() ) {
            halfTimeSubscription.unsubscribe();
        }
        halfTimeSubscription = null;
    }

    public void resetGameCtrl() {
        mainCtrl.resetMainCtrl();
        resetAllJokers();
        hideEmojis();
        this.answerCount = 0;
        this.colors = new ArrayList<>();
        this.user.resetScore();
    }

    /**
     * Populates a given score table with a sorted list of users
     * @param scoreTable the score table to populate
     * @param users the list of users to populate
     */
    public void populateRanking(TableView<MultiplayerUser> scoreTable, List<MultiplayerUser> users) {
        try {
            scoreTable.getItems().clear();

            for (MultiplayerUser user : users) {
                scoreTable.getItems().add(user);
            }

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }
}

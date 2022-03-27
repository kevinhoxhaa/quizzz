package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.Answer;
import commons.models.Emoji;
import commons.models.EstimationQuestion;
import commons.models.Question;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.Cursor;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EstimationQuestionCtrl implements SceneController, QuestionNumController, EmojiController {

    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double TIMEOUT = 8.0;
    private static final double STANDARD_SIZE = 1.0;
    private static final int KICK_AT_X_QUESTIONS = 3;

    private static final double MILLISECONDS_PER_SECONDS = 1000.0;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private MultiplayerGameCtrl gameCtrl;

    private Question currentQuestion;
    private double startTime;
    private List<StackPane> jokers;

    @FXML
    private Label yourAnswer;

    @FXML
    private Text questionNum;

    @FXML
    private HBox circles;

    @FXML
    private Text currentScore;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private Text questionDescription;

    @FXML
    private TextField answerField;

    @FXML
    private StackPane doublePoints;

    @FXML
    private StackPane removeIncorrect;

    @FXML
    private StackPane reduceTime;

    @FXML
    private ImageView doublePointsImage;

    @FXML
    private ImageView removeIncorrectImage;

    @FXML
    private ImageView reduceTimeImage;

    @FXML
    private ImageView questionImg;

    @FXML
    private GridPane emojiPane;

    @FXML
    private ImageView emojiImage;

    @FXML
    private Text emojiText;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Send emojis to the server on emoji click
     */
    public void enableEmojis() {
        emojiPane.getChildren().forEach(n -> {
            if(n instanceof ImageView) {
                ImageView e = (ImageView) n;
                e.setOnMouseClicked(event -> gameCtrl.sendEmoji(e));
                e.setCursor(Cursor.HAND);

                String[] parts = e.getImage().getUrl().split("/");
                String emojiPath = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));
                emojiPath = emojiPath.substring(
                        "file:/".length(), emojiPath.length() - "classes/java/main/".length())
                        + "resources/main/client/images/" + parts[parts.length - 1];

                e.setImage(new Image(emojiPath));
            }
        });
    }
    /**
     * Disable emoji clicks
     */
    public void disableEmojis() {
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
     */
    @Override
    public void displayEmoji(Emoji emoji) {
        String emojiPath = String.valueOf(ServerUtils.class.getClassLoader().getResource(""));
        emojiPath = emojiPath.substring(
                "file:/".length(), emojiPath.length() - "classes/java/main/".length())
                + "resources/main/client/images/" + emoji.getImageName();
        emojiImage.setImage(new Image(emojiPath));
        emojiText.setText(emoji.getUsername());
    }

    /**
     * Hides the emoji from the image view
     */
    @Override
    public void hideEmoji() {
        emojiImage.setImage(null);
        emojiText.setText("");
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    public void setup(EstimationQuestion question) {
        enableEmojis();
        jokers = new ArrayList<>();
        jokers.add(doublePoints);
        jokers.add(removeIncorrect);
        jokers.add(reduceTime);
        removeIncorrect.setDisable(true);
        removeIncorrect.setBackground(new Background(
                new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        for(StackPane joker:jokers){
            if(gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())){
                gameCtrl.disableJokerButton(joker);
            }
        }

        currentScore.setText("Score: " + gameCtrl.getUser().points);
        currentQuestion = question;
        questionDescription.setText("How much energy in Wh does " + question.getActivity().title + " use?");

        doublePointsImage.setVisible(false);
        try {
            questionImg.setImage(server.fetchImage(mainCtrl.getServerUrl(), currentQuestion.getImagePath()));
        }
        catch (IOException e){

        }
    }

    /**
     * Captures the exact time the question page started showing used for measuring the time
     * players needed for answering the question.
     */
    protected void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Returns the time since the timer started, in seconds.
     * For now, a placeholder method.
     *
     * @return the time since the timer started, in seconds.
     */
    private double getSeconds() {
        return (System.currentTimeMillis() - startTime) / MILLISECONDS_PER_SECONDS;
    }

    @FXML
    protected void onAnswerPostClick() {
        gameCtrl.setAnsweredQuestion( true );
        try {
            long answer = Long.parseLong(answerField.getText());
            currentQuestion.setUserAnswer(new Answer(answer), getSeconds());
            yourAnswer.setText("Your answer: " + answer);
        } catch(NumberFormatException ex) {
            System.out.println("Enter a number!");
        }
    }

    @Override
    public void redirect() {
        MultiplayerUser user = gameCtrl.getUser();
        if (!gameCtrl.getAnsweredQuestion()) {
            user.unansweredQuestions++;
            if (user.unansweredQuestions == KICK_AT_X_QUESTIONS) {
                try {
                    server.removeMultiplayerUser(server.getURL(), user);
                } catch(WebApplicationException e) {
                    System.out.println("User to remove not found!");
                }

                mainCtrl.killThread();

                if(server.getSession() != null && server.getSession().isConnected()) {
                    gameCtrl.unregisterForEmojis();
                    server.getSession().disconnect();
                }
                gameCtrl.hideEmojis();
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

        gameCtrl.setAnsweredQuestion(false);

        try {
            if (currentQuestion.getUserAnswer().getLongAnswer().equals(-1L)) {
                long answer = Long.parseLong(answerField.getText());
                currentQuestion.setUserAnswer(new Answer(answer), TIMEOUT);
            }
        } catch(NumberFormatException ex) {
            System.out.println("Enter a number!");
        }

        disableEmojis();
        answerField.setText("");
        yourAnswer.setText("Your answer: ");
        gameCtrl.postAnswer(currentQuestion);
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

    /**
     * This method is called when the double points joker is clicked.
     * It gives double points for the current question if the answer is correct.
     */
    @FXML
    public void useDoublePoints(){
        gameCtrl.setIsActiveDoublePoints(true);
        gameCtrl.useJoker(doublePoints,doublePointsImage);
    }

    /**
     * This method resets the double point jokers so that it can be used again when another game starts
     */
    public void resetDoublePoints(){
        doublePoints.setOnMouseClicked(event -> useDoublePoints());
        gameCtrl.enableJoker(doublePoints);
    }

    /**
     * The method called when the cursor enters the button double points.
     * Sets double points' background color according to whether it is selected.
     */
    @FXML
    protected void enterDoublePoints(){
        enterJoker(doublePoints);
    }

    /**
     * The method called when the cursor enters the button remove incorrect question.
     * Sets remove incorrect question's background color according to whether it is selected.
     */
    @FXML
    protected void enterRemoveIncorrect(){
        enterJoker(removeIncorrect);
    }

    /**
     * The method called when the cursor enters the button reduce time for others.
     * Sets reduce time for others' background color according to whether it is selected.
     */
    @FXML
    protected void enterReduceTime(){
        enterJoker(reduceTime);
    }

    /**
     * A general method for setting a joker button's background color upon the cursor enters it,
     * according to whether it is already used.
     *
     * @param jokerBtn The joker button to be recolored.
     */
    private void enterJoker(StackPane jokerBtn) {
        if (!gameCtrl.getUsedJokers().contains(jokerBtn.idProperty().getValue())) {
            jokerBtn.setBackground(new Background(
                    new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * The method called upon loading the estimation question scene,
     * and when the cursor leaves either one of the joker buttons.
     * Resets all joker buttons' background color according to whether they are already used.
     */
    @FXML
    public void resetJokerColors() {

        for (StackPane joker : jokers) {
            if (!gameCtrl.getUsedJokers().contains(joker.idProperty().getValue()) && joker != removeIncorrect) {
                joker.setBackground(new Background(
                        new BackgroundFill(Color.color(gameCtrl.RGB_VALUE,gameCtrl.RGB_VALUE,gameCtrl.RGB_VALUE),
                                CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    /**
     * Getter for the circles bar
     *
     * @return circles
     */
    public HBox getCirclesHBox() {
        return circles;
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle circle = (Circle) circles.getChildren().get(gameCtrl.getAnswerCount());
        circle.setFill(Color.DARKGRAY);
        circle.setStrokeWidth(CIRCLE_BORDER_SIZE);
    }

    /**
     * Resets the highlighting of the circle borders
     */
    public void resetHighlight() {
        for (int i = 0; i < circles.getChildren().size(); i++) {
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setStrokeWidth(STANDARD_SIZE);
        }
    }

    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < gameCtrl.getAnswerCount(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(colors.get(i));
        }
    }

    @Override
    public void resetCircleColor() {
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (gameCtrl.getAnswerCount() + 1));
    }
}

package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Answer;
import commons.models.Emoji;
import commons.models.EstimationQuestion;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerEstimationQuestionCtrl extends AbstractEstimationQuestionCtrl implements EmojiController {
    private MultiplayerGameCtrl gameCtrl;
    private List<StackPane> jokers;

    @FXML
    private Button quitButton;
    @FXML
    private Button submitButton;

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
    private GridPane emojiPane;
    @FXML
    private ImageView emojiImage;
    @FXML
    private Text emojiText;

    private static final double TIMEOUT = 8.0;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerEstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Sets up the multiplayer estimation question page for showing.
     * @param question the question that the screen will be based upon
     */
    public void setup(EstimationQuestion question) {
        gameCtrl.enableEmojis(emojiPane);
        jokers = new ArrayList<>();
        jokers.add(doublePoints);
        jokers.add(removeIncorrect);
        jokers.add(reduceTime);

        List<StackPane> availableJokers=new ArrayList<>();
        removeIncorrect.setDisable(true);
        removeIncorrect.setBackground(new Background(
                new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        for(StackPane joker:jokers){
            if(gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())){
                gameCtrl.disableJokerButton(joker);
            }
            else{
                availableJokers.add(joker);
            }
        }

        gameCtrl.enableJokers(availableJokers,false);
        currentQuestion = question;
        super.setup(gameCtrl.getUser().points);

        doublePointsImage.setVisible(false);
        removeIncorrectImage.setVisible(false);
        reduceTimeImage.setVisible(false);
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Called when the player clicks the button next to the input field
     * Saves the user input and updates the screen accordingly
     */
    @FXML
    protected void onSubmit() {
        gameCtrl.setAnsweredQuestion( true );
        super.onSubmit();
    }

    /**
     * Redirects the player to the corresponding answer page.
     * If the player was inactive for 3 questions, kicks them.
     * Called when the timer is up.
     */
    @Override
    public void redirect() {
        gameCtrl.redirectFromQuestion();

        try {
            if (currentQuestion.getUserAnswer().getLongAnswer().equals(-1L)) {
                long answer = Long.parseLong(userInput.getText());
                currentQuestion.setUserAnswer(new Answer(answer), TIMEOUT);
            }
        } catch(NumberFormatException ex) {
            System.out.println("Enter a number!");
        }

        disableJokers();
        gameCtrl.disableEmojis(emojiPane);
        mainCtrl.addScore(mainCtrl.getUser(),currentQuestion);
        gameCtrl.postAnswer(currentQuestion);
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Quits the game. Called when clicking 'quit'.
     */
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
                        new BackgroundFill(Color.web("#D6EAF8"),
                                CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        super.highlightCurrentCircle(gameCtrl.getAnswerCount());
    }

    /**
     * Updates the question number shown on screen
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (gameCtrl.getAnswerCount() + 1));
    }

    /**
     * Visualise emoji on the screen
     * @param emoji the emoji to visualise
     */
    @Override
    public void displayEmoji(Emoji emoji) {
        gameCtrl.displayEmoji(emoji, emojiImage, emojiText);
    }

    /**
     * Hides the emoji from the image view
     */
    public void hideEmoji() {
        emojiImage.setImage(null);
        emojiText.setText("");
    }

    /**
     * This method is called when the reduceTime joker is clicked
     * It halves the time for everyone in the lobby
     */

    @FXML
    public void useReduceTime() {
        server.send ( "/app/halfTime/" + gameCtrl.getGameIndex(), mainCtrl.getUser() );
        gameCtrl.useJoker( reduceTime, reduceTimeImage );
    }

    /**
     * This method resets the double point jokers so that it can be used again when another game starts
     */
    public void resetReduceTime(){
        reduceTime.setOnMouseClicked(event -> useReduceTime());
        gameCtrl.enableJoker(reduceTime);
    }

    /**
     * Sets all buttons hover cursors to hand
     */
    @Override
    public void setupHoverCursor() {
        doublePoints.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        removeIncorrect.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        reduceTime.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));

        emojiPane.getChildren().forEach(c -> {
            if(c instanceof ImageView) {
                c.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
            }
        });

        quitButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        submitButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
    }

    /**
     * Disables all interaction with the jokers buttons.
     */
    public void disableJokers() {
        doublePoints.setOnMouseClicked(null);
        removeIncorrect.setOnMouseClicked(null);
        reduceTime.setOnMouseClicked(null);

        doublePoints.setOnMouseEntered(null);
        removeIncorrect.setOnMouseEntered(null);
        reduceTime.setOnMouseEntered(null);
    }
}

package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Answer;
import commons.models.Emoji;
import commons.models.Question;
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
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MultiplayerQuestionCtrl extends AbstractMultichoiceQuestionCtrl
        implements EmojiController {

    private MultiplayerGameCtrl gameCtrl;
    private List<StackPane> jokers;

    @FXML
    private Button quitButton;

    @FXML
    private GridPane emojiPane;
    @FXML
    private ImageView emojiImage;
    @FXML
    private Text emojiText;

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

    /**
     * Creates a controller for the multiplayer question screen, with the given server and main controller.
     * Creates the list answerButtons for iterating through all of these.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Sets up the question page scene: <br>
     * - Sets up the question/answers according to the type of the question given <br>
     * - Fills the answerButtons list for iterations <br>
     * - Resets all buttons to their default colors
     *
     * @param question the question instance upon which the setup is based
     */
    public void setup(Question question) {
        jokers=new ArrayList<>();
        jokers.add(doublePoints);
        jokers.add(removeIncorrect);
        jokers.add(reduceTime);

        for(StackPane joker:jokers){
            if(gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())){
                gameCtrl.disableJokerButton(joker);
            }
        }

        gameCtrl.setAnsweredQuestion ( false );

        super.setup(question, gameCtrl.getUser().points);

        resetAnswerClickability();
        disabledAnswer = null;
        gameCtrl.enableEmojis(emojiPane);
        doublePointsImage.setVisible(false);
        removeIncorrectImage.setVisible(false);
        reduceTimeImage.setVisible(false);
    }

    /**
     * Saves the answer selected last by the user, as well as the amount of time it took.
     * Changes the scene visuals accordingly.
     *
     * @param answerButton The answer button pressed.
     * @param answer       The answer corresponding to the answer button.
     */
    protected void onAnswerClicked(StackPane answerButton, Answer answer) {
        gameCtrl.setAnsweredQuestion(true);
        super.onAnswerClicked(answerButton, answer);
    }

    /**
     * The method called when the cursor enters the button answerTop. Checks if the answer is disabled by the joker.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    @Override
    protected void enterAnswerTop() {
        enterAnswer(answerTop);
    }

    /**
     * The method called when the cursor enters the button answerMid. Checks if the answer is disabled by the joker.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    @Override
    protected void enterAnswerMid() {
        enterAnswer(answerMid);
    }

    /**
     * The method called when the cursor enters the button answerBot. Checks if the answer is disabled by the joker.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    @Override
    protected void enterAnswerBot() {
        enterAnswer(answerBot);
    }

    /**
     * The method called when the cursor exits the button answerTop. Checks if the answer is disabled by the joker.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    protected void exitAnswerTop() {
        resetAnswerColors(answerTop);
    }

    /**
     * The method called when the cursor exits the button answerMid. Checks if the button is disabled by the joker.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    protected void exitAnswerMid() {
        resetAnswerColors(answerMid);
    }

    /**
     * The method called when the cursor exits the button answerBot. Checks if the answer is disabled by the joker.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    protected void exitAnswerBot() {
        resetAnswerColors(answerBot);
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
     * The method is called upon loading the question scene, to make sure each answer button is clickable.
     * Else an answer is disabled after a user uses the Remove Incorrect Answer Joker.
     */
    private void resetAnswerClickability(){
        for(StackPane answerBtn : answerButtons){
            answerBtn.setDisable(false);
        }
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
                    new BackgroundFill(Color.web("#85C1E9"), CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the joker buttons.
     * Resets all joker buttons' background color according to whether they are already used.
     */
    @FXML
    public void resetJokerColors() {
        for (StackPane joker : jokers) {
            if (!gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())) {
                joker.setBackground(new Background(
                        new BackgroundFill(Color.web("#D6EAF8"),
                                CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
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
    public void resetDoublePoints(){
        doublePoints.setOnMouseClicked(event -> useDoublePoints());
        gameCtrl.enableJoker(doublePoints);
    }

    /**
     * This method resets the double point jokers so that it can be used again when another game starts
     */
    public void resetReduceTime(){
        reduceTime.setOnMouseClicked(event -> useReduceTime());
        gameCtrl.enableJoker(reduceTime);
    }

    /**
     * This method is called when the remove incorrect answer joker is clicked.
     * It removes a randomly selected incorrect answer from the multiple choice questions.
     * Disables the selected incorrect answer's stackPane.
     */
    public void useRemoveIncorrect(){
        gameCtrl.setIsActiveRemoveIncorrect(true);
        gameCtrl.useJoker(removeIncorrect, removeIncorrectImage);

        List<Answer> incorrectAnswers = new ArrayList<>();

        incorrectAnswers.add(answerTopAnswer);
        incorrectAnswers.add(answerMidAnswer);
        incorrectAnswers.add(answerBotAnswer);
        incorrectAnswers.remove(currentQuestion.getUserAnswer());
        incorrectAnswers.remove(currentQuestion.generateCorrectAnswer());
        Collections.shuffle(incorrectAnswers);
        Answer answerToRemove = incorrectAnswers.get(0);

        for(Pair pair : answerButtonPairs){
            StackPane button = (StackPane) pair.getKey();
            Answer answer = (Answer) pair.getValue();
            if(answer.equals(answerToRemove)){
                button.setDisable(true);
                button.setBackground(new Background(
                        new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                disabledAnswer = button;
            }
        }
    }

    /**
     * This method resets the remove incorrect answer jokers so that it can be used when
     * another game starts.
     */
    public void resetRemoveIncorrect(){
        removeIncorrect.setOnMouseClicked(event -> useRemoveIncorrect());
        gameCtrl.enableJoker(removeIncorrect);
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Disables all interaction with the answer buttons.
     */
    public void disableAnswers() {
        answerTop.setOnMouseEntered(null);
        answerMid.setOnMouseEntered(null);
        answerBot.setOnMouseEntered(null);
        answerTop.setOnMouseClicked(null);
        answerMid.setOnMouseClicked(null);
        answerBot.setOnMouseClicked(null);
    }

    /**
     * Enables interaction with the answer buttons.
     */
    public void enableAnswers() {
        answerTop.setOnMouseClicked(event -> onAnswerTopClicked());
        answerMid.setOnMouseClicked(event -> onAnswerMidClicked());
        answerBot.setOnMouseClicked(event -> onAnswerBotClicked());
    }

    /**
     * Redirects the player to the corresponding answer page.
     * If the player was inactive for 3 questions, kicks them.
     * Called when the timer is up.
     */
    @Override
    public void redirect() {
        disableAnswers();
        gameCtrl.disableEmojis(emojiPane);
        gameCtrl.redirectFromQuestion();
        gameCtrl.postAnswer(currentQuestion);
    }

    /**
     * Quits the game. Called when clicking 'quit'.
     */
    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(gameCtrl.getAnswerCount());
    }

    /**
     * Updates the question number on the top of the screen.
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
     * Removes the emoji from the image view
     */
    public void hideEmoji() {
        emojiImage.setImage(null);
        emojiText.setText("");
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Sets hover cursors to all buttons to hand
     */
    @Override
    public void setupHoverCursor() {
        answerTop.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        answerMid.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        answerBot.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));

        doublePoints.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        removeIncorrect.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        reduceTime.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));

        emojiPane.getChildren().forEach(c -> {
            if(c instanceof ImageView) {
                c.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
                System.out.println("Setting cursors...");
            }
        });

        quitButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
    }
}

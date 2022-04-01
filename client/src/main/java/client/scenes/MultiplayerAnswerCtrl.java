package client.scenes;

import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.Emoji;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.List;

public class MultiplayerAnswerCtrl extends AbstractAnswerCtrl implements EmojiController {

    private MultiplayerGameCtrl gameCtrl;

    @FXML
    private ListView<String> correctPlayers;

    @FXML
    private ImageView emojiImage;
    @FXML
    private GridPane emojiPane;
    @FXML
    private Text emojiText;


    /**
     * Creates a controller for the multiplayer answer screen, with the given server and main controller.
     *
     * @param mainCtrl
     */
    @Inject
    public MultiplayerAnswerCtrl(MainCtrl mainCtrl) {
        super(mainCtrl);
    }

    /**
     * Sets up the answer page screen: <br>
     * - Sets up a fitting message (with corresponding color) for the player
     * based on if the player answered correctly or not. <br>
     * - Fills in the question and correct answer in their corresponding text boxes. <br>
     * - Fills the correctPlayers ListView with players that answered correctly.
     *
     * @param prevQuestion   The question that has just been asked to the players.
     * @param correctPlayers A list of all the players that answered the precious question correctly.
     */
    public void setup(Question prevQuestion, List<MultiplayerUser> correctPlayers) {
        super.setup(prevQuestion);
        gameCtrl.enableEmojis(emojiPane);
        this.correctPlayers.getItems().clear();
        correctPlayers.forEach(u -> this.correctPlayers.getItems().add(u.username));
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
     * Initiates the timer countdown and animation
     */
    @Override
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Called when the timer is up.
     * Redirects the player to the appropriate one of the following: next question,
     * ranking page, results page.
     */
    @Override
    public void redirect() {
        gameCtrl.disableEmojis(emojiPane);
        if(gameCtrl.getAnswerCount() == mainCtrl.getQuestionsPerGame()/2) {
            List<MultiplayerUser> rankedUsers = gameCtrl.fetchRanking();
            gameCtrl.showRanking(rankedUsers);
            return;
        }

        if(gameCtrl.getAnswerCount() == mainCtrl.getQuestionsPerGame()) {
            List<MultiplayerUser> rankedUsers = gameCtrl.fetchRanking();
            gameCtrl.showResults(rankedUsers);
            return;
        }

        Question nextQuestion = gameCtrl.fetchQuestion();
        gameCtrl.showQuestion(nextQuestion);
    }

    /**
     * Called when the quit button is pressed
     */
    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

    /**
     * Updates the question number on screen
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (gameCtrl.getAnswerCount()));
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(gameCtrl.getAnswerCount());
    }
}

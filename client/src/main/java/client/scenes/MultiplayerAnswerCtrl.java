package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.Emoji;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.List;

public class MultiplayerAnswerCtrl extends AbstractAnswerCtrl implements SceneController, EmojiController {

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
    protected void setup(Question prevQuestion, List<MultiplayerUser> correctPlayers) {

        super.setup(prevQuestion, gameCtrl.getUser().points);

        enableEmojis();
        this.correctPlayers.getItems().clear();
        correctPlayers.forEach(u -> this.correctPlayers.getItems().add(u.username));
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
     * Removes the emoji from the image view
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

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    @Override
    public void redirect() {
        disableEmojis();
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

    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

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

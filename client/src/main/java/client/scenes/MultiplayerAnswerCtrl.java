package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.Emoji;
import commons.models.EstimationQuestion;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class MultiplayerAnswerCtrl implements SceneController, QuestionNumController, EmojiController {

    private static final int HALF_QUESTIONS = 10;
    private static final int TOTAL_QUESTIONS = 20;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private MultiplayerGameCtrl gameCtrl;

    @FXML
    private VBox answerPane;

    @FXML
    private Text activity;

    @FXML
    private Text answer;

    @FXML
    private Text answerResponse;

    @FXML
    private Text questionNum;

    @FXML
    private GridPane emojiPane;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private HBox circles;

    @FXML
    private ListView<String> correctPlayers;

    @FXML
    private Text currentScore;

    @FXML
    private ImageView emojiImage;

    @FXML
    private Text emojiText;


    /**
     * Creates a controller for the multiplayer answer screen, with the given server and main controller.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerAnswerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
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

        if (prevQuestion.hasCorrectUserAnswer()) {
            this.answerResponse.setText("Well done!");
            answerPane.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            this.answerResponse.setText("By making mistakes, we learn!");
            answerPane.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        currentScore.setText("Score: " + gameCtrl.getUser().points);

        switch (prevQuestion.getType()) {
            case CONSUMPTION:
                setupConsumptionAnswer(prevQuestion);
                break;
            case COMPARISON:
                setupComparisonAnswer(prevQuestion);
                break;
            case CHOICE:
                setupChoiceAnswer(prevQuestion);
                break;
            case ESTIMATION:
                setupEstimationAnswer(prevQuestion);
                break;
        }

        startTimer();
        enableEmojis();
        this.correctPlayers.getItems().clear();
        correctPlayers.forEach(u -> this.correctPlayers.getItems().add(u.username));
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a consumption question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupConsumptionAnswer(Question prevQuestion) {
        ConsumptionQuestion prevConsQuestion = (ConsumptionQuestion) prevQuestion;

        this.activity.setText(
                String.format("How much energy does %s cost?",
                        prevConsQuestion.getActivity().title)
        );

        this.answer.setText(Long.toString(prevConsQuestion.getActivity().consumption));
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a comparison question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupComparisonAnswer(Question prevQuestion) {
        ComparisonQuestion prevCompQuestion = (ComparisonQuestion) prevQuestion;

        this.activity.setText(
                String.format("Does %s use more, less, or the same amount of energy as %s?",
                        prevCompQuestion.getFirstActivity().title,
                        prevCompQuestion.getSecondActivity().title)
        );

        if (prevCompQuestion.getFirstActivity().consumption > prevCompQuestion.getSecondActivity().consumption) {
            this.answer.setText("MORE");
        } else if (prevCompQuestion.getFirstActivity().consumption < prevCompQuestion.getSecondActivity().consumption) {
            this.answer.setText("LESS");
        } else {
            this.answer.setText("EQUAL");
        }
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a choice question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupChoiceAnswer(Question prevQuestion) {
        ChoiceQuestion prevChoiceQuestion = (ChoiceQuestion) prevQuestion;

        this.activity.setText(
                String.format("What could you do instead of %s to consume less energy?",
                        prevChoiceQuestion.getComparedActivity().title)
        );

        this.answer.setText(prevChoiceQuestion.getAnswer().title);
    }

    /**
     * Sets up the previous question and correct answer for an answer page of an estimation question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupEstimationAnswer(Question prevQuestion) {
        EstimationQuestion prevEstimQuestion = (EstimationQuestion) prevQuestion;

        this.activity.setText(
                String.format("How much energy do you think that %s consumes?",
                        prevEstimQuestion.getActivity().title)
        );

        this.answer.setText(Long.toString(prevEstimQuestion.getActivity().consumption));
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
        if(gameCtrl.getAnswerCount() == HALF_QUESTIONS) {
            List<MultiplayerUser> rankedUsers = gameCtrl.fetchRanking();
            gameCtrl.showRanking(rankedUsers);
            return;
        }

        if(gameCtrl.getAnswerCount() == TOTAL_QUESTIONS) {
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

    /**
     * Getter for the current question number
     *
     * @return questionNum
     */
    public Text getQuestionNum() {
        return questionNum;
    }

    /**
     * Getter for the circles bar
     *
     * @return circles
     */
    public HBox getCircles() {
        return circles;
    }

    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < gameCtrl.getAnswerCount(); i++) {
            Circle circle = (Circle) getCircles().getChildren().get(i);
            circle.setFill(colors.get(i));
        }
    }

    @Override
    public void resetCircleColor() {
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCircles().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + (gameCtrl.getAnswerCount() + 1));
    }
}

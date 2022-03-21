package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.EstimationQuestion;
import commons.models.Question;
import commons.models.SoloGame;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;


public class SoloAnswerCtrl implements SceneController, QuestionNumController {

    private static final int QUESTIONS_PER_GAME = 20;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
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
    private Text currentScore;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private HBox circles;

    @FXML
    private ListView<String> correctPlayers;
    private SoloGame game;


    /**
     * Creates a controller for the multiplayer answer screen, with the given server and main controller.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloAnswerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets up the answer page screen: <br>
     * - Sets up a fitting message (with corresponding color) for the player
     * based on if the player answered correctly or not. <br>
     * - Fills in the question and correct answer in their corresponding text boxes. <br>
     *
     * @param soloGame The solo game instance
     * @param colors   The list of colors associated with the past questions
     */
    protected void setup(SoloGame soloGame, List<Color> colors) {
        this.game = soloGame;
        Question prevQuestion = soloGame.getCurrentQuestion();
        if (prevQuestion.hasCorrectUserAnswer()) {
            mainCtrl.addScore(prevQuestion.calculatePoints());
            currentScore.setFill(Color.GREEN);
            this.answerResponse.setText("Well done!");
            answerPane.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            this.answerResponse.setText("By making mistakes, we learn!");
            currentScore.setFill(Color.DARKRED);
            answerPane.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        currentScore.setText(String.format("Score: %d", mainCtrl.getSoloScore()));

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

        updateQuestionNumber();
        updateCircleColor(colors);
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

        this.answer.setText(prevChoiceQuestion.getAnswer().toString());
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
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * redirects to:
     * - either the next question
     * - or the results page
     */
    @Override
    public void redirect() {
        if (game.incrementCurrentQuestionNum() < QUESTIONS_PER_GAME) {
            mainCtrl.showSoloQuestion(game);
        } else {
            mainCtrl.showSoloResults(game);
        }
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
    public HBox getCirclesHBox() {
        return circles;
    }

    /**
     * Updates the colors of the little circles based on the array given
     *
     * @param colors Is the list of colors of previous answers(green/red depending on their correctness)
     */
    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < colors.size(); i++) {
            Circle c = (Circle) getCirclesHBox().getChildren().get(i);
            c.setFill(colors.get(i));
        }
    }

    /**
     * Resets the colors of the little circles to gray.
     */
    @Override
    public void resetCircleColor() {
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Updates the question number on the top of the screen.
     */
    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + (game.getCurrentQuestionNum() + 1));
    }

    /**
     * Handles the user clicking the quit button.
     */
    @Override
    @FXML
    public void onQuit() {
        mainCtrl.killThread();
        mainCtrl.showHome();
    }
}

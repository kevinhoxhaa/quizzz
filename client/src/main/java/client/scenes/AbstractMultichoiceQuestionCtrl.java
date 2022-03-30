package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.Activity;
import commons.models.Answer;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static commons.utils.CompareType.EQUAL;
import static commons.utils.CompareType.LARGER;
import static commons.utils.CompareType.SMALLER;

public abstract class AbstractMultichoiceQuestionCtrl implements SceneController, QuestionNumController {
    protected final ServerUtils server;
    protected final MainCtrl mainCtrl;

    protected Question currentQuestion;
    protected double startTime;

    protected Answer answerTopAnswer;
    protected Answer answerMidAnswer;
    protected Answer answerBotAnswer;

    protected List<StackPane> answerButtons;
    protected StackPane selectedAnswerButton;

    @FXML
    protected StackPane answerTop;
    @FXML
    protected StackPane answerMid;
    @FXML
    protected StackPane answerBot;
    @FXML
    protected Text answerTopText;
    @FXML
    protected Text answerMidText;
    @FXML
    protected Text answerBotText;

    @FXML
    protected Text currentScore;

    @FXML
    protected Text activityText;
    @FXML
    protected Text questionNum;
    @FXML
    protected ImageView questionImg;

    @FXML
    protected ProgressIndicator countdownCircle;

    @FXML
    protected HBox circles;

    protected static final double MILLISECONDS_PER_SECONDS = 1000.0;
    protected static final double THICK_CIRCLE_BORDER_SIZE = 1.7;
    protected static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;


    /**
     * Creates a controller for the question screen, with the given server and main controller.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    protected AbstractMultichoiceQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets up the question page scene: <br>
     * - Sets up the question/answers according to the type of the question given <br>
     * - Fills the answerButtons list for iterations <br>
     * - Resets all buttons to their default colors
     *
     * @param question the question instance upon which the setup is based
     * @param points the score of the player
     */
    protected void setup(Question question, long points) {
        this.currentQuestion = question;
        this.selectedAnswerButton = null;
        currentScore.setText(String.valueOf(points));

        try {
            questionImg.setImage(server.fetchImage(mainCtrl.getServerUrl(), currentQuestion.getImagePath()));
        }
        catch (IOException e){
        }

        switch (question.getType()) {
            case CONSUMPTION:
                setupConsumptionQuestion(question);
                break;
            case COMPARISON:
                setupComparisonQuestion(question);
                break;
            case CHOICE:
                setupChoiceQuestion(question);
                break;
        }

        this.answerButtons = new ArrayList<>();
        this.answerButtons.add(answerTop);
        this.answerButtons.add(answerMid);
        this.answerButtons.add(answerBot);

        for (StackPane answerBtnLoop : answerButtons) {
            answerBtnLoop.setStyle("-fx-border-width: 1; -fx-border-color: black");
            answerBtnLoop.getChildren().get(0).setStyle("-fx-font-weight: normal");
        }
        resetAnswerColors();
    }

    /**
     * Sets up the questions and answers on the page for the given comparison question
     *
     * @param generalQuestion the given question
     */
    protected void setupComparisonQuestion(Question generalQuestion) {
        ComparisonQuestion question = (ComparisonQuestion) generalQuestion;

        activityText.setText(
                String.format("Does %s use more, less, or the same amount of energy as %s?",
                        question.getFirstActivity().title, question.getSecondActivity().title)
        );
        answerTopText.setText("MORE");
        answerMidText.setText("EQUAL");
        answerBotText.setText("LESS");

        answerTopAnswer = new Answer(LARGER);
        answerMidAnswer = new Answer(EQUAL);
        answerBotAnswer = new Answer(SMALLER);
    }

    /**
     * Sets up the questions and answers on the page for the given consumption question
     *
     * @param generalQuestion the given question
     */
    private void setupConsumptionQuestion(Question generalQuestion) {
        ConsumptionQuestion question = (ConsumptionQuestion) generalQuestion;

        activityText.setText(
                String.format("How much energy does %s cost?",
                        question.getActivity().title)
        );

        List<Long> answers = question.getAnswers();

        answerTopText.setText(answers.get(0).toString());
        answerMidText.setText(answers.get(1).toString());
        answerBotText.setText(answers.get(2).toString());

        answerTopAnswer = new Answer(answers.get(0));
        answerMidAnswer = new Answer(answers.get(1));
        answerBotAnswer = new Answer(answers.get(2));
    }

    /**
     * Sets up the questions and answers on the page for the given choice question
     *
     * @param generalQuestion the given question
     */
    private void setupChoiceQuestion(Question generalQuestion) {
        ChoiceQuestion question = (ChoiceQuestion) generalQuestion;

        activityText.setText(
                String.format("What could you do instead of %s to consume less energy?",
                        question.getComparedActivity().title)
        );

        List<Activity> answers = question.getActivities();
        answers.remove(question.getComparedActivity());
        Collections.shuffle(answers);

        answerTopText.setText(answers.get(0).title);
        answerMidText.setText(answers.get(1).title);
        answerBotText.setText(answers.get(2).title);

        answerTopAnswer = new Answer(answers.get(0));
        answerMidAnswer = new Answer(answers.get(1));
        answerBotAnswer = new Answer(answers.get(2));
    }

    /**
     * Saves the answer selected last by the user, as well as the amount of time it took.
     * Changes the scene visuals accordingly.
     *
     * @param answerButton The answer button pressed.
     * @param answer       The answer corresponding to the answer button.
     */
    protected void onAnswerClicked(StackPane answerButton, Answer answer) {
        if (!answerButton.equals(selectedAnswerButton)) {
            currentQuestion.setUserAnswer(answer, getSeconds());

            selectedAnswerButton = answerButton;
            resetAnswerColors();
            answerButton.setBackground(new Background(
                    new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));

            for (StackPane answerBtnLoop : answerButtons) {
                answerBtnLoop.setStyle("-fx-border-width: 1; -fx-border-color: black");
                answerBtnLoop.getChildren().get(0).setStyle("-fx-font-weight: normal");
            }
            answerButton.getChildren().get(0).setStyle("-fx-font-weight: bold");
            answerButton.setStyle("-fx-border-width: 2; -fx-border-color: black");
        }
    }

    /**
     * The method called when the button answerTop is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the top button.
     */
    @FXML
    protected void onAnswerTopClicked() {
        onAnswerClicked(answerTop, answerTopAnswer);
    }

    /**
     * The method called when the button answerMid is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the middle button.
     */
    @FXML
    protected void onAnswerMidClicked() {
        onAnswerClicked(answerMid, answerMidAnswer);
    }

    /**
     * The method called when the button answerBot is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the bottom button.
     */
    @FXML
    protected void onAnswerBotClicked() {
        onAnswerClicked(answerBot, answerBotAnswer);
    }

    /**
     * The method called when the cursor enters the button answerTop.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerTop() {
        enterAnswer(answerTop);
    }

    /**
     * The method called when the cursor enters the button answerMid.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerMid() {
        enterAnswer(answerMid);
    }

    /**
     * The method called when the cursor enters the button answerBot.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerBot() {
        enterAnswer(answerBot);
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

    /**
     * Captures the exact time the question page started showing used for measuring the time
     * players needed for answering the question.
     */
    protected void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     * A general method for setting an answer button's background color upon the cursor enters it,
     * according to whether it is selected.
     *
     * @param answerBtn The answer button to recolor
     */
    protected void enterAnswer(StackPane answerBtn) {
        if (answerBtn.equals(selectedAnswerButton)) {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the answer buttons.
     * Resets all answer boxes' background color according to whether they are selected.
     */
    @FXML
    protected void resetAnswerColors() {
        for (StackPane answerBtn : answerButtons) {
            resetAnswerColors(answerBtn);
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves the given answer button .
     * Resets the given answer box's background color according to whether they are selected.
     * @param answerBtn The answer button to be recolored.
     */
    @FXML
    public void resetAnswerColors(StackPane answerBtn) {
        if (answerBtn.equals(selectedAnswerButton)) {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            answerBtn.setStyle("-fx-border-width: 1; -fx-border-color: black");
            answerBtn.getChildren().get(0).setStyle("-fx-font-weight: normal");
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
     * @param questionNum the number of the current question
     */
    protected void highlightCurrentCircle(int questionNum) {
        Circle c = (Circle) circles.getChildren().get(questionNum);
        c.setFill(Color.DARKGRAY);
        c.setStrokeWidth(THICK_CIRCLE_BORDER_SIZE);
    }

    /**
     * Resets the highlighting of the circle borders
     */
    public void resetHighlight() {
        for (int i = 0; i < circles.getChildren().size(); i++) {
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setStrokeWidth(STANDARD_CIRCLE_BORDER_SIZE);
        }
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

}

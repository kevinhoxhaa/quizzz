package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Answer;
import commons.models.EstimationQuestion;
import commons.models.SoloGame;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class SoloEstimationQuestionCtrl implements SceneController, QuestionNumController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private EstimationQuestion currentQuestion;
    private double startTime;
    private SoloGame game;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private Text currentScore;

    @FXML
    private HBox circles;

    @FXML
    private Text activityText;
    @FXML
    private Text questionNum;
    @FXML
    private ImageView questionImg;

    @FXML
    private TextField userInput;
    @FXML
    private Label yourAnswer;

    private static final double MILLISECONDS_PER_SECONDS = 1000.0;
    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;



    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloEstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Prepares the scene for being shown:
     *  - updates the question counter and the circles
     *  - updates the question text
     *  - resets input field to empty
     * @param soloGame the solo game instance
     * @param colors the array of colors for the circles
     */
    protected void setup(SoloGame soloGame, List<Color> colors){
        this.game = soloGame;
        currentScore.setText(String.format( "Score: %d", mainCtrl.getSoloScore()));
        currentQuestion = (EstimationQuestion) soloGame.loadCurrentQuestion();

        updateCircleColor(colors);
        resetHighlight();
        highlightCurrentCircle();
        updateQuestionNumber();

        activityText.setText(
                String.format("How many Wh's does %s consume?", currentQuestion.getActivity().title)
        );
        yourAnswer.setText("Your answer:");
        userInput.setText("");
    }

    /**
     * Saves the user input and updates the screen accordingly
     */
    @FXML
    private void onSubmit(){
        try{
            long userAnswerLong = Long.parseLong(userInput.getText());
            Answer answer = new Answer(userAnswerLong);
            currentQuestion.setUserAnswer(answer, getSeconds());
            yourAnswer.setText(String.format("Your answer: %d", userAnswerLong));
        }
        catch (NumberFormatException e){
            //TODO notify user of incorrect format
        }
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Returns the time since the timer started, in seconds.
     * For now, a placeholder method.
     * @return the time since the timer started, in seconds.
     */
    private double getSeconds() {
        return (System.currentTimeMillis() - startTime)/MILLISECONDS_PER_SECONDS;
    }


    /**
     * Captures the exact time the question page started showing used for measuring the time
     * players needed for answering the question.
     */
    protected void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Called by the timer running out in MainCtrl. Redirects to the answer page.
     */
    @Override
    public void redirect() {
        mainCtrl.showSoloAnswerPage(game);
    }

    /**
     * Getter for the circles bar
     * @return circles
     */
    public HBox getCirclesHBox(){
        return circles;
    }

    /**
     * Getter for the text node containing the current question number
     * @return questionNum
     */
    public Text getQuestionNum(){
        return questionNum;
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle c = (Circle) circles.getChildren().get(game.getCurrentQuestionNum());
        c.setFill(Color.DARKGRAY);
        c.setStrokeWidth(CIRCLE_BORDER_SIZE);
    }

    /**
     * Resets the highlighting of the circle borders
     */
    public void resetHighlight(){
        for(int i=0;i<circles.getChildren().size();i++){
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setStrokeWidth(STANDARD_CIRCLE_BORDER_SIZE);
        }
    }

    /**
     * Updates the colors of the little circles based on the array given
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
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Updates the question number on the top of the screen.
     */
    @Override
    public void updateQuestionNumber(){
        getQuestionNum().setText("" + (game.getCurrentQuestionNum()+ 1));
    }

    /**
     * Handles the user clicking the quit button.
     */
    @Override
    @FXML
    public void onQuit(){
        mainCtrl.killThread();
        mainCtrl.showHome();
    }
}

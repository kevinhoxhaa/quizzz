package client.scenes;

import client.utils.ServerUtils;
import commons.models.Answer;
import commons.models.EstimationQuestion;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public abstract class AbstractEstimationQuestionCtrl implements SceneController, QuestionNumController {
    protected final ServerUtils server;
    protected final MainCtrl mainCtrl;

    protected EstimationQuestion currentQuestion;
    protected double startTime;

    @FXML
    protected Text activityText;
    @FXML
    protected Text questionNum;
    @FXML
    protected ImageView questionImg;

    @FXML
    protected Text currentScore;
    @FXML
    protected HBox circles;

    @FXML
    protected ProgressIndicator countdownCircle;

    @FXML
    protected TextField userInput;
    @FXML
    protected Text yourAnswer;

    protected static final double MILLISECONDS_PER_SECONDS = 1000.0;
    protected static final double THICK_CIRCLE_BORDER_SIZE = 1.7;
    protected static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     *
     * @param server
     * @param mainCtrl
     */
    protected AbstractEstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    protected void setup(long points){
        currentScore.setText(String.valueOf(points));
        activityText.setText(
                String.format("How many Wh's does %s consume?", currentQuestion.getActivity().title)
        );

        yourAnswer.setText("");
        userInput.setText("");

        try {
            questionImg.setImage(server.fetchImage(mainCtrl.getServerUrl(), currentQuestion.getImagePath()));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Saves the user input and updates the screen accordingly
     */
    @FXML
    protected void onSubmit(){
        try {
            long answer = Long.parseLong(userInput.getText());
            currentQuestion.setUserAnswer(new Answer(answer), getSeconds());
            yourAnswer.setText(String.valueOf(answer));
        } catch(NumberFormatException ex) {
            System.out.println("Enter a number!");
        }
    }

    /**
     * Returns the time since the timer started, in seconds.
     * For now, a placeholder method.
     *
     * @return the time since the timer started, in seconds.
     */
    protected double getSeconds() {
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
     * Getter for the circles bar
     * @return circles
     */
    public HBox getCirclesHBox(){
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

}

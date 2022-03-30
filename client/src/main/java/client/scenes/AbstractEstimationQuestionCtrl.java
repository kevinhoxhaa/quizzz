package client.scenes;

import client.utils.ServerUtils;
import commons.models.Answer;
import commons.models.EstimationQuestion;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

public abstract class AbstractEstimationQuestionCtrl extends QuestionNumController implements SceneController{
    protected final ServerUtils server;

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
    protected ProgressIndicator countdownCircle;

    @FXML
    protected TextField userInput;
    @FXML
    protected Text yourAnswer;

    protected static final double MILLISECONDS_PER_SECONDS = 1000.0;



    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     *
     * @param server
     * @param mainCtrl
     */
    protected AbstractEstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(mainCtrl);
        this.server = server;
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
     * Resets the colors of the little circles to gray.
     */
    @Override
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

}

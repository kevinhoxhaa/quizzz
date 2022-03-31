package client.scenes;

import client.utils.ServerUtils;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;

public abstract class AbstractQuestionCtrl extends QuestionNumController {
    protected final ServerUtils server;

    protected double startTime;

    @FXML
    protected ProgressIndicator countdownCircle;

    @FXML
    protected Text currentScore;

    @FXML
    protected Text questionText;
    @FXML
    protected Text questionNum;
    @FXML
    protected ImageView questionImg;

    protected static final int KICK_AT_X_QUESTIONS = 3;

    /**
     * A constructor for question controllers
     * @param mainCtrl
     * @param server
     */
    protected AbstractQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(mainCtrl);
        this.server = server;
    }

    /**
     * Sets up the question scene with the given question and number of points.
     * @param question the question
     * @param points the number of points
     */
    protected void setup(Question question, long points){
        currentScore.setText(String.valueOf(points));
        questionText.setText(question.generateQuestionText());

        try {
            questionImg.setImage(server.fetchImage(mainCtrl.getServerUrl(), question.getImagePath()));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the time since the timer started, in seconds.
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
}

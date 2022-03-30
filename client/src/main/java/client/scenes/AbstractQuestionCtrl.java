package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Answer;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.List;

public abstract class AbstractQuestionCtrl {
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
    protected AbstractQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}

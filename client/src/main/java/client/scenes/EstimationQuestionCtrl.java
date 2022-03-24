package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.Answer;
import commons.models.EstimationQuestion;
import commons.models.Question;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class EstimationQuestionCtrl implements SceneController, QuestionNumController {

    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;
    private static final double TIMEOUT = 8.0;
    private static final int KICK_AT_X_QUESTIONS = 3;

    private static final double MILLISECONDS_PER_SECONDS = 1000.0;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private MultiplayerGameCtrl gameCtrl;

    private Question currentQuestion;
    private double startTime;

    @FXML
    private Label yourAnswer;

    @FXML
    private Text questionNum;

    @FXML
    private HBox circles;

    @FXML
    private Text currentScore;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private Text questionDescription;

    @FXML
    private TextField answerField;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    public void setup(EstimationQuestion question) {
        currentScore.setText("Score: " + gameCtrl.getUser().points);
        currentQuestion = question;
        questionDescription.setText("How much energy in Wh does " + question.getActivity().title + " use?");
    }

    /**
     * Captures the exact time the question page started showing used for measuring the time
     * players needed for answering the question.
     */
    protected void setStartTime() {
        startTime = System.currentTimeMillis();
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

    @FXML
    protected void onAnswerPostClick() {
        gameCtrl.setAnsweredQuestion( true );
        try {
            long answer = Long.parseLong(answerField.getText());
            currentQuestion.setUserAnswer(new Answer(answer), getSeconds());
            yourAnswer.setText("Your answer: " + answer);
        } catch(NumberFormatException ex) {
            System.out.println("Enter a number!");
        }
    }

    @Override
    public void redirect() {
        MultiplayerUser user = gameCtrl.getUser();
        if ( !gameCtrl.getAnsweredQuestion() ) {
            user.unansweredQuestions++;
            if ( user.unansweredQuestions == KICK_AT_X_QUESTIONS ) {
                try {
                    server.removeMultiplayerUser(server.getURL(), user);
                    user = null;
                } catch(WebApplicationException e) {
                    System.out.println("User to remove not found!");
                }
                mainCtrl.killThread();
                mainCtrl.showHome();
                mainCtrl.bindUser( null );
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle ( "Kicked :(" );
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("You've been kicked for not answering 3 question in a row!");
                ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll( okButton );
                alert.showAndWait().ifPresent(type -> {
                    if ( type == okButton ) {
                        mainCtrl.killThread();
                    }
                });
            }
        } else {
            user.unansweredQuestions = 0;
        }

        gameCtrl.setAnsweredQuestion( false );
        try {
            if (currentQuestion.getUserAnswer().getLongAnswer().equals(-1L)) {
                long answer = Long.parseLong(answerField.getText());
                currentQuestion.setUserAnswer(new Answer(answer), TIMEOUT);
            }
        } catch(NumberFormatException ex) {
            System.out.println("Enter a number!");
        }

        answerField.setText("");
        yourAnswer.setText("Your answer: ");
        gameCtrl.postAnswer(currentQuestion);
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
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
     */
    public void highlightCurrentCircle() {
        Circle circle = (Circle) circles.getChildren().get(mainCtrl.getAnswerCount());
        circle.setFill(Color.DARKGRAY);
        circle.setStrokeWidth(CIRCLE_BORDER_SIZE);
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

    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < mainCtrl.getAnswerCount(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(colors.get(i));
        }
    }

    @Override
    public void resetCircleColor() {
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (mainCtrl.getAnswerCount() + 1));
    }
}

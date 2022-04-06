package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Question;
import commons.models.SoloGame;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;


public class SoloQuestionCtrl extends AbstractMultichoiceQuestionCtrl {

    private SoloGame game;

    @FXML
    private Button quitButton;

    /**
     * Creates a controller for the solo question screen, with the given server and main controller.
     * Creates the list answerButtons for iterating through all of these.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Sets up the question page scene: <br>
     * - Sets up the question/answers according to the type of the question given <br>
     * - Fills the answerButtons list for iterations <br>
     * - Resets all buttons to their default colors
     *
     * @param soloGame the game instance
     */
    public void setup(SoloGame soloGame) {
        this.game = soloGame;
        Question question = soloGame.loadCurrentQuestion();
        super.setup(question, mainCtrl.getSoloScore());
    }


    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Called by the timer running out in MainCtrl. Redirects to the answer page.
     */
    @Override
    public void redirect() {
        mainCtrl.showSoloAnswerPage(game);
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        super.highlightCurrentCircle(game.getCurrentQuestionNum());
    }

    /**
     * Updates the question number at the top of the screen.
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (game.getCurrentQuestionNum() + 1));
    }

    /**
     * Handles the user clicking the quit button.
     */
    @Override
    @FXML
    public void onQuit(){
        mainCtrl.quitGame(false, false);
    }

    /**
     * Sets up the buttons hover cursors
     */
    @Override
    public void setupHoverCursor() {
        answerTop.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        answerMid.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        answerBot.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        quitButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
    }
}

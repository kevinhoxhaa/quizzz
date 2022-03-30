package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.EstimationQuestion;
import commons.models.SoloGame;
import javafx.fxml.FXML;

public class SoloEstimationQuestionCtrl extends AbstractEstimationQuestionCtrl{
    private SoloGame game;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloEstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Prepares the scene for being shown:
     *  - updates the question counter and the circles
     *  - updates the question text
     *  - resets input field to empty
     * @param soloGame the solo game instance
     */
    protected void setup(SoloGame soloGame){
        this.game = soloGame;
        currentQuestion = (EstimationQuestion) soloGame.loadCurrentQuestion();

        super.setup(mainCtrl.getSoloScore());

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
    public void highlightCurrentCircle() {
        super.highlightCurrentCircle(game.getCurrentQuestionNum());
    }

    /**
     * Updates the question number on the top of the screen.
     */
    @Override
    public void updateQuestionNumber(){
        questionNum.setText("" + (game.getCurrentQuestionNum()+ 1));
    }

    /**
     * Handles the user clicking the quit button.
     */
    @Override
    @FXML
    public void onQuit(){
        mainCtrl.quitGame(false, false);
    }
}

package client.scenes;

import com.google.inject.Inject;
import commons.models.Question;
import commons.models.SoloGame;
import commons.utils.QuestionType;
import javafx.fxml.FXML;


public class SoloAnswerCtrl extends AbstractAnswerCtrl{
    private SoloGame game;

    /**
     * Creates a controller for the multiplayer answer screen, with the given server and main controller.
     *
     * @param mainCtrl
     */
    @Inject
    public SoloAnswerCtrl(MainCtrl mainCtrl) {
        super(mainCtrl);
    }

    /**
     * Sets up the answer page screen: <br>
     * - Sets up a fitting message (with corresponding color) for the player
     * based on if the player answered correctly or not. <br>
     * - Fills in the question and correct answer in their corresponding text boxes. <br>
     *
     * @param soloGame The solo game instance
     */
    public void setup(SoloGame soloGame) {
        this.game = soloGame;
        Question prevQuestion = soloGame.loadCurrentQuestion();
        mainCtrl.getUser().incrementScore(prevQuestion.calculatePoints());

        if(prevQuestion.hasCorrectUserAnswer()){
            mainCtrl.addScore(prevQuestion.calculatePoints());
        }

        super.setup(prevQuestion, mainCtrl.getSoloScore());
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Redirects to:
     * - either the next question
     * - or the results page
     */
    @Override
    public void redirect() {
        if(game.incrementCurrentQuestionNum() < mainCtrl.getQuestionsPerGame()){
            if(game.loadCurrentQuestion().getType() == QuestionType.ESTIMATION){
                mainCtrl.showSoloEstimationQuestion(game);
            }
            else{
                mainCtrl.showSoloQuestion(game);
            }
        }
        else{
            mainCtrl.showSoloResults(game);
        }
    }


    /**
     * Updates the question number at the top of the screen.
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (game.getCurrentQuestionNum() + 1));
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(game.getCurrentQuestionNum());
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

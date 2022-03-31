package client.scenes;

import client.utils.ServerUtils;
import commons.models.Answer;
import commons.models.EstimationQuestion;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public abstract class AbstractEstimationQuestionCtrl extends AbstractQuestionCtrl{
    protected EstimationQuestion currentQuestion;

    @FXML
    protected TextField userInput;
    @FXML
    protected Text yourAnswer;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     *
     * @param server
     * @param mainCtrl
     */
    protected AbstractEstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Sets up the estimation scene: <br>
     *  - Sets the number of points <br>
     *  - Sets the question text <br>
     *  - Resets the user input field and the "your answer" text <br>
     *  - Sets the question image
     * @param points the player's points
     */
    protected void setup(long points){
        yourAnswer.setText("");
        userInput.setText("");

        super.setup(currentQuestion, points);
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



}

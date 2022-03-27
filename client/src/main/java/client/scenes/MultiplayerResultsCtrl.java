package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class MultiplayerResultsCtrl implements QuestionNumController{

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private MultiplayerGameCtrl gameCtrl;

    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;

    @FXML
    private Text scoreTableUserName;
    @FXML
    private Text scoreTableUserScore;

    @FXML
    private Text personalBest;

    @FXML
    private Button quitButton;

    @FXML
    private Button rematchButton;

    @FXML
    private Text questionNum;

    @FXML
    private HBox circles;

    /**
     * Creates a controller for the multiplayer results page screen, with the given server and mainCtrl parameters.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     */

    protected void setup() {

        scoreTableUserName.setText( String.format( "%s", mainCtrl.getUser().username) );
        // scoreTableUserScore.setText( String.format( "%d", mainCtrl.getSoloScore()) );
        //TODO: Show all players in the leaderboard.
    }

    /**
     * Indicates that the player wants (or doesn't want) to rematch the players from the last game.
     */
    @FXML
    protected void onRematchButton(){
        //TODO: Make a working rematch button.
    }
    /**
     * Redirects the user to the home page when the quit button is clicked.
     */
    @FXML
    protected void onQuitButton(){
        mainCtrl.bindUser(null);
        mainCtrl.killThread();
        mainCtrl.showHome();
    }

    /**
     * Getter for the current question number
     * @return questionNum
     */
    public Text getQuestionNum(){
        return questionNum;
    }

    /**
     * Getter for the circles bar
     * @return circles
     */
    public HBox getCirclesHBox(){
        return circles;
    }

    /**
     * Updates the color of the past questions' circles on the circle bar
     * (green/red depending on the correctness of the answer)
     *
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
     * Resets the circles colors every time the game starts
     */
    @Override
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Updates the number of the current question
     */
    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + mainCtrl.getAnswerCount());
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle c = (Circle) circles.getChildren().get(gameCtrl.getAnswerCount());
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
}

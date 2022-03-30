package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public abstract class QuestionNumController {
    protected final MainCtrl mainCtrl;

    protected QuestionNumController(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }

    @FXML
    protected HBox circles;
    @FXML
    protected Text questionNum;

    protected static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;
    protected static final double THICK_CIRCLE_BORDER_SIZE = 1.7;

    /**
     * Updates the color of the past questions' circles on the circle bar
     * (green/red depending on the correctness of the answer)
     *
     * @param colors Is the list of colors of previous answers(green/red depending on their correctness)
     */
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < colors.size(); i++) {
            Circle c = (Circle) circles.getChildren().get(i);
            c.setFill(colors.get(i));
        }
    }

    /**
     * Resets the circles colors every time the game starts
     */
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Updates the number of the current question (e.g 11/20)
     */
    public abstract void updateQuestionNumber();

    /**
     * Highlights the little circle corresponding to the current question
     */
    public abstract void highlightCurrentCircle();

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
     * Resets all little circles to a non-highlighted state
     */
    public void resetHighlight(){
        for(int i=0;i<circles.getChildren().size();i++){
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setStrokeWidth(STANDARD_CIRCLE_BORDER_SIZE);
        }
    }

    /**
     * Getter for the circles bar
     *
     * @return circles
     */
    public HBox getCircles() {
        return circles;
    }
}

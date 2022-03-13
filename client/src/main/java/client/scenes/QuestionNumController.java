package client.scenes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public interface QuestionNumController {

    /**
     * Updates the color of the past questions' circles on the circle bar
     * (green/red depending on the correctness of the answer)
     * @param colors Is the list of colors of previous answers(green/red depending on their correctness)
     */
    public void updateCircleColor(List<Color> colors);

    /**
     * Updates the number of the current question (e.g 11/20)
     */
    public void updateQuestionNumber();
}

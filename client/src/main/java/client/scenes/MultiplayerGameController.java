package client.scenes;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MultiplayerGameController {
    private Stage primaryStage;

    private Scene mcQuestion;
    private MultiplayerQuestionCtrl mcQuestionCtrl;

    private Scene estimationQuestion;
    private EstimationQuestionCtrl estimationQuestionCtrl;

    private Scene answer;
    private MultiplayerAnswerCtrl answerCtrl;

    private Scene ranking;
    private RankingCtrl rankingCtrl;

    // TODO: add results and resultsCtrl

    /**
     * Constructs a multiplayer game controller with the given
     * specific scenes and controllers
     * @param primaryStage the primary stage of the app
     * @param mcQuestion the mc question controller-scene pair
     * @param estimationQuestion the estimation question controller-scene pair
     * @param answer the answer controller-scene pair
     * @param ranking the ranking controller-scene pair
     */
    public MultiplayerGameController(Stage primaryStage, Pair<MultiplayerQuestionCtrl, Scene> mcQuestion,
                                     Pair<EstimationQuestionCtrl, Scene> estimationQuestion,
                                     Pair<MultiplayerAnswerCtrl, Scene> answer,
                                     Pair<RankingCtrl, Scene> ranking) {
        this.primaryStage = primaryStage;

        this.mcQuestionCtrl = mcQuestion.getKey();
        this.mcQuestion = mcQuestion.getValue();

        this.estimationQuestionCtrl = estimationQuestion.getKey();
        this.estimationQuestion = estimationQuestion.getValue();

        this.answerCtrl = answer.getKey();
        this.answer = answer.getValue();

        this.rankingCtrl = ranking.getKey();
        this.ranking = ranking.getValue();
    }

    /**
     * Returns the app primary stage
     * @return the app primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}

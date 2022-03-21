package client.scenes;

import client.utils.ServerUtils;
import commons.entities.MultiplayerUser;
import javafx.scene.Scene;
import javafx.util.Pair;

public class MultiplayerGameController {
    private MainCtrl mainCtrl;
    private ServerUtils server;

    private int gameIndex;
    private MultiplayerUser user;

    private String serverUrl;
    private int answerCount;

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
     * @param mainCtrl the app main controller
     * @param server the app server utils
     * @param mcQuestion the mc question controller-scene pair
     * @param estimationQuestion the estimation question controller-scene pair
     * @param answer the answer controller-scene pair
     * @param ranking the ranking controller-scene pair
     * @param gameIndex the index of the multiplayer game
     */
    public MultiplayerGameController(int gameIndex, MainCtrl mainCtrl, ServerUtils server,
                                     Pair<MultiplayerQuestionCtrl, Scene> mcQuestion,
                                     Pair<EstimationQuestionCtrl, Scene> estimationQuestion,
                                     Pair<MultiplayerAnswerCtrl, Scene> answer,
                                     Pair<RankingCtrl, Scene> ranking) {
        this.gameIndex = gameIndex;
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.user = (MultiplayerUser) mainCtrl.getUser();

        this.serverUrl = mainCtrl.getServerUrl();
        this.answerCount = 0;

        this.mcQuestionCtrl = mcQuestion.getKey();
        this.mcQuestion = mcQuestion.getValue();

        this.estimationQuestionCtrl = estimationQuestion.getKey();
        this.estimationQuestion = estimationQuestion.getValue();

        this.answerCtrl = answer.getKey();
        this.answer = answer.getValue();

        this.rankingCtrl = ranking.getKey();
        this.ranking = ranking.getValue();
    }
}

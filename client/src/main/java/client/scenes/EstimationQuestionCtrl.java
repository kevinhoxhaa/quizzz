package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Question;

public class EstimationQuestionCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Question currentQuestion;
    private double startTime;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}

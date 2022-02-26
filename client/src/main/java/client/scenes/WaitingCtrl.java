package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class WaitingCtrl {
    private ServerUtils server;
    private MainCtrl mainCtrl;

    /**
     * Creates a new waiting controller instance
     * @param server the server util object containing
     *               necessary REST API functionality
     * @param mainCtrl the main controller used for changing
     *                 scenes in the application
     */
    @Inject
    public WaitingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}

package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class WaitingCtrl {
    private ServerUtils server;
    private MainCtrl mainCtrl;

    @Inject
    public WaitingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}

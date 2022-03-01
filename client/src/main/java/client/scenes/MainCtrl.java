/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Timer;
import java.util.TimerTask;

public class MainCtrl {

    public static final double MIN_WIDTH = 768.0;
    public static final double MIN_HEIGHT = 512.0;
    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 1500;

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;
    
    private MultiplayerAnswerCtrl multiplayerAnswerCtrl;
    private Scene answerScene;

    private HomeCtrl homeCtrl;
    private Scene home;

    private MultiplayerQuestionCtrl multiplayerQuestionCtrl;
    private Scene questionScene;

    private WaitingCtrl waitingCtrl;
    private Scene waiting;

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
            Pair<AddQuoteCtrl, Parent> add, Pair<HomeCtrl, Parent> home, 
            Pair<WaitingCtrl, Parent> waiting, Pair<MultiplayerQuestionCtrl, Parent> question,
            Pair<MultiplayerAnswerCtrl, Parent> answerPage) {
        this.primaryStage = primaryStage;
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.multiplayerAnswerCtrl = answerPage.getKey();
        this.answerScene = new Scene(answerPage.getValue());
        
        this.homeCtrl = home.getKey();
        this.home = new Scene(home.getValue());

        this.multiplayerQuestionCtrl = question.getKey();
        this.questionScene = new Scene(question.getValue());

        this.waitingCtrl = waiting.getKey();
        this.waiting = new Scene(waiting.getValue());

        showHome();
        primaryStage.show();
    }

    /**
     * Shows the home page of the quiz application on the primary
     * stage
     */
    public void showHome() {
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(home);
    }
    /**
     * Displays the waiting page of the quiz application
     */
    public void showWaiting() {
        primaryStage.setTitle("Quizzz: Waiting");
        primaryStage.setScene(waiting);
        waitingCtrl.scaleButton();
        new Timer().schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        System.out.println("REFRESH");
                        Platform.runLater(() -> waitingCtrl.fetchUsers(homeCtrl.getServerUrl()));
                    }
                }, POLLING_DELAY, POLLING_INTERVAL);
    }

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }
    
    /**
     * Sets the multiplayer answer screen as the scene in the primary stage 
     * and gives the primary stage a corresponding title.
     */
    public void showAnswerPage() {
        primaryStage.setTitle("Answer screen");
        primaryStage.setScene(answerScene);
    }

    /**
     * Sets the scene in the primary stage to the one corresponding to a multiplayer question screen.
     */
    public void showQuestion() {
        primaryStage.setTitle("Question screen");
        primaryStage.setScene(questionScene);
    }
}
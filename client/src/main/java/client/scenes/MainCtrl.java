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

import client.utils.ServerUtils;
import commons.entities.Activity;
import commons.entities.User;
import commons.models.ConsumptionQuestion;
import commons.models.Question;
import commons.models.SoloGame;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class MainCtrl {

    private static final double TIMEOUT = 8.0;
    private static final double START_TIME = 7.95;
    private static final double INTERVAL = 0.05;
    private static final int MILLIS = 50;

    public static final double MIN_WIDTH = 768.0;
    public static final double MIN_HEIGHT = 512.0;
    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 1500;
    private static final long ANSWER_TO_THE_ULTIMATE_QUESTION = 42;
    private static final int STANDARD_PAGE_TIME = 15;
    private static final int QUESTIONS_PER_GAME = 20;

    private Stage primaryStage;

    private ServerUtils server;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private MultiplayerAnswerCtrl multiplayerAnswerCtrl;
    private Scene multiplayerAnswer;

    private HomeCtrl homeCtrl;
    private Scene home;

    private MultiplayerQuestionCtrl multiplayerQuestionCtrl;
    private Scene multiplayerQuestion;

    private WaitingCtrl waitingCtrl;
    private Scene waiting;

    private RankingCtrl rankingCtrl;
    private Scene ranking;

    private EstimationQuestionCtrl estimationQuestionCtrl;
    private Scene estimation;

    private SoloQuestionCtrl soloQuestionCtrl;
    private Scene soloQuestion;

    private SoloAnswerCtrl soloAnswerCtrl;
    private Scene soloAnswer;

    private SoloResultsCtrl soloResultsCtrl;
    private Scene soloResults;

    private User user;
    private List<Color> colors;

    private int answerCount = 0;
    private int soloScore = 0;
    private static final int TOTAL_ANSWERS = 20;
    private static final int HALFWAY_ANSWERS = 10;

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
            Pair<AddQuoteCtrl, Parent> add, Pair<HomeCtrl, Parent> home,
            Pair<WaitingCtrl, Parent> waiting, Pair<MultiplayerQuestionCtrl, Parent> multiplayerQuestion,
            Pair<MultiplayerAnswerCtrl, Parent> multiplayerAnswer, Pair<RankingCtrl, Parent> ranking,
            Pair<EstimationQuestionCtrl, Parent> estimation, Pair<SoloQuestionCtrl, Parent> soloQuestion,
                           Pair<SoloAnswerCtrl, Parent> soloAnswer, Pair<SoloResultsCtrl, Parent> soloResults) {
        this.primaryStage = primaryStage;
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.multiplayerAnswerCtrl = multiplayerAnswer.getKey();
        this.multiplayerAnswer = new Scene(multiplayerAnswer.getValue());

        this.homeCtrl = home.getKey();
        this.home = new Scene(home.getValue());

        this.server = homeCtrl.getServer();

        this.multiplayerQuestionCtrl = multiplayerQuestion.getKey();
        this.multiplayerQuestion = new Scene(multiplayerQuestion.getValue());

        this.waitingCtrl = waiting.getKey();
        this.waiting = new Scene(waiting.getValue());

        this.rankingCtrl = ranking.getKey();
        this.ranking = new Scene(ranking.getValue());

        this.estimationQuestionCtrl = estimation.getKey();
        this.estimation = new Scene(estimation.getValue());

        this.soloQuestionCtrl = soloQuestion.getKey();
        this.soloQuestion = new Scene(soloQuestion.getValue());

        this.soloAnswerCtrl = soloAnswer.getKey();
        this.soloAnswer = new Scene(soloAnswer.getValue());

        this.soloResultsCtrl = soloResults.getKey();
        this.soloResults = new Scene(soloResults.getValue());

        colors = new ArrayList<>();

        showHome();
        primaryStage.show();
    }

    /**
     * Binder for the User in the client side
     *
     * @param user
     */
    public void bindUser(User user) {
        this.user = user;
    }

    /**
     * Getter for the user
     *
     * @return user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Getter for the solo score points
     *
     * @return the score
     */

    public int getSoloScore() {
        return this.soloScore;
    }

    /**
     * add the score to the player's own score
     *
     * @param score the score to be added
     */

    public void addScore ( int score ) {
        this.soloScore += score;
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
     * Furthermore, it increments the answerCount and first sets up the answer page.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void showAnswerPage(Question prevQuestion) {
        multiplayerAnswerCtrl.updateQuestionNumber();
        //Adds the color of the answer correctness to a list of answers
        if (prevQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }
        answerCount++;
        multiplayerAnswerCtrl.updateCircleColor(colors);
        multiplayerAnswerCtrl.setup(prevQuestion, getCorrectPlayersMock());
        primaryStage.setTitle("Answer screen");
        primaryStage.setScene(multiplayerAnswer);
    }

    /**
     * Mock method to create a simple list of strings that should later be replaced by players that
     * answered correctly.
     *
     * @return A list of Strings that represent players that answered the previous question correctly.
     */
    public List<String> getCorrectPlayersMock() {
        //TODO: Instead get list from server.
        List<String> correctPlayers = new ArrayList<>();
        correctPlayers.add("Patrik");
        correctPlayers.add("Bink");
        correctPlayers.add("Boris");
        return correctPlayers;
    }

    /**
     * Sets the scene in the primary stage to the one corresponding to a multiplayer question screen.
     * Sets the timer to an initial 10 seconds for the players to answer the question.
     */
    public void showQuestion() {
        Question question = getNextQuestion();

        multiplayerQuestionCtrl.updateCircleColor(colors);
        multiplayerQuestionCtrl.resetHighlight();
        multiplayerQuestionCtrl.highlightCurrentCircle();
        multiplayerQuestionCtrl.setup(question);
        multiplayerQuestionCtrl.resetAnswerColors();
        multiplayerQuestionCtrl.updateQuestionNumber();
        multiplayerQuestionCtrl.startTimer();
        multiplayerQuestionCtrl.setStartTime();
        primaryStage.setTitle("Question screen");
        primaryStage.setScene(multiplayerQuestion);
    }

    /**
     * Sets the scene in the primary stage to the one corresponding to a ranking screen.
     */
    public void showRanking() {
        rankingCtrl.updateCircleColor(colors);
        rankingCtrl.updateQuestionNumber();
        primaryStage.setTitle("Ranking Screen");
        primaryStage.setScene(ranking);
        rankingCtrl.startTimer();
    }

    /**
     * Sets the scene in the primary stage to the estimation screen
     */
    public void showEstimation() {
        primaryStage.setTitle("Estimation");
        primaryStage.setScene(estimation);
        estimationQuestionCtrl.startTimer();
    }

    /**
     * A getter for the number of the current question
     *
     * @return questionCount, which is the count of the number of questions that have already been shown.
     */
    public int getAnswerCount() {
        return answerCount;
    }

    /**
     * Fetches a random question from the server. For now, it just returns a placeholder for testing.
     *
     * @return a random question
     */
    private Question getNextQuestion() {
        //TODO instead of this, return a random question fetched from the server
        Activity activity = new Activity(
                "testing the question models", ANSWER_TO_THE_ULTIMATE_QUESTION,
                "it was me. I said it. haha", "client/images/xd.png");
        return new ConsumptionQuestion(activity, new Random());
    }

    /**
     * Deletes user from database when the close button is clicked
     */
    public void onClose() {
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        homeCtrl.getServer().removeMultiplayerUser(homeCtrl.getServer().getURL(), user);
                        user = null;
                        System.exit(0);
                    }
                });
            }
        });
    }

    /**
     * A method that redirects the User to:
     * - The next question if the number of previous answers is less than 20 and not equal to 10
     * - The Ranking Page if the User is halfway through the game (10 answers so far)
     * - The Final Results Page if the User has answered all 20 questions
     */
    public void afterAnswerScreen() {
        if (getAnswerCount() <= TOTAL_ANSWERS) {
            if (getAnswerCount() == HALFWAY_ANSWERS) {
                showRanking();
                // The ranking page will be showed here
            }
            //If the User is not redirected to the ranking page, they go to the next Question
            else {
                multiplayerQuestionCtrl.resetAnswerColors();
                showQuestion();
            }
        } else {
//            showResultsPage();
            // Once the game is over, the results page should be shown
        }
    }

    /**
     * Starts a particular countdown timer and initiates the
     * timer animation
     *
     * @param countdownCircle the circle to perform the
     *                        animation on
     * @param sceneController the scene controller instance that will redirect to the next scene,
     *                        once the timer is up
     */
    public void startTimer(ProgressIndicator countdownCircle, SceneController sceneController) {
        countdownCircle.applyCss();
        Text text = (Text) countdownCircle.lookup(".text.percentage");
        new Thread(() -> {
            double countdown = START_TIME;
            while (countdown >= 0.0) {
                try {
                    double finalCountdown = countdown;
                    Platform.runLater(() -> {
                        countdownCircle.setProgress(finalCountdown / TIMEOUT);
                        if (text != null) {
                            text.setText(Math.round(finalCountdown) + "s");
                        }
                    });

                    Thread.sleep(MILLIS);
                    countdown -= INTERVAL;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(
                new Runnable(){
                    @Override
                    public void run() {
                        sceneController.redirect();
                        if(text != null) {
                            text.setText("Timeout");
                        }
                    }
            });
        }).start();
    }

    /**
     * Called once, initializes a solo game and shows the first question screen
     */
    public void startSoloGame() {
        SoloGame soloGame = server.getSoloGame(server.getURL(), QUESTIONS_PER_GAME);
        soloQuestionCtrl.setup(soloGame);
        primaryStage.setTitle("Solo game");
        primaryStage.setScene(soloQuestion);
        soloQuestionCtrl.startTimer();
    }

    /**
     * Shows the relevant answer screen for the given solo game instance
     * @param game the solo game instance
     */
    public void showSoloAnswerPage(SoloGame game) {
        soloAnswerCtrl.setup(game);
        primaryStage.setScene(soloAnswer);
        soloAnswerCtrl.startTimer();
    }

    /**
     * Shows the relevant question screen for the given solo game instance
     * @param game the solo game instance
     */
    public void showSoloQuestion(SoloGame game) {
        soloQuestionCtrl.setup(game);
        primaryStage.setScene(soloQuestion);
        soloQuestionCtrl.startTimer();
    }

    /**
     * THIS STILL NEEDS TO BE IMPLEMENTED
     * Called after the last answer screen's timer is up, shows the solo results page
     */
    public void showSoloResults() {
        //TODO
        soloResultsCtrl.setup();
        primaryStage.setScene(soloResults);
        primaryStage.setTitle("Solo results");
    }
}
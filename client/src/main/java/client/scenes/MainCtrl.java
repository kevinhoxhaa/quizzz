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
import commons.entities.MultiplayerUser;
import commons.entities.User;
import commons.models.Question;
import commons.models.SoloGame;
import commons.utils.QuestionType;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainCtrl {

    public static final double WIDTH = 1024.0;
    public static final double HEIGHT = 704.0;
    private static final double TIMEOUT = 8.0;
    private static final double START_TIME = 7.95;
    private static final double INTERVAL = 0.05;
    private static final int MILLIS = 50;
    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;
    private static final long ANSWER_TO_THE_ULTIMATE_QUESTION = 42;
    private static final int STANDARD_PAGE_TIME = 15;
    private static final int QUESTIONS_PER_GAME = 20;
    private static final int TOTAL_ANSWERS = 20;
    private static final int HALFWAY_ANSWERS = 10;

    private String serverUrl;
    private Timer waitingTimer;
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

    private EstimationQuestionCtrl multiplayerEstimationCtrl;
    private Scene multiplayerEstimation;

    private SoloEstimationQuestionCtrl soloEstimationCtrl;
    private Scene soloEstimation;

    private SoloQuestionCtrl soloQuestionCtrl;
    private Scene soloQuestion;

    private SoloAnswerCtrl soloAnswerCtrl;
    private Scene soloAnswer;

    private SoloResultsCtrl soloResultsCtrl;
    private Scene soloResults;

    private MultiplayerGameCtrl multiplayerCtrl;

    private MultiplayerResultsCtrl multiplayerResultsCtrl;
    private Scene multiplayerResults;

    private User user;
    private int gameIndex;
    private List<Color> colors;
    private Thread timerThread;
    private int answerCount = 0;

    private long soloScore = 0;
    private int currentQuestion = 0;

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
            Pair<AddQuoteCtrl, Parent> add, Pair<HomeCtrl, Parent> home,
            Pair<WaitingCtrl, Parent> waiting, Pair<MultiplayerQuestionCtrl, Parent> multiplayerQuestion,
            Pair<MultiplayerAnswerCtrl, Parent> multiplayerAnswer, Pair<RankingCtrl, Parent> ranking,
            Pair<EstimationQuestionCtrl, Parent> multiplayerEstimation,
                           Pair<SoloEstimationQuestionCtrl, Parent> soloEstimation,
                           Pair<SoloQuestionCtrl, Parent> soloQuestion,
                           Pair<SoloAnswerCtrl, Parent> soloAnswer,
                           Pair<SoloResultsCtrl, Parent> soloResults,
                           Pair<MultiplayerResultsCtrl, Parent> multiplayerResults
                           ) {
        this.primaryStage = primaryStage;
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMaxHeight(HEIGHT);
        primaryStage.setMaxWidth(WIDTH);
        primaryStage.setResizable(false);

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

        this.multiplayerEstimationCtrl = multiplayerEstimation.getKey();
        this.multiplayerEstimation = new Scene(multiplayerEstimation.getValue());

        this.soloEstimationCtrl = soloEstimation.getKey();
        this.soloEstimation = new Scene(soloEstimation.getValue());

        this.soloQuestionCtrl = soloQuestion.getKey();
        this.soloQuestion = new Scene(soloQuestion.getValue());

        this.soloAnswerCtrl = soloAnswer.getKey();
        this.soloAnswer = new Scene(soloAnswer.getValue());

        this.soloResultsCtrl = soloResults.getKey();
        this.soloResults = new Scene(soloResults.getValue());

        this.multiplayerResultsCtrl = multiplayerResults.getKey();
        this.multiplayerResults = new Scene(multiplayerResults.getValue());

        showHome();
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {

            quitGame(true, true);

            event.consume();
        });
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
     * Returns the current game index
     *
     * @return the current game index
     */
    public int getGameIndex() {
        return gameIndex;
    }

    /**
     * Sets the index of the multiplayer game a user participates in
     *
     * @param gameIndex the multiplayer game index
     */
    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
    }

    /**
     * Returns the app primary stage
     * @return the app primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * add the score to the player's own score
     * @param score the player score
     */
    public void addScore(long score) {
        this.soloScore += score;
    }

    /**
     * Returns the solo game points
     * @return the solo game points
     */
    public long getSoloScore() {
        return getUser().getPoints();
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
     * Resets the colorList and the answerCount to 0 every time someone enters the waiting room.
     */
    public void showWaiting() {
        primaryStage.setTitle("Quizzz: Waiting");
        primaryStage.setScene(waiting);

        colors = new ArrayList<>();
        answerCount = 0;
        multiplayerQuestionCtrl.resetCircleColor();
        multiplayerAnswerCtrl.resetCircleColor();
        rankingCtrl.resetCircleColor();

        waitingCtrl.scaleButton();
        waitingTimer = new Timer();
        waitingTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        Platform.runLater(() -> waitingCtrl.fetchUsers());
                    }
                }, POLLING_DELAY, POLLING_INTERVAL);
    }

    /**
     * Initialises the multiplayer game controller and starts
     * a multiplayer game
     * @param gameIndex the index of the multiplayer game
     */
    public void startMultiplayerGame(int gameIndex) {
        multiplayerCtrl = new MultiplayerGameCtrl(
                gameIndex, this, server,
                new Pair<>(this.multiplayerQuestionCtrl, this.multiplayerQuestion),
                new Pair<>(this.multiplayerEstimationCtrl, this.multiplayerEstimation),
                new Pair<>(this.multiplayerAnswerCtrl, this.multiplayerAnswer),
                new Pair<>(this.rankingCtrl, this.ranking)
        );
        multiplayerCtrl.startGame();
    }

    /**
     * Stops the waiting room timer for continuous
     * user polling
     */
    public void stopWaitingTimer() {
        waitingTimer.cancel();
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
     * @param correctUsers the users that have answered correctly
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void showAnswerPage(Question prevQuestion, List<MultiplayerUser> correctUsers) {
        updateQuestionCounters(multiplayerAnswerCtrl, colors);
        //Adds the color of the answer correctness to a list of answers
        if (prevQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }
        answerCount++;
        multiplayerAnswerCtrl.setup(prevQuestion, correctUsers);
        primaryStage.setTitle("Answer screen");
        primaryStage.setScene(multiplayerAnswer);
    }

    /**
     * Mock method to create a simple list of strings that should later be replaced by players that
     * answered correctly.
     *
     * @return A list of Strings that represent players that answered the previous question correctly.
     */
    public List<MultiplayerUser> getCorrectPlayersMock() {
        List<MultiplayerUser> correctPlayers = new ArrayList<>();
        correctPlayers.add(new MultiplayerUser("Patrik"));
        correctPlayers.add(new MultiplayerUser("Bink"));
        correctPlayers.add(new MultiplayerUser("Boris"));
        return correctPlayers;
    }

    /**
     * Sets the scene in the primary stage to the one corresponding to a ranking screen.
     */
    public void showRanking() {
        updateQuestionCounters(rankingCtrl, colors);
        primaryStage.setTitle("Ranking Screen");
        primaryStage.setScene(ranking);
        rankingCtrl.startTimer();
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
    public Question getNextQuestion() {
        return server.getQuestion(serverUrl, gameIndex, answerCount);
    }


    /**
     * Starts a particular countdown timer and initiates the
     * timer animation with a new thread
     *
     * @param countdownCircle the circle to perform the
     *                        animation on
     * @param sceneController the scene controller instance that will redirect to the next scene,
     *                        once the timer is up
     */
    public void startTimer(ProgressIndicator countdownCircle, SceneController sceneController) {
        countdownCircle.applyCss();
        Text text = (Text) countdownCircle.lookup(".text.percentage");
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
        timerThread = new Thread(() -> {
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
//                    e.printStackTrace();
                    //This kills the current running thread
                    return;
                }
            }
            Platform.runLater(
                    () -> {
                        sceneController.redirect();
                        if (text != null) {
                            text.setText("Timeout");
                        }
                    });
        });
        timerThread.start();
    }

    /**
     * Kills the thread that is running the timer
     */
    public void killThread() {
        timerThread.interrupt();
    }

    /**
     * Called once, initializes a solo game and shows the first question screen
     * Resets the state of the solo game
     */
    public void startSoloGame() {
        answerCount = 0;
        getUser().resetScore();
        colors = new ArrayList<>();

        soloQuestionCtrl.resetCircleColor();
        soloAnswerCtrl.resetCircleColor();

        SoloGame soloGame = server.getSoloGame(server.getURL(), QUESTIONS_PER_GAME);
        primaryStage.setTitle("Solo game");

        if(soloGame.loadCurrentQuestion().getType() == QuestionType.ESTIMATION){
            showSoloEstimationQuestion(soloGame);
        }
        else {
            showSoloQuestion(soloGame);
        }
    }

    /**
     * Shows the relevant answer screen for the given solo game instance
     *
     * @param game the solo game instance
     */
    public void showSoloAnswerPage(SoloGame game) {
        Question prevQuestion = game.loadCurrentQuestion();
        if (prevQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }
        soloAnswerCtrl.setup(game);
        updateQuestionCounters(soloAnswerCtrl, colors);
        primaryStage.setScene(soloAnswer);
        soloAnswerCtrl.startTimer();
    }

    /**
     * Getter for the number of questions per game
     *
     * @return QUESTIONS_PER_GAME
     */
    public int getQuestionsPerGame() {
        return QUESTIONS_PER_GAME;
    }

    /**
     * Shows the relevant question screen for the given solo game instance
     *
     * @param game the solo game instance
     */
    public void showSoloQuestion(SoloGame game) {
        soloQuestionCtrl.setup(game);
        updateQuestionCounters(soloQuestionCtrl, colors);
        primaryStage.setScene(soloQuestion);
        soloQuestionCtrl.startTimer();
        soloQuestionCtrl.setStartTime();
    }

    /**
     * Shows the solo estimation question page relevant to the given solo game
     * @param game the solo game instance
     */
    public void showSoloEstimationQuestion(SoloGame game) {
        soloEstimationCtrl.setup(game);
        updateQuestionCounters(soloEstimationCtrl, colors);
        primaryStage.setScene(soloEstimation);
        soloEstimationCtrl.startTimer();
        soloEstimationCtrl.setStartTime();
    }

    /**
     * Returns the server URL the application makes requests
     * to
     *
     * @return the app server URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Sets the server URL for the application
     *
     * @param serverUrl the new server URL
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * THIS STILL NEEDS TO BE IMPLEMENTED
     * Called after the last answer screen's timer is up, shows the solo results page
     *
     * @param game
     */
    public void showSoloResults(SoloGame game) {
        soloResultsCtrl.setup(game);
        updateQuestionCounters(soloResultsCtrl, colors);
        primaryStage.setScene(soloResults);
    }

    /**
     * Called after the last answer screen's timer is up, shows the solo results page
     */
    public void showMultiplayerResults() {
        multiplayerResultsCtrl.setup();
        updateQuestionCounters(multiplayerResultsCtrl, colors);
        primaryStage.setTitle("Multiplayer results screen");
        primaryStage.setScene(multiplayerResults);
    }

    /**
     * Shows a pop up on screen to confirm quitting the game
     * @param quitApp is used to decide whether the application should be closed or not
     *                  If quitApp is true: the application is closed
     *                  If quitApp is false: the user is redirected to home page
     * @param isMultiplayer is used to decide whether the user is a multiplayer user
     *                      If so, it deletes the user from the database.
     */
    public void quitGame(boolean quitApp, boolean isMultiplayer){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit solo game");
        alert.setContentText("Are you sure you want to quit?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                if(isMultiplayer) {
                    if(multiplayerCtrl != null) {
                        multiplayerCtrl.unregisterForEmojis();
                        multiplayerCtrl.unregisterForDialog();
                        multiplayerCtrl.hideEmojis();
                    }

                    if(server.getSession() != null && server.getSession().isConnected()) {
                        server.getSession().disconnect();
                    }

                    try {
                        server.removeMultiplayerUser(serverUrl, user);
                        user = null;
                        multiplayerEstimationCtrl.resetDoublePoints();
                        multiplayerQuestionCtrl.resetDoublePoints();
                        multiplayerQuestionCtrl.resetRemoveIncorrect();

                    } catch(WebApplicationException e) {
                        System.out.println("User to remove not found!");
                    } finally {
                        if(quitApp) {
                            System.exit(0);
                        }
                    }
                }
                killThread();
                showHome();
            }
        });
    }

    /**
     * Updates the little circles and the question counter in the given controller
     * @param controller the controller of the scene
     * @param colors the list of colors corresponding to answers to past questions
     */
    public void updateQuestionCounters(QuestionNumController controller, List<Color> colors){
        controller.resetCircleColor();
        controller.updateCircleColor(colors);
        controller.updateQuestionNumber();
        if(controller instanceof MultiplayerQuestionCtrl ||
                controller instanceof EstimationQuestionCtrl ||
                controller instanceof SoloQuestionCtrl ||
                controller instanceof SoloEstimationQuestionCtrl){
            controller.resetHighlight();
            controller.highlightCurrentCircle();
        }
    }
}
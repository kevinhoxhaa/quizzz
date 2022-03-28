package client.scenes;

import client.utils.ServerUtils;
import commons.entities.MultiplayerUser;
import commons.models.EstimationQuestion;
import commons.models.Question;
import commons.utils.QuestionType;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MultiplayerGameCtrl {
    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;

    private List<Color> colors;

    private Timer answerTimer;

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

    private Scene results;
    private MultiplayerResultsCtrl resultsCtrl;

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
     * @param results The results controller-scene pair.
     */
    public MultiplayerGameCtrl(int gameIndex, MainCtrl mainCtrl, ServerUtils server,
                               Pair<MultiplayerQuestionCtrl, Scene> mcQuestion,
                               Pair<EstimationQuestionCtrl, Scene> estimationQuestion,
                               Pair<MultiplayerAnswerCtrl, Scene> answer,
                               Pair<RankingCtrl, Scene> ranking,
                               Pair<MultiplayerResultsCtrl, Scene> results) {
        this.gameIndex = gameIndex;
        this.mainCtrl = mainCtrl;

        this.server = server;
        this.user = (MultiplayerUser) mainCtrl.getUser();
        this.colors = new ArrayList<>();

        this.answerTimer = new Timer();

        this.serverUrl = mainCtrl.getServerUrl();
        this.answerCount = 0;

        this.mcQuestionCtrl = mcQuestion.getKey();
        mcQuestionCtrl.setGameCtrl(this);
        this.mcQuestion = mcQuestion.getValue();

        this.estimationQuestionCtrl = estimationQuestion.getKey();
        estimationQuestionCtrl.setGameCtrl(this);
        this.estimationQuestion = estimationQuestion.getValue();

        this.answerCtrl = answer.getKey();
        answerCtrl.setGameCtrl(this);
        this.answer = answer.getValue();

        this.rankingCtrl = ranking.getKey();
        rankingCtrl.setGameCtrl(this);
        this.ranking = ranking.getValue();

        this.resultsCtrl = results.getKey();
        resultsCtrl.setGameCtrl(this);
        this.results = results.getValue();
    }

    /**
     * Polls the first question and initialises
     * the game loop
     */
    public void startGame() {
         user.gameID = (long) gameIndex;
         Question firstQuestion = fetchQuestion();
         showQuestion(firstQuestion);
    }

    /**
     * Polls the next question from the server using the
     * answers counter
     * @return the next question
     */
    public Question fetchQuestion() {
        return server.getQuestion(serverUrl, gameIndex, answerCount);
    }

    /**
     * Posts an answered question to the server, updates the
     * answer colours and redirects to the answer screen
     * @param answeredQuestion the answered question to post
     */
    public void postAnswer(Question answeredQuestion) {
        user.points += answeredQuestion.calculatePoints();
        answerTimer = new Timer();
        answerTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            List<MultiplayerUser> correctUsers = fetchCorrectUsers(answeredQuestion);

                            Platform.runLater(() -> {
                                showAnswer(answeredQuestion, correctUsers);
                            });
                            answerTimer.cancel();
                        } catch (WebApplicationException e) {
                            System.out.println(e.getResponse());
                            // Not all users have answered
                        }
                    }
                }, POLLING_DELAY, POLLING_INTERVAL);
    }

    /**
     * Returns the correct users to an answered question
     * @param answeredQuestion the answered question
     * @return the correctly answered users
     * @throws WebApplicationException if the question has not been
     * answered by everyone
     */
    public List<MultiplayerUser> fetchCorrectUsers(Question answeredQuestion) throws WebApplicationException {
        return server.answerQuestion(serverUrl, gameIndex,
                mainCtrl.getUser().id, answerCount, answeredQuestion);
    }

    /**
     * Shows the answer screen displaying an answered question
     * and the users that have correctly answered that question
     * @param answeredQuestion the answered question
     * @param correctUsers the correct users list
     */
    public void showAnswer(Question answeredQuestion, List<MultiplayerUser> correctUsers) {
        answerCtrl.updateQuestionNumber();
        answerCount++;

        if (answeredQuestion.hasCorrectUserAnswer()) {
            colors.add(Color.LIGHTGREEN);
        } else {
            colors.add(Color.INDIANRED);
        }

        answerCtrl.updateCircleColor(colors);
        answerCtrl.setup(answeredQuestion, correctUsers);
        mainCtrl.getPrimaryStage().setTitle("Answer screen");
        mainCtrl.getPrimaryStage().setScene(answer);
    }

    /**
     * Visualises a question on one of the question
     * screens according to the question type
     * @param question the question to visualise
     */
    public void showQuestion(Question question) {
        System.out.println("Received question answer: " + question.getUserAnswer().generateAnswer());
        if (question.getType() == QuestionType.ESTIMATION) {
            showEstimationQuestion((EstimationQuestion) question);
            return;
        }

        showMultipleChoiceQuestion(question);
    }

    /**
     * Sets the scene in the primary stage to the multiple choice
     * question screen
     * @param question the question to visualise
     */
    public void showMultipleChoiceQuestion(Question question) {
        mcQuestionCtrl.updateCircleColor(colors);
        mcQuestionCtrl.resetHighlight();
        mcQuestionCtrl.highlightCurrentCircle();
        mcQuestionCtrl.setup(question);
        mcQuestionCtrl.resetAnswerColors();
        mcQuestionCtrl.updateQuestionNumber();

        mcQuestionCtrl.resetAnswerColors();
        mcQuestionCtrl.enableAnswers();
        mcQuestionCtrl.startTimer();
        mcQuestionCtrl.setStartTime();
        mainCtrl.getPrimaryStage().setTitle("Question screen");
        mainCtrl.getPrimaryStage().setScene(mcQuestion);
    }

    /**
     * Sets the scene in the primary stage to the estimation screen
     *
     * @param question the estimation question to visualise
     */
    public void showEstimationQuestion(EstimationQuestion question) {
        mainCtrl.getPrimaryStage().setTitle("Estimation");
        mainCtrl.getPrimaryStage().setScene(estimationQuestion);
        estimationQuestionCtrl.startTimer();
        estimationQuestionCtrl.setup(question);
    }

    /**
     * Polls the ranked users for the current game from
     * the server
     * @return the ranked users
     */
    public List<MultiplayerUser> fetchRanking() {
        // TODO: fetch users ranked by points from server
        return new ArrayList<>();
    }

    /**
     * Displays the list of ranked users on the ranking page
     * @param rankedUsers the list of ranked users to display
     */
    public void showRanking(List<MultiplayerUser> rankedUsers) {
        // TODO: handle passed multiplayer users
        rankingCtrl.updateCircleColor(colors);
        rankingCtrl.updateQuestionNumber();
        mainCtrl.getPrimaryStage().setTitle("Ranking Screen");
        mainCtrl.getPrimaryStage().setScene(ranking);
        rankingCtrl.startTimer();
    }

    /**
     * Displays the list of ranked users on the game
     * results page
     * @param rankedUsers the list of ranked users to display
     */
    public void showResults(List<MultiplayerUser> rankedUsers) {
        // TODO: display list of ranked users on results screen
        resultsCtrl.updateCircleColor(colors);
        resultsCtrl.updateQuestionNumber();
        mainCtrl.getPrimaryStage().setTitle("Results Screen");
        mainCtrl.getPrimaryStage().setScene(results);
        resultsCtrl.startTimer();
    }

    /**
     * Returns the current answer count
     * @return the current answer count
     */
    public int getAnswerCount() {
        return answerCount;
    }

    /**
     * Returns the current user
     * @return the current user
     */
    public MultiplayerUser getUser() {
        return user;
    }

    /**
     * Returns the current server url
     * @return the game server url
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Returns the index of the current game.
     * @return The current game index.
     */
    public int getGameIndex() {
        return gameIndex;
    }

    public void resetGameCtrl() {
        mainCtrl.resetMainCtrl();
        this.answerCount = 0;
        this.colors = new ArrayList<>();
        this.user.resetScore();
    }
}

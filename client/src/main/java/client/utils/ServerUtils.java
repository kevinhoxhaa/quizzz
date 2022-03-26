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
package client.utils;

import commons.entities.MultiplayerUser;
import commons.entities.Quote;
import commons.entities.SoloUser;
import commons.models.GameList;
import commons.models.Question;
import commons.models.SoloGame;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private static final long MAGICNUMBER = 42;
    private static final int QUESTIONS_PER_GAME = 20;

    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
    public String getURL(){
        return SERVER;
    }
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    public List<MultiplayerUser> getUsers(String serverUrl) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * Gets the games that are currently on the server.
     * @param serverUrl The server where the games should be fetched from.
     * @return A GameList object containing all the games on the server.
     */
    public GameList getGames(String serverUrl) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/games")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(GameList.class);
    }

    /**
     * Starts a game on the server and returns the index
     * of the game object
     * @param serverUrl the server to start a game on
     * @return the index of the game object
     */
    public Integer startGame(String serverUrl) {
        String path = String.format("/api/games/start/%d", QUESTIONS_PER_GAME);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Integer.class);
    }

    /**
     * Restarts the game on the server and returns the index
     * of the game object.
     * @param serverUrl The server to start a game on.
     * @param gameIndex The index of the current game.
     * @param userId The ID of the user that wants a rematch.
     * @return The first question of the new game.
     */
    public Question restartGame(String serverUrl, Integer gameIndex, Long userId) {
        String path = String.format("/api/games/restart/%d/%d/%d", gameIndex,  QUESTIONS_PER_GAME, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Question.class);
    }

    public MultiplayerUser addUserMultiplayer(String serverUrl, MultiplayerUser user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), MultiplayerUser.class);
    }

    /**
     * Returns the index of the game a user participates in
     * @param serverUrl the server URL
     * @param userId the user id
     * @return the index of the game
     */
    public Integer findGameIndex(String serverUrl, long userId) {
        String path = String.format("/api/games/find/%d", userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Integer.class);
    }

    /**
     * A getter for a given question
     * @param serverUrl the server url
     * @param gameIndex the game index
     * @param questionIndex the index of the question inside the game
     * @return a question
     */
    public Question getQuestion(String serverUrl, int gameIndex, int questionIndex) {
        String path = String.format("/api/games/%d/question/%d", gameIndex, questionIndex);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Question.class);
    }

    /**
     * Adds a user to the user repository for solo games.
     * @param serverUrl The server URL of the game the user is in.
     * @param user The user that has to be saved in the repository.
     * @return The user that has been saved in the repository.
     */
    public SoloUser addUserSolo(String serverUrl, SoloUser user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/solo")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), SoloUser.class);
    }

    /**
     * A method that removes a multiplayer user from the repository
     * @param serverUrl
     * @param user
     * @return the user that was removed
     */
    public MultiplayerUser removeMultiplayerUser(String serverUrl, MultiplayerUser user) {
        if (user.gameID != null) {
            removeMultiplayerUserID(serverUrl, (int) ((long) user.gameID), user.id);
        }
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/"+user.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(MultiplayerUser.class);
    }

    public List<MultiplayerUser> answerQuestion(String serverUrl, int gameIndex,
                                                long userId, int questionIndex, Question question) {
        String path = String.format(
                "api/games/%d/user/%d/question/%d",
                gameIndex,
                userId,
                questionIndex
        );

        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(question, APPLICATION_JSON), new GenericType<List<MultiplayerUser>>() {});
    }

    /**
     * Removes an ID from a player from the list of ID's of the users that are in a game.
     * @param serverUrl The URL of the server.
     * @param gameIndex The index of the game from where the user should be removed.
     * @param userId The ID of the user that should be removed.
     * @return A list with all ID's of the users that are still left in the game.
     */
    private List<Long> removeMultiplayerUserID(String serverUrl, int gameIndex, Long userId) {
        String path = String.format("/api/games/%d/%d", gameIndex, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(List.class);
    }

    /**
     * Removes an ID from a player from the list of ID's of the users that want to have a rematch.
     * @param serverUrl The URL of the server.
     * @param gameIndex The index of the game from where the user should be removed.
     * @param userId The ID of the user that should be removed.
     * @return A list with all ID's of the users that want to have a rematch.
     */
    public List<Long> removeRestartUserID(String serverUrl, int gameIndex, Long userId) {
        String path = String.format("/api/games/restart/%d/%d", gameIndex, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(List.class);
    }

    /**
     * Adds an ID from a player to the list of ID's of the users that want to have a rematch.
     * @param serverUrl The URL of the server.
     * @param gameIndex The index of the game to where the user should be added.
     * @param userId The ID of the user that should be added.
     * @return A list with all ID's of the users that want to have a rematch.
     */
    public List<Long> addRestartUserID(String serverUrl, int gameIndex, Long userId) {
        String path = String.format("/api/games/restart/%d/%d", gameIndex, userId);
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(userId, APPLICATION_JSON), List.class);
    }

    /**
     * Returns a new (solo) game instance with the given number of questions
     * @param serverUrl the server url
     * @param count the number of questions
     * @return a new (solo) game instance
     */
    public SoloGame getSoloGame(String serverUrl, int count) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/games/startSolo/" + count)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(SoloGame.class);
    }

    /**
     * Returns an arraylist of solo users with their corresponding scores in descending order
     * @param serverUrl
     * @return Arraylist of solo users
     */
    public List<SoloUser> getAllUsersByScore(String serverUrl){
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/solo/leaderboard")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<SoloUser>>() {});
    }
}
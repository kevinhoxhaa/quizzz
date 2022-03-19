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

import commons.entities.Activity;
import commons.entities.MultiplayerUser;
import commons.entities.Quote;
import commons.entities.SoloUser;
import commons.entities.User;
import commons.models.ConsumptionQuestion;
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
import java.util.Random;

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

    public MultiplayerUser addUserMultiplayer(String serverUrl, MultiplayerUser user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), MultiplayerUser.class);
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
    public MultiplayerUser removeMultiplayerUser(String serverUrl, User user) {
        MultiplayerUser mu = (MultiplayerUser) user;
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/users/"+mu.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(MultiplayerUser.class);
    }

    /**
     * Returns a new (solo) game instance with the given number of questions
     * @param serverUrl the server url
     * @param count the number of questions
     * @return a new (solo) game instance
     */
    public SoloGame getSoloGame(String serverUrl, int count) {
        /*
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverUrl).path("api/games/startSolo/" + count)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(SoloGame.class);
        */

        //THE FORMER PART WILL BE USED ONCE THE BACKEND IS SET UP PROPERLY, THE FOLLOWING PART IS A DUMMY
        SoloGame soloGame = new SoloGame();
        Activity activity = new Activity("starting a solo game on client side", MAGICNUMBER,
                "source", "client/images/angry.png");
        for (int i = 0; i < QUESTIONS_PER_GAME; i++) {
            soloGame.getQuestions().add(new ConsumptionQuestion(activity, new Random()));
        }
        return soloGame;
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
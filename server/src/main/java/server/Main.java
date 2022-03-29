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
package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import commons.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.util.ResourceUtils;
import server.database.ActivityRepository;
import server.database.GameUserRepository;
import server.database.WaitingUserRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main implements CommandLineRunner {

    @Autowired
    private ActivityRepository activityRepo;

    @Autowired
    private GameUserRepository gameRepo;

    @Autowired
    private WaitingUserRepository waitingRepo;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * Fills up the activities repository with activities from the resources/activities directory.
     * @param args
     * @throws IOException
     */
    @Override
    public void run(String[] args) throws IOException {
        gameRepo.deleteAll();
        waitingRepo.deleteAll();

        if(activityRepo.count() == 0){
            String content = "";
            try {
                content = Files.readString(
                        Path.of(
                                ResourceUtils.getFile("classpath:activities/activities.json").getPath()
                        ), StandardCharsets.US_ASCII);
            }
            catch (FileNotFoundException e){
                System.out.println("Please put some files in the activities directory!");
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(content);
            JsonArray array = element.getAsJsonArray();

            List<Activity> activities = new ArrayList<>();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                String id = String.valueOf(o.get("id"));

                String imagePath = String.valueOf(o.get("image_path")).replaceAll("\"", "");
                System.out.println(imagePath);
                String title = String.valueOf(o.get("title"));
                long consumption = Long.parseLong(String.valueOf(o.get("consumption_in_wh")));
                String source = String.valueOf(o.get("source"));

                Activity activity = new Activity(id, title, consumption, source, imagePath);
                activities.add(activity);
            });
            activityRepo.saveAll(activities);
        }
    }
}
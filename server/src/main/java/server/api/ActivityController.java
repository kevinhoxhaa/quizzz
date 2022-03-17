package server.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import commons.entities.Activity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ActivityRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final Random random;
    private final ActivityRepository repo;

    /**
     * Constructs a new activity controller object
     * @param random the random generator of activities
     * @param repo the activity repository
     */
    public ActivityController(Random random, ActivityRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Checks whether a String is null or empty
     * @param s the string to check
     * @return true if the string is null or empty
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Takes the activities from the JSON file and populates
     * them into the database on server start.
     * @return if activities have been populated successfully
     */
    @GetMapping(path = "/populate")
    public ResponseEntity<Boolean> populateActivities() throws IOException {
        if(repo.count() > 0) {
            return ResponseEntity.ok(false);
        }

        String content = Files.readString(
                Path.of(
                        ResourceUtils.getFile("classpath:activities/activities.json").getPath()
                ), StandardCharsets.US_ASCII);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(content);
        JsonArray array = element.getAsJsonArray();

        List<Activity> activities = new ArrayList<>();
        array.forEach(e -> {
            JsonObject o = e.getAsJsonObject();
            String id = String.valueOf(o.get("id"));

            String imagePath = String.valueOf(o.get("image_path"));
            String title = String.valueOf(o.get("title"));
            long consumption = Long.parseLong(String.valueOf(o.get("consumption_in_wh")));
            String source = String.valueOf(o.get("source"));

            Activity activity = new Activity(id, title, consumption, source, imagePath);
            activities.add(activity);
        });

        repo.saveAll(activities);
        return ResponseEntity.ok(true);
    }

    /**
     * Retrieves all activities from the repository and sends them
     * to the client
     * @return a list of all activities in the repository
     */
    @GetMapping(path = { "", "/" })
    public List<Activity> getAll() {
        return repo.findAll();
    }

    /**
     * Saves an activity sent by the client to the activity repository
     * @param activity the activity to save
     * @return the saved activity entity
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Activity> add(@RequestBody Activity activity) {
// || isNullOrEmpty(server) has to be added
        if (isNullOrEmpty(activity.title)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (isNullOrEmpty(activity.source)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(activity.consumption < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Activity saved = repo.save(activity);
        return ResponseEntity.ok(saved);
    }

    /**
     * Retrieves an activity by a given id and
     * returns a bad request if an activity with that
     * id does not exist
     * @param id the id of the activity to retrieve
     * @return the requested activity
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Activity>> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id));
    }

    /**
     * Retrieves a random activity from the activities repo
     * @return the random activity requested
     */
    @GetMapping("/rnd")
    public ResponseEntity<Optional<Activity>> getRandom() {
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(repo.findById((long) idx));
    }

    /**
     * Deletes an activity from the database if an activity
     * with the given id exists. Otherwise, returns a bad request
     * @param id the id of the activity to delete
     * @return the deleted activity if it exists
     */
    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<Activity> delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

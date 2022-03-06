package server.api;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.Random;

public class ActivityControllerTest {

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }

    private static final long NUMBER = 5;

    public int nextInt;
    private MyRandom random;
    private TestActivityRepository repo;

    private ActivityController sut;

    private Activity getActivity(String title, long consumption, String source) {
        return new Activity(title, consumption, source);
    }

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestActivityRepository();
        sut = new ActivityController(random, repo);
    }

    @Test
    public void getAllReturnsList() {
        assertNotNull(sut.getAll());
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getActivity(null, NUMBER, "random"));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotAddEmptySource() {
        var actual = sut.add(getActivity("random", NUMBER, ""));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotAddNegativeConsumption() {
        var actual = sut.add(getActivity("random", -NUMBER, "random"));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void addIncreasesActivitiesSize() {
        var actual = sut.add(getActivity("random", NUMBER, "random"));
        assertEquals(1, repo.activities.size());
    }

    @Test
    public void getByIdRetrievesActivity() {
        Activity activity = getActivity("random", NUMBER, "random");
        var expected = sut.add(activity);
        var actual = sut.getById(expected.getBody().id);
        assertTrue(actual.getBody().isPresent());
        assertEquals(expected.getBody(), actual.getBody().get());
    }

    @Test
    public void cannotGetNegativeId() {
        Activity activity = getActivity("random", NUMBER, "random");
        var actual = sut.getById(-NUMBER);
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void cannotGetNonexistentId() {
        Activity activity = getActivity("random", NUMBER, "random");
        var actual = sut.getById(NUMBER);
        assertTrue(actual.getStatusCode().is4xxClientError());
    }

    @Test
    public void randomSelection() {
        sut.add(getActivity("q1", NUMBER, "source"));
        sut.add(getActivity("q2", NUMBER, "source"));
        nextInt = 1;
        var actual = sut.getRandom();

        assertTrue(random.wasCalled);
        assertEquals("q2", actual.getBody().get().title);
    }

    @Test
    public void databaseIsUsed() {
        sut.add(getActivity("q1", NUMBER, "src"));
        repo.calledMethods.contains("save");
    }

    @Test
    public void cannotDeleteNegativeID() {
        var actual = sut.delete(-NUMBER);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonExistingActivity() {
        var actual = sut.delete(NUMBER);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void deleteRightActivity() {
        var activity = sut.add(getActivity("q1", NUMBER, "src"));
        var actual = sut.delete(activity.getBody().id);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
        assertFalse(repo.existsById(activity.getBody().id));
    }
}

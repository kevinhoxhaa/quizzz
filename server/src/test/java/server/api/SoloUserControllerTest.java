package server.api;

import commons.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

class SoloUserControllerTest {

    private TestSoloUserRepository soloRepo;

    private SoloUserController sut;

    @BeforeEach
    public void setup() {
        soloRepo = new TestSoloUserRepository();
        sut = new SoloUserController(soloRepo);
    }

    @Test
    public void cannotAddNullPersonSolo() {
        var actual = sut.addSolo((User) getUser(null));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void addCorrectPersonSolo() {
        var actual = sut.addSolo((User) getUser("q1"));
        var found = soloRepo.getById((long) soloRepo.users.size()-1);
        assertEquals(actual.getBody(), found);
        assertTrue(soloRepo.calledMethods.contains("save"));
        assertTrue(soloRepo.calledMethods.contains("getById"));
    }

    private static User getUser(String q) {
        return new User(q);
    }
}
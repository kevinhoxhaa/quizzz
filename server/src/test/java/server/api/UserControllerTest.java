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
package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Random;

import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestUserRepository repo;

    private UserController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestUserRepository();
        sut = new UserController(random, repo);
    }

    @Test
    public void cannotAddNullPerson() {
        var actual = sut.add(getUser(null));
        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void randomSelection() {
        sut.add(getUser("q1"));
        sut.add(getUser("q2"));
        nextInt = 1;
        var actual = sut.getRandom();

        assertTrue(random.wasCalled);
        assertEquals("q2", actual.getBody().username);
    }

    @Test
    public void duplicateUsername() {
        sut.add(getUser("q1"));
        var actual = sut.add(getUser("q1"));
        assertEquals(UNAUTHORIZED, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsed() {
        sut.add(getUser("q1"));
        repo.calledMethods.contains("save");
    }

    private static User getUser(String q) {
        return new User(q);
    }

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}
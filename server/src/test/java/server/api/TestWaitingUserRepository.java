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

import commons.entities.MultiplayerUser;
import commons.entities.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import server.database.WaitingUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestWaitingUserRepository implements WaitingUserRepository {

    public final List<MultiplayerUser> users = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<MultiplayerUser> findAll() {
        calledMethods.add("findAll");
        return users;
    }

    @Override
    public List<MultiplayerUser> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MultiplayerUser> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends MultiplayerUser> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends MultiplayerUser> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends MultiplayerUser> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<MultiplayerUser> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public MultiplayerUser getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultiplayerUser getById(Long id) {
        call("getById");
        return find(id).get();
    }

    private Optional<MultiplayerUser> find(Long id) {
        return users.stream().filter(q -> q.id == id).findFirst();
    }

    @Override
    public <S extends MultiplayerUser> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends MultiplayerUser> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<MultiplayerUser> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends MultiplayerUser> S save(S entity) {
        call("save");
        entity.id = (long) users.size();
        users.add(entity);
        return entity;
    }

    @Override
    public Optional<MultiplayerUser> findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        users.remove((int)(long) id);
    }

    @Override
    public void delete(MultiplayerUser entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends MultiplayerUser> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public <S extends MultiplayerUser> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends MultiplayerUser> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends MultiplayerUser> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends MultiplayerUser> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends MultiplayerUser, R> R findBy(
            Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        for(User user : users) {
            if(user.username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<MultiplayerUser> findByGameIDIsNull() {
        return users.stream().filter(u -> u.gameID == null).collect(Collectors.toList());
    }
}
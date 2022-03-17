package server.api;

import commons.entities.SoloUser;
import commons.entities.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.SoloUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestSoloUserRepository implements SoloUserRepository {

    public final List<SoloUser> users = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<SoloUser> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SoloUser> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<SoloUser> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SoloUser> findAllById(Iterable<Long> longs) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(SoloUser entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends SoloUser> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends SoloUser> S save(S user) {
        call("save");
        user.id = (long) users.size();
        users.add(user);
        return user;
    }

    @Override
    public <S extends SoloUser> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<SoloUser> findById(Long aLong) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends SoloUser> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends SoloUser> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<SoloUser> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public SoloUser getOne(Long aLong) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SoloUser getById(Long id) {
        // TODO Auto-generated method stub
        call("getById");
        return users.stream().filter(q -> q.id == id).findFirst().get();
    }

    @Override
    public <S extends SoloUser> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public <S extends SoloUser> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends SoloUser> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends SoloUser> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends SoloUser> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends SoloUser> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends SoloUser, R> R findBy(Example<S> example,
                           Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public ArrayList<User> sortUserByDescendingOrder(ArrayList<User> users){
        users.sort((o1, o2)
                -> o1.points.compareTo(o2.points));
        return users;
    }
}

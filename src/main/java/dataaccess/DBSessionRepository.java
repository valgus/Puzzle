package dataaccess;

import domain.entity.User;
import domain.entity.UserSession;
import domain.repository.SessionRepository;

//TODO реализовать взаимодействие с БД
public class DBSessionRepository implements SessionRepository {
    @Override
    public void create(UserSession userSession) {

    }

    @Override
    public UserSession getByUser(User user) {
        return null;
    }

    @Override
    public UserSession getByToken(String token) {
        return null;
    }
}

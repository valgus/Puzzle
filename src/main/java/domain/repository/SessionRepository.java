package domain.repository;

import domain.entity.User;
import domain.entity.UserSession;

public interface SessionRepository {

    void create(UserSession userSession);
    UserSession getByUser(User user);
    UserSession getByToken (String token);
}

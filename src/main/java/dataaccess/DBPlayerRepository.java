package dataaccess;

import domain.entity.Player;
import domain.repository.PlayerRepository;


//TODO реализовать взаимодействие с БД
public class DBPlayerRepository implements PlayerRepository {
    @Override
    public Player getByID(int playerID) {
        return null;
    }
}

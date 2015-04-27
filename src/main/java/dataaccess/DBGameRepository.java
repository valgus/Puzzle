package dataaccess;

import domain.entity.GameBoard;
import domain.repository.GameRepository;

import java.util.List;

//TODO реализовать взаимодействие с БД
public class DBGameRepository implements GameRepository {
    @Override
    public List<GameBoard> getAllByLogin(String name) {
        return null;
    }

    @Override
    public GameBoard getByID(int gameID) {
        return null;
    }
}

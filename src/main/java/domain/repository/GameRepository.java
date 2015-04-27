package domain.repository;

import domain.entity.GameBoard;

import java.util.List;

public interface GameRepository {
    List<GameBoard> getAllByLogin(String name);
    GameBoard getByID(int gameID);
;}

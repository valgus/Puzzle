package domain.dto;

import domain.entity.GameBoard;

import java.util.List;

public class GameOutput {

    private final List<GameBoard> gameBoards;

    public GameOutput(List<GameBoard> gameBoards) {
        this.gameBoards = gameBoards;
    }

    public List<GameBoard> getGameBoards() {
        return gameBoards;
    }
}

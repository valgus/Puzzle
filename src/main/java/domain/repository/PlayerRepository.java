package domain.repository;

import domain.entity.Player;

public interface PlayerRepository {
    Player getByID (int playerID);

}

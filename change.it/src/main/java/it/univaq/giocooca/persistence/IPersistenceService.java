package it.univaq.giocooca.persistence;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.exceptions.GameException;

public interface IPersistenceService {
    void saveGame(Game game, String filePath) throws GameException;
    Game loadGame(String filePath) throws GameException;
    void deleteGame(String filePath) throws GameException;
}

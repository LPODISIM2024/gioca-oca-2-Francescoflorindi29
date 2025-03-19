package it.univaq.giocooca.persistence;

import it.univaq.giocooca.Game;

public interface IPersistenceService {
    void saveGame(Game game, String filePath) throws Exception;
    Game loadGame(String filePath) throws Exception;
}


package it.univaq.giocooca.persistence;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.exceptions.GameException;

import java.io.*;

public class FilePersistenceService implements IPersistenceService {

    @Override
    public void saveGame(Game game, String filePath) throws GameException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(game);
        } catch (IOException e) {
            throw new GameException("Errore durante il salvataggio su file: " + filePath, e);
        }
    }

    @Override
    public Game loadGame(String filePath) throws GameException {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Game) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException("Errore durante il caricamento da file: " + filePath, e);
        }
    }

    @Override
    public void deleteGame(String filePath) throws GameException {
        File f = new File(filePath);
        if (!f.exists()) {
            throw new GameException("Il file non esiste: " + filePath);
        }
        if (!f.delete()) {
            throw new GameException("Impossibile cancellare il file: " + filePath);
        }
    }
}

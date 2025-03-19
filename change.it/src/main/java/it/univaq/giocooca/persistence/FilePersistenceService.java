package it.univaq.giocooca.persistence;

import it.univaq.giocooca.Game;

import java.io.*;

public class FilePersistenceService implements IPersistenceService {

    @Override
    public void saveGame(Game game, String filePath) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(game);
        }
    }

    @Override
    public Game loadGame(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Game) ois.readObject();
        }
    }
}


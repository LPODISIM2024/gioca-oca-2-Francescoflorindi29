package it.univaq.giocooca.persistence;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.exceptions.GameException;

import java.io.*;

public class FilePersistenceService implements IPersistenceService {

    private String baseDirectory; // Esempio: "C:\Temp\SavedGamesGOOSE"

    public FilePersistenceService(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void saveGame(Game game, String filePath) throws GameException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(game);
        } catch (IOException e) {
            throw new GameException("Errore salvataggio su " + filePath, e);
        }
    }

    @Override
    public Game loadGame(String filePath) throws GameException {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Game) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException("Errore caricamento da " + filePath, e);
        }
    }

    @Override
    public void deleteGame(String filePath) throws GameException {
        File f = new File(filePath);
        if (!f.exists()) {
            throw new GameException("File non esiste: " + filePath);
        }
        if (!f.delete()) {
            throw new GameException("Impossibile eliminare: " + filePath);
        }
    }

    /**
     * Costruisce un path unico nella baseDirectory
     * Se esiste già "baseName", aggiunge "-copia1", poi "-copia2", ecc.
     * Ritorna il path finale.
     */
    public String buildUniqueFileName(String baseName) {
        File dir = new File(baseDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String nameNoExt = baseName;
        String ext = "";
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex >= 0) {
            nameNoExt = baseName.substring(0, dotIndex);
            ext = baseName.substring(dotIndex);
        }

        File candidate = new File(dir, baseName);
        int copyNum = 1;
        while (candidate.exists()) {
            String newName = nameNoExt + "-copia" + copyNum + ext;
            candidate = new File(dir, newName);
            copyNum++;
        }
        return candidate.getAbsolutePath();
    }
}

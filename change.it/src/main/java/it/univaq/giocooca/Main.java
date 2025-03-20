package it.univaq.giocooca;

import it.univaq.giocooca.exceptions.GameException;
import it.univaq.giocooca.persistence.FilePersistenceService;
import it.univaq.giocooca.persistence.IPersistenceService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static final String BASE_DIR = "C:\\Temp";
    private static final String SAVES_DIR = BASE_DIR + "\\SavedGamesGOOSE";

    public static void main(String[] args) {
        // Assicuriamoci che esista C:\Temp e C:\Temp\SavedGamesGOOSE
        prepareFolders();

        Properties props = loadProperties();
        int diceFaces = Integer.parseInt(props.getProperty("dice.faces", "6"));
        int boardSize = Integer.parseInt(props.getProperty("board.size", "30"));

        IPersistenceService persistence = new FilePersistenceService(SAVES_DIR);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== MENÙ PRINCIPALE ===");
            System.out.println("1) Nuova partita");
            System.out.println("2) Carica partita");
            System.out.println("3) Elimina partita salvata");
            System.out.println("4) Esci");
            System.out.print("Scelta: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // CREAZIONE NUOVA PARTITA
                    Game game = createNewGame(diceFaces, boardSize, scanner, persistence);
                    if (game != null && !game.isGameEnded()) {
                        game.startGame(); // Avvia il loop della partita
                    }
                    break;

                case "2":
                    // CARICA PARTITA
                    loadAndPlayGame(scanner, persistence);
                    break;

                case "3":
                    // ELIMINA PARTITA
                    deleteSavedGame(scanner, persistence);
                    break;

                case "4":
                    exit = true;
                    break;

                default:
                    System.out.println("Opzione non valida!");
            }
        }

        System.out.println("Uscita dal programma.");
        scanner.close();
    }

    /**
     * Prepara le cartelle C:\Temp e C:\Temp\SavedGamesGOOSE (se non esistono, le crea).
     */
    private static void prepareFolders() {
        File base = new File(BASE_DIR);
        if (!base.exists()) {
            base.mkdirs();
        }
        File saves = new File(SAVES_DIR);
        if (!saves.exists()) {
            saves.mkdirs();
        }
    }

    /**
     * Crea una nuova partita, ma con un "sub-menù" che permette
     * di tornare al menù principale o annullare la configurazione.
     */
    private static Game createNewGame(int diceFaces, int boardSize,
                                      Scanner scanner,
                                      IPersistenceService persistence) {
        System.out.println("\n=== CREA NUOVA PARTITA ===");
        System.out.println("Vuoi procedere? (s/n) - 'n' tornerà al menù principale");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.equals("n")) {
            System.out.println("Torno al menù principale...");
            return null;
        } else if (!ans.equals("s")) {
            System.out.println("Opzione non valida! Torno al menù principale...");
            return null;
        }

        // Se l'utente vuole procedere
        Game game = new Game(diceFaces, boardSize, persistence);
        if (!game.setupNewGame(scanner)) {
            // L'utente ha annullato la configurazione => torniamo a menù
            return null;
        }
        return game;
    }

    /**
     * Permette di caricare una partita dall'elenco di file in SavedGamesGOOSE
     * e, se la partita non è terminata, la fa ripartire.
     */
    private static void loadAndPlayGame(Scanner scanner, IPersistenceService persistence) {
        File[] saves = listSavedGames();
        if (saves == null || saves.length == 0) {
            System.out.println("Nessuna partita salvata trovata!");
            return;
        }

        System.out.println("\n=== SCEGLI PARTITA DA CARICARE ===");
        for (int i = 0; i < saves.length; i++) {
            System.out.println((i+1) + ") " + saves[i].getName());
        }
        System.out.println("0) Annulla e torna al menù principale");
        System.out.print("Scelta: ");
        String input = scanner.nextLine().trim();

        int choice = -1;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Opzione non valida!");
            return;
        }
        if (choice == 0) {
            System.out.println("Torno al menù principale...");
            return;
        }
        if (choice < 1 || choice > saves.length) {
            System.out.println("Opzione non valida!");
            return;
        }

        File selected = saves[choice - 1];
        // Carichiamo
        Game loadedGame = new Game();
        loadedGame.setPersistenceService(persistence);
        loadedGame.loadGame(selected.getAbsolutePath());

        if (!loadedGame.isGameEnded()) {
            loadedGame.startGame();
        } else {
            System.out.println("Partita già terminata!");
        }
    }

    /**
     * Elimina un file di salvataggio a scelta, dalla cartella SavedGamesGOOSE.
     */
    private static void deleteSavedGame(Scanner scanner, IPersistenceService persistence) {
        File[] saves = listSavedGames();
        if (saves == null || saves.length == 0) {
            System.out.println("Nessuna partita salvata trovata!");
            return;
        }

        System.out.println("\n=== SCEGLI PARTITA DA ELIMINARE ===");
        for (int i = 0; i < saves.length; i++) {
            System.out.println((i+1) + ") " + saves[i].getName());
        }
        System.out.println("0) Annulla");
        System.out.print("Scelta: ");
        String input = scanner.nextLine().trim();

        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Opzione non valida!");
            return;
        }
        if (choice == 0) {
            System.out.println("Operazione annullata.");
            return;
        }
        if (choice < 1 || choice > saves.length) {
            System.out.println("Opzione non valida!");
            return;
        }

        try {
            persistence.deleteGame(saves[choice - 1].getAbsolutePath());
            System.out.println("File eliminato con successo!");
        } catch (GameException e) {
            System.out.println("Errore durante l'eliminazione: " + e.getMessage());
        }
    }

    /**
     * Ritorna la lista dei file .dat in C:\Temp\SavedGamesGOOSE
     */
    private static File[] listSavedGames() {
        File dir = new File(SAVES_DIR);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".dat"));
        if (files != null) {
            Arrays.sort(files); // Ordiniamo alfabeticamente
        }
        return files;
    }

    /**
     * Carica le proprietà da application.properties
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = Main.class.getResourceAsStream("/application.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("application.properties non trovato! Uso valori di default.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}

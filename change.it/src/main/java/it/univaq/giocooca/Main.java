package it.univaq.giocooca;

import it.univaq.giocooca.exceptions.GameException;
import it.univaq.giocooca.persistence.FilePersistenceService;
import it.univaq.giocooca.persistence.IPersistenceService;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Properties props = loadProperties();
        int faces = Integer.parseInt(props.getProperty("dice.faces", "6"));
        int boardSize = Integer.parseInt(props.getProperty("board.size", "20"));

        Scanner scanner = new Scanner(System.in);
        IPersistenceService persistence = new FilePersistenceService();

        boolean done = false;
        while (!done) {
            System.out.println("\n=== MENU INIZIALE ===");
            System.out.println("1) Nuova partita");
            System.out.println("2) Carica partita");
            System.out.println("3) Cancella partita salvata");
            System.out.println("4) Esci");
            System.out.print("Scegli un'opzione: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": {
                    Game game = new Game(faces, boardSize);
                    game.startGame();
                    break;
                }
                case "2": {
                    System.out.print("Inserisci il percorso del file salvato: ");
                    String filePath = scanner.nextLine();
                    Game loadedGame = new Game();
                    loadedGame.loadGame(filePath);
                    if (!loadedGame.isGameEnded()) {
                        loadedGame.startGame();
                    } else {
                        System.out.println("La partita caricata risulta già terminata.");
                    }
                    break;
                }
                case "3": {
                    System.out.print("Inserisci il percorso del file da cancellare: ");
                    String filePath = scanner.nextLine();
                    try {
                        persistence.deleteGame(filePath);
                        System.out.println("File cancellato con successo!");
                    } catch (GameException e) {
                        System.out.println("Errore cancellazione: " + e.getMessage());
                    }
                    break;
                }
                case "4": {
                    done = true;
                    System.out.println("Uscita dal gioco.");
                    break;
                }
                default:
                    System.out.println("Scelta non valida.");
            }
        }
        scanner.close();
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = Main.class.getResourceAsStream("/application.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("application.properties non trovato. Uso default.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }
}

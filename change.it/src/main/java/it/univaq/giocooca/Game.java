package it.univaq.giocooca;

import it.univaq.giocooca.minigame.IMinigameService;
import it.univaq.giocooca.minigame.MinigameServiceImpl;
import it.univaq.giocooca.persistence.IPersistenceService;
import it.univaq.giocooca.persistence.FilePersistenceService;
import it.univaq.giocooca.squares.Square;
import it.univaq.giocooca.logging.ILoggingService;
import it.univaq.giocooca.logging.ConsoleLoggingService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private Board board;
    private List<Player> players;
    private Dice dice;
    private int currentPlayerIndex;
    private boolean gameEnded;

    private transient ILoggingService logger;
    private transient IPersistenceService persistenceService;
    private transient IMinigameService minigameService;

    private transient Scanner scanner;

    public Game() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.gameEnded = false;
        this.logger = new ConsoleLoggingService();
        this.persistenceService = new FilePersistenceService();
        this.minigameService = new MinigameServiceImpl();
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.print("Vuoi caricare una partita salvata? (s/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("s")) {
            System.out.print("Inserisci il path del file di salvataggio: ");
            String file = scanner.nextLine();
            loadGame(file);
            if (this.scanner == null) {
                this.scanner = new Scanner(System.in);
            }
        } else {
            setupNewGame();
        }

        while (!gameEnded) {
            nextTurn();
        }

        System.out.println("Partita terminata!");
    }


    private void setupNewGame() {
        System.out.print("Quanti giocatori vuoi creare? ");
        int numPlayers = Integer.parseInt(scanner.nextLine());
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Nome del giocatore " + i + ": ");
            String name = scanner.nextLine();
            players.add(new Player(name));
        }

        this.dice = new Dice(6);
        this.board = new Board(20);

        logger.logAction("Nuova partita creata con " + numPlayers + " giocatori.");
    }

    public void nextTurn() {
        Player current = players.get(currentPlayerIndex);

        System.out.println("\n--------------------------------------");
        System.out.println("Tocca a: " + current.getName() + " (posizione: " + current.getPosition() + ")");
        System.out.println("Digita 'lancia' per lanciare il dado, 'salva' per salvare la partita, 'esci' per uscire:");
        String cmd = scanner.nextLine().trim().toLowerCase();

        switch (cmd) {
            case "lancia":
                if (!current.isBlocked()) {
                    int rollValue = dice.roll();
                    logger.logAction(current.getName() + " lancia il dado e fa " + rollValue);
                    System.out.println(current.getName() + " lancia il dado e fa " + rollValue);

                    current.move(rollValue, board.size());

                    Square sq = board.getSquare(current.getPosition());
                    sq.onLanding(this, current);

                    if (current.getPosition() == board.size() - 1) {
                        gameEnded = true;
                        logger.logAction(current.getName() + " ha vinto la partita!");
                        System.out.println(current.getName() + " ha vinto la partita!");
                    }
                } else {
                    logger.logAction(current.getName() + " e' bloccato e salta il turno!");
                    current.decrementBlockTurns(); 
                }
                break;

            case "salva":
                System.out.print("Inserisci il nome del file di salvataggio: ");
                String saveFile = scanner.nextLine();
                saveGame(saveFile);
                return; 

            case "esci":
                logger.logAction("Partita interrotta manualmente.");
                this.gameEnded = true;
                return;

            default:
                System.out.println("Comando non riconosciuto. Salti il turno.");
                break;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void saveGame(String filePath) {
        try {
            persistenceService.saveGame(this, filePath);
            logger.logAction("Partita salvata su file: " + filePath);
            System.out.println("Partita salvata con successo!");
        } catch (Exception e) {
            System.out.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    public void loadGame(String filePath) {
        try {
            Game loaded = persistenceService.loadGame(filePath);
            this.board = loaded.board;
            this.players = loaded.players;
            this.dice = loaded.dice;
            this.currentPlayerIndex = loaded.currentPlayerIndex;
            this.gameEnded = loaded.gameEnded;
            this.logger = new ConsoleLoggingService();
            this.persistenceService = new FilePersistenceService();
            this.minigameService = new MinigameServiceImpl();
            this.scanner = new Scanner(System.in);

            logger.logAction("Partita caricata da file: " + filePath);
            System.out.println("Partita caricata con successo!");
        } catch (Exception e) {
            System.out.println("Errore durante il caricamento: " + e.getMessage());
        }
    }


    public ILoggingService getLogger() {
        return logger;
    }

    public IMinigameService getMinigameService() {
        return minigameService;
    }

    public List<Player> getPlayers() {
        return players;
    }
}

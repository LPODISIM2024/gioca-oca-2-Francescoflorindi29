package it.univaq.giocooca;

import it.univaq.giocooca.exceptions.GameException;
import it.univaq.giocooca.logging.ConsoleLoggingService;
import it.univaq.giocooca.logging.ILoggingService;
import it.univaq.giocooca.minigame.IMinigameService;
import it.univaq.giocooca.minigame.MinigameServiceImpl;
import it.univaq.giocooca.persistence.FilePersistenceService;
import it.univaq.giocooca.persistence.IPersistenceService;
import it.univaq.giocooca.squares.Square;

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

    public Game(int diceFaces, int boardSize) {
        this();
        this.dice = new Dice(diceFaces);
        this.board = new Board(boardSize);
    }

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
        if (players.isEmpty()) {
            setupNewGame();
        }
        while (!gameEnded) {
            nextTurn();
        }
        System.out.println("Partita terminata!");
    }

    private void setupNewGame() {
        System.out.print("Quanti giocatori vuoi creare? ");
        String input = scanner.nextLine();
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            numPlayers = 2; // default
        }

        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Nome del giocatore " + i + ": ");
            String name = scanner.nextLine();

            System.out.print("Il giocatore " + i + " è IA (s/n)? ");
            String ans = scanner.nextLine().trim().toLowerCase();
            boolean isAI = ans.equals("s");

            players.add(new Player(name, isAI));
        }
        logger.logAction("Creati " + numPlayers + " giocatori.");
    }

    public void nextTurn() {
        Player current = players.get(currentPlayerIndex);

        System.out.println("\n--------------------------------------");
        System.out.println("Tocca a: " + current.getName() 
            + " (posizione: " + current.getPosition() + ")");

        String cmd;
        if (current.isAI()) {
            cmd = "lancia";
            System.out.println(current.getName() + " (CPU) decide automaticamente: LANCIA");
        } else {
            System.out.println("Comandi: [lancia] [salva] [esci]");
            cmd = scanner.nextLine().trim().toLowerCase();
        }

        switch (cmd) {
            case "lancia":
                if (!current.isBlocked()) {
                    int rollValue = dice.roll();
                    logger.logAction(current.getName() 
                        + " lancia il dado e ottiene " + rollValue);

                    current.move(rollValue, board.size());

                    Square sq = board.getSquare(current.getPosition());
                    sq.onLanding(this, current);

                    if (current.getPosition() == board.size() - 1) {
                        gameEnded = true;
                        logger.logAction(current.getName() + " ha vinto la partita!");
                        System.out.println(current.getName() + " ha vinto la partita!");
                    }
                } else {
                    logger.logAction(current.getName() + " è bloccato e salta il turno.");
                    current.decrementBlockTurns();
                }
                break;

            case "salva":
                if (!current.isAI()) {
                    System.out.print("Inserisci il percorso del file di salvataggio: ");
                    String path = scanner.nextLine();
                    saveGame(path);
                } else {
                    System.out.println("Il CPU non salva la partita!");
                }
                return;

            case "esci":
                if (!current.isAI()) {
                    logger.logAction("Partita interrotta manualmente da " 
                        + current.getName());
                    gameEnded = true;
                }
                return;

            default:
                if (!current.isAI()) {
                    System.out.println("Comando non riconosciuto! Turno perso.");
                }
                break;
        }

        board.printBoard(players);

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void saveGame(String filePath) {
        try {
            persistenceService.saveGame(this, filePath);
            logger.logAction("Partita salvata su file: " + filePath);
            System.out.println("Partita salvata con successo!");
        } catch (GameException e) {
            System.err.println("Errore salvataggio: " + e.getMessage());
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

            logger.logAction("Partita caricata da " + filePath);
            System.out.println("Partita caricata con successo!");
        } catch (GameException e) {
            System.err.println("Errore caricamento: " + e.getMessage());
        }
    }

    public boolean isGameEnded() {
        return gameEnded;
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

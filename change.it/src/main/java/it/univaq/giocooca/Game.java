package it.univaq.giocooca;

import it.univaq.giocooca.exceptions.GameException;
import it.univaq.giocooca.logging.FileLoggingService;
import it.univaq.giocooca.logging.ILoggingService;
import it.univaq.giocooca.minigame.IMinigameService;
import it.univaq.giocooca.minigame.MinigameServiceImpl;
import it.univaq.giocooca.persistence.FilePersistenceService;
import it.univaq.giocooca.persistence.IPersistenceService;
import it.univaq.giocooca.squares.Square;

import java.io.IOException;
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

	// Servizi
	private transient ILoggingService logger;
	private transient IPersistenceService persistence;
	private transient IMinigameService minigameService;
	private transient Scanner scanner;

	private static final String LOG_FILE = "game.log";

	public Game() {
		this.players = new ArrayList<>();
		this.currentPlayerIndex = 0;
		this.gameEnded = false;

		// default
		this.persistence = new FilePersistenceService("C:\\Temp\\SavedGamesGOOSE");
		this.minigameService = new MinigameServiceImpl();
		this.scanner = new Scanner(System.in);

		// Dentro la classe Game (dove c'è l'errore di lambda):
		try {
			this.logger = new FileLoggingService(LOG_FILE);
		} catch (IOException e) {
			e.printStackTrace();
			// fallback console
			this.logger = new ILoggingService() {
				@Override
				public void logAction(String action) {
					System.out.println("[LOG-FALLBACK] " + action);
				}

				@Override
				public void close() {
					// non facciamo niente
				}
			};
		}
	}

	// Nuovo costruttore, passiamo la cartella di salvataggio
	public Game(int diceFaces, int boardSize, IPersistenceService persistence) {
		this();
		this.dice = new Dice(diceFaces);
		this.board = new Board(boardSize);
		this.persistence = persistence;
	}

	public boolean setupNewGame(Scanner scanner) {
	    System.out.print("Quanti giocatori vuoi creare? (minimo 2, 0 per annullare): ");
	    String input = scanner.nextLine();
	    int numPlayers;
	    try {
	        numPlayers = Integer.parseInt(input);
	    } catch (NumberFormatException e) {
	        System.out.println("Opzione non valida! Annullamento configurazione...");
	        return false;
	    }

	    if (numPlayers == 0) {
	        System.out.println("Annullamento configurazione partita...");
	        return false;
	    }
	    if (numPlayers < 2) {
	        System.out.println("Devi avere almeno 2 giocatori! Annullamento configurazione...");
	        return false;
	    }

	    for (int i = 1; i <= numPlayers; i++) {
	        System.out.print("Nome del giocatore " + i + " (o 'annulla' per annullare): ");
	        String name = scanner.nextLine().trim();
	        if (name.equalsIgnoreCase("annulla")) {
	            System.out.println("Configurazione annullata!");
	            return false;
	        }

	        boolean valid = false;
	        boolean isAI = false;
	        while (!valid) {
	            System.out.print("Il giocatore " + i + " è IA? (s/n) : ");
	            String ans = scanner.nextLine().trim().toLowerCase();
	            switch (ans) {
	                case "s":
	                    isAI = true;
	                    valid = true;
	                    break;
	                case "n":
	                    isAI = false;
	                    valid = true;
	                    break;
	                default:
	                    System.out.println("Opzione non valida!");
	            }
	        }

	        players.add(new Player(name, isAI));
	    }
	    logger.logAction("Creati " + numPlayers + " giocatori.");
	    return true;
	}


	public void startGame() {
		while (!gameEnded) {
			nextTurn();
		}
		System.out.println("Partita terminata!");
		logger.close();
	}

	public void nextTurn() {
		Player current = players.get(currentPlayerIndex);
		String red = "\u001B[31m";
		String reset = "\u001B[0m";

		System.out.println("\n--------------------------------------");
		System.out.println(
				"Turno di: " + red + current.getName() + reset + " (posizione: " + current.getPosition() + ")");
		logger.logAction("Tocca a " + current.getName());

		String cmd;
		if (current.isAI()) {
			cmd = "lancia";
			System.out.println(current.getName() + " (CPU) decide di lanciare il dado automaticamente.");
		} else {
			System.out.println("Comandi: [lancia] [salva] [esci]");
			cmd = scanner.nextLine().trim().toLowerCase();
		}

		switch (cmd) {
		case "lancia":
			if (!current.isBlocked()) {
				int rollVal = dice.roll();
				// stampiamo in rosso anche il risultato del dado
				System.out.println(red + current.getName() + reset + " ha lanciato il dado e ha ottenuto: " + red
						+ rollVal + reset);
				logger.logAction(current.getName() + " lancia il dado: " + rollVal);

				current.move(rollVal, board.size());
				Square sq = board.getSquare(current.getPosition());
				sq.onLanding(this, current);

				// Verifica vittoria
				if (current.getPosition() == board.size() - 1) {
					gameEnded = true;
					logger.logAction(current.getName() + " ha vinto la partita!");
					System.out.println("### " + red + current.getName() + reset + " ha vinto la partita ###");
				}
			} else {
				logger.logAction(current.getName() + " è bloccato. Turno saltato.");
				current.decrementBlockTurns();
			}
			break;

		case "salva":
			if (!current.isAI()) {
				System.out.print("Inserisci il nome della partita: ");
				String saveName = scanner.nextLine().trim();
				saveGame(saveName); // Salvataggio
			} else {
				System.out.println("Il CPU non può salvare la partita!");
			}
			return;

		case "esci":
			if (!current.isAI()) {
				logger.logAction(current.getName() + " abbandona la partita.");
				gameEnded = true;
			}
			return;

		default:
			if (!current.isAI()) {
				System.out.println("Opzione non valida! Turno perso.");
			}
			break;
		}

		// Visualizziamo il tabellone
		board.printBoard(players);

		// Passa al giocatore successivo
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
	}

	/**
	 * Salvataggio: crea un file con estensione ".dat" in C:\Temp\SavedGamesGOOSE Se
	 * esiste già un file con quel nome, aggiunge "-copia1", "-copia2", ecc.
	 */
	public void saveGame(String baseName) {
		if (!baseName.endsWith(".dat")) {
			baseName += ".dat";
		}
		// Verifica se esiste già
		String filePath = persistence.buildUniqueFileName(baseName);
		try {
			persistence.saveGame(this, filePath);
			logger.logAction("Partita salvata su " + filePath);
			System.out.println("Partita salvata in: " + filePath);
		} catch (GameException e) {
			System.err.println("Errore salvataggio: " + e.getMessage());
		}
	}

	public void loadGame(String filePath) {
		try {
			Game loaded = persistence.loadGame(filePath);
			// Copiamo i campi
			this.board = loaded.board;
			this.players = loaded.players;
			this.dice = loaded.dice;
			this.currentPlayerIndex = loaded.currentPlayerIndex;
			this.gameEnded = loaded.gameEnded;

			// Ricostruiamo i servizi
			this.persistence = loaded.persistence;
			this.minigameService = new MinigameServiceImpl();
			this.scanner = new Scanner(System.in);
			// Dentro la classe Game (dove c'è l'errore di lambda):
			try {
			    this.logger = new FileLoggingService(LOG_FILE);
			} catch (IOException e) {
			    e.printStackTrace();
			    // fallback console
			    this.logger = new ILoggingService() {
			        @Override
			        public void logAction(String action) {
			            System.out.println("[LOG-FALLBACK] " + action);
			        }

			        @Override
			        public void close() {
			            // non facciamo niente
			        }
			    };
			}


			logger.logAction("Partita caricata da " + filePath);
			System.out.println("Partita caricata con successo!");
		} catch (GameException e) {
			System.err.println("Errore caricamento: " + e.getMessage());
		}
	}

	public boolean isGameEnded() {
		return gameEnded;
	}

	public void setPersistenceService(IPersistenceService persistence) {
		this.persistence = persistence;
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

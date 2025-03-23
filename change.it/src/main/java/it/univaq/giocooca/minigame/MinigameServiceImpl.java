package it.univaq.giocooca.minigame;

import it.univaq.giocooca.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Esempio di implementazione Morra + Tris con le regole richieste.
 */
public class MinigameServiceImpl implements IMinigameService {

    private Scanner scanner;
    private Random rand;

    public MinigameServiceImpl() {
        this.scanner = new Scanner(System.in);
        this.rand = new Random();
    }

    // =========================================
    //       MORRA (Sasso / Carta / Forbice)
    // =========================================
    @Override
    public Player playMorra(Player p1, Player p2) {
        int scoreP1 = 0;
        int scoreP2 = 0;

        // al meglio di 3
        while (scoreP1 < 2 && scoreP2 < 2 && (scoreP1 + scoreP2 < 3)) {
            // un round
            int result = singleMorraRound(p1, p2);
            if (result == 1) scoreP1++;
            else if (result == 2) scoreP2++;
        }

        if (scoreP1 > scoreP2) return p1;
        if (scoreP2 > scoreP1) return p2;
        return null; // pareggio
    }

    /**
     * Esegue un singolo round di Morra:
     * - p1 sceglie (AI => random, umano => console) -> la memorizziamo in moveP1
     * - p2 sceglie (AI => random, umano => console) -> la memorizziamo in moveP2
     * - stampiamo le mosse solo dopo che entrambi hanno scelto
     * Ritorna 1 se vince p1, 2 se vince p2, 0 se pareggio di round
     */
    private int singleMorraRound(Player p1, Player p2) {
        // 1) Chiediamo la mossa a p1 (umano o AI), ma NON la stampiamo
        int moveP1 = getMorraMove(p1);
        // Opzionale: se vuoi dire a console che ha scelto qualcosa, metti "p1 ha scelto ***"
        System.out.println(p1.getName() + " ha scelto: *** (mossa nascosta)");

        // 2) Chiediamo la mossa a p2
        int moveP2 = getMorraMove(p2);
        System.out.println(p2.getName() + " ha scelto: *** (mossa nascosta)");

        // 3) Ora abbiamo entrambe le mosse, le "sveliamo" tutte insieme
        System.out.println("\n--- Sveliamo le mosse ---");
        System.out.println(p1.getName() + " mostra: " + morraToString(moveP1));
        System.out.println(p2.getName() + " mostra: " + morraToString(moveP2));

        // 4) Applichiamo la logica di confronto
        if (moveP1 == moveP2) {
            System.out.println("Pareggio di round!");
            return 0;
        }
        // sasso(0) batte forbice(2), carta(1) batte sasso(0), forbice(2) batte carta(1)
        if ((moveP1 == 0 && moveP2 == 2) ||
            (moveP1 == 1 && moveP2 == 0) ||
            (moveP1 == 2 && moveP2 == 1)) {
            System.out.println("Vince il round: " + p1.getName());
            return 1;
        } else {
            System.out.println("Vince il round: " + p2.getName());
            return 2;
        }
    }


    /**
     * Ritorna 0-sasso, 1-carta, 2-forbice.
     * Se è AI => random
     * Se è umano => chiede in console
     * Non svela la mossa immediatamente, 
     * la sveliamo solo dopo che l'altro ha scelto (vedi singleMorraRound).
     */
    private int getMorraMove(Player p) {
        if (p.isAI()) {
            // Scelta random
            return rand.nextInt(3); // 0..2
        } else {
            while (true) {
                System.out.print(p.getName() + ", scegli (sasso/carta/forbice): ");
                String input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    case "sasso":   return 0;
                    case "carta":   return 1;
                    case "forbice": return 2;
                    default:
                        System.out.println("Mossa non valida! Riprova.");
                }
            }
        }
    }

    private String morraToString(int m) {
        switch (m) {
            case 0: return "sasso";
            case 1: return "carta";
            case 2: return "forbice";
            default: return "?";
        }
    }

    // =========================================
    //                TRIS
    // =========================================
    @Override
    public Player playTris(Player p1, Player p2) {
        // Board 3x3
        char[][] board = {
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
        };

        // p1 = X, p2 = O
        char currentChar = 'X';
        int movesCount = 0;

        while (true) {
            // Stampiamo la board (senza linee vincenti finché non c'è vittoria)
            printTrisBoard(board, null);

            // Determino chi deve giocare adesso
            Player currentPlayer = (currentChar == 'X') ? p1 : p2;

            // Il giocatore (umano o AI) sceglie una mossa
            String move = getTrisMove(currentPlayer, board, currentChar);

            // Convertiamo input in row,col
            int row = move.charAt(0) - 'A';  // A->0, B->1, C->2
            int col = move.charAt(1) - '1';  // '1'->0, '2'->1, '3'->2
            board[row][col] = currentChar;
            movesCount++;

            // Check vittoria
            List<int[]> winningCells = checkWinAndGetCells(board, currentChar);
            if (winningCells != null) {
                // stampiamo la board con le celle vincenti evidenziate
                printTrisBoard(board, winningCells);
                System.out.println("🎉 Giocatore " + currentPlayer.getName() 
                    + " (" + currentChar + ") ha vinto!<");
                return currentPlayer; // p1 o p2
            }

            // Pareggio?
            if (movesCount == 9) {
                printTrisBoard(board, null);
                System.out.println("Pareggio nel Tris!");
                return null;
            }

            // Cambio giocatore
            currentChar = (currentChar == 'X') ? 'O' : 'X';
        }
    }

    // -- Metodi di supporto per TRIS --

    /**
     * checkWinAndGetCells: se c'è una vittoria di 'playerChar', 
     * restituisce le celle (row,col) che formano la linea vincente, 
     * altrimenti null.
     */
    private List<int[]> checkWinAndGetCells(char[][] b, char playerChar) {
        // Righe
        for (int i = 0; i < 3; i++) {
            if (b[i][0] == playerChar && b[i][1] == playerChar && b[i][2] == playerChar) {
                return List.of(new int[]{i,0}, new int[]{i,1}, new int[]{i,2});
            }
        }
        // Colonne
        for (int j = 0; j < 3; j++) {
            if (b[0][j] == playerChar && b[1][j] == playerChar && b[2][j] == playerChar) {
                return List.of(new int[]{0,j}, new int[]{1,j}, new int[]{2,j});
            }
        }
        // Diagonali
        if (b[0][0] == playerChar && b[1][1] == playerChar && b[2][2] == playerChar) {
            return List.of(new int[]{0,0}, new int[]{1,1}, new int[]{2,2});
        }
        if (b[0][2] == playerChar && b[1][1] == playerChar && b[2][0] == playerChar) {
            return List.of(new int[]{0,2}, new int[]{1,1}, new int[]{2,0});
        }
        return null;
    }

    /**
     * printTrisBoard: stampa la board con la formattazione desiderata,
     * e se winningCells != null, mette [X] o [O] nelle celle vincenti.
     */
    private void printTrisBoard(char[][] b, List<int[]> winningCells) {
        System.out.println("   1   2   3");
        System.out.println(" ╔═══╦═══╦═══╗");
        for (int i = 0; i < 3; i++) {
            char rowLabel = (char) ('A' + i);
            System.out.print(rowLabel + "║");
            for (int j = 0; j < 3; j++) {
                char symbol = b[i][j];
                if (symbol != ' ' && isWinningCell(winningCells, i, j)) {
                    System.out.print("[" + symbol + "]");
                } else {
                    System.out.print(" " + symbol + " ");
                }
                System.out.print("║");
            }
            System.out.println();
            if (i < 2) {
                System.out.println(" ╠═══╬═══╬═══╣");
            }
        }
        System.out.println(" ╚═══╩═══╩═══╝");
    }

    private boolean isWinningCell(List<int[]> cells, int r, int c) {
        if (cells == null) return false;
        for (int[] cell : cells) {
            if (cell[0] == r && cell[1] == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Se il player è IA, sceglie una cella libera a caso (es: "A1").
     * Altrimenti chiede in console.
     */
    private String getTrisMove(Player currentPlayer, char[][] board, char currentChar) {
        if (currentPlayer.isAI()) {
            // Sceglie una cella libera a caso
            List<String> freeCells = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        String cell = "" + (char)('A' + i) + (char)('1' + j);
                        freeCells.add(cell);
                    }
                }
            }
            if (!freeCells.isEmpty()) {
                int idx = rand.nextInt(freeCells.size());
                String choice = freeCells.get(idx);
                System.out.println(currentPlayer.getName() + " (CPU) gioca su: " + choice);
                return choice;
            } else {
                // Non dovremmo mai arrivare qui se la board non è piena
                return "A1";
            }
        } else {
            while (true) {
                System.out.println("Giocatore " + currentPlayer.getName() 
                    + " (" + currentChar + "), inserisci la mossa (es: A1, B3): ");
                String input = scanner.nextLine().toUpperCase();
                if (isValidTrisInput(input) && isCellFree(board, input)) {
                    return input;
                }
                System.out.println("Mossa non valida o casella occupata. Riprova.");
            }
        }
    }

    private boolean isValidTrisInput(String s) {
        if (s.length() != 2) return false;
        char r = s.charAt(0);
        char c = s.charAt(1);
        if ("ABC".indexOf(r) == -1) return false;
        if ("123".indexOf(c) == -1) return false;
        return true;
    }

    private boolean isCellFree(char[][] b, String s) {
        int row = s.charAt(0) - 'A';
        int col = s.charAt(1) - '1';
        return b[row][col] == ' ';
    }
}

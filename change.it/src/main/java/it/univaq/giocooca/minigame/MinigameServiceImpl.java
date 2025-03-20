package it.univaq.giocooca.minigame;

import it.univaq.giocooca.Player;

import java.util.Random;
import java.util.Scanner;

/**
 * Implementazione semplificata di Morra e Tris al meglio di 3.
 */
public class MinigameServiceImpl implements IMinigameService {

    private Scanner scanner;
    private Random rand;

    public MinigameServiceImpl() {
        this.scanner = new Scanner(System.in);
        this.rand = new Random();
    }

    @Override
    public Player playMorra(Player p1, Player p2) {
        int scoreP1 = 0;
        int scoreP2 = 0;
        // al meglio di 3 => primo a fare 2 punti vince

        while (scoreP1 < 2 && scoreP2 < 2 && (scoreP1 + scoreP2 < 3)) {
            int result = singleMorraRound(p1, p2);
            if (result == 1) scoreP1++;
            else if (result == 2) scoreP2++;
        }

        if (scoreP1 > scoreP2) return p1;
        if (scoreP2 > scoreP1) return p2;
        return null; // pareggio
    }

    private int singleMorraRound(Player p1, Player p2) {
        // 1 = p1 vince, 2 = p2 vince, 0 = pareggio
        // sasso=0, carta=1, forbice=2

        int moveP1 = getMorraMove(p1);
        int moveP2 = getMorraMove(p2);

        // System.out.println("moveP1=" + moveP1 + ", moveP2=" + moveP2);

        if (moveP1 == moveP2) return 0;
        if ((moveP1 == 0 && moveP2 == 2) ||
            (moveP1 == 1 && moveP2 == 0) ||
            (moveP1 == 2 && moveP2 == 1)) {
            return 1;
        } else {
            return 2;
        }
    }

    private int getMorraMove(Player p) {
        if (p.isAI()) {
            // CPU sceglie random
            return rand.nextInt(3); // 0..2
        } else {
            // Giocatore umano => chiediamo in console
            while (true) {
                System.out.print("[" + p.getName() + "] Scegli (sasso/carta/forbice): ");
                String input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    case "sasso":   return 0;
                    case "carta":   return 1;
                    case "forbice": return 2;
                    default:
                        System.out.println("Inserimento non valido!");
                }
            }
        }
    }

    @Override
    public Player playTris(Player p1, Player p2) {
        // Esempio semplificato: 3 "mini-round" casuali
        int scoreP1 = 0;
        int scoreP2 = 0;

        for (int i = 0; i < 3; i++) {
            // 0 = pareggio, 1 = p1 vince, 2 = p2 vince
            int result = singleTrisRound(p1, p2);
            if (result == 1) scoreP1++;
            else if (result == 2) scoreP2++;
        }
        if (scoreP1 > scoreP2) return p1;
        if (scoreP2 > scoreP1) return p2;
        return null;
    }

    private int singleTrisRound(Player p1, Player p2) {
        // Demo: vince p1 con 1/3, p2 con 1/3, pareggio con 1/3
        int r = rand.nextInt(3);
        if (r == 0) return 1;
        if (r == 1) return 2;
        return 0;
    }
}

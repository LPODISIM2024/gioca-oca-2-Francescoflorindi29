package it.univaq.giocooca.minigame;

import it.univaq.giocooca.Player;

import java.util.Random;

public class MinigameServiceImpl implements IMinigameService {

    private Random rand = new Random();

    @Override
    public Player playMorra(Player p1, Player p2) {
        int r = rand.nextInt(3);
        switch (r) {
            case 0: return p1; 
            case 1: return p2; 
            default: return null; 
        }
    }

    @Override
    public Player playTris(Player p1, Player p2) {
        int r = rand.nextInt(3);
        switch (r) {
            case 0: return p1;
            case 1: return p2;
            default: return null;
        }
    }
}

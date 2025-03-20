package it.univaq.giocooca;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int position;
    private boolean blocked;
    private int turnsBlocked;
    private boolean ai;

    public Player(String name, boolean ai) {
        this.name = name;
        this.ai = ai;
        this.position = 0;
        this.blocked = false;
        this.turnsBlocked = 0;
    }

    public String getName() {
        return name;
    }
    public int getPosition() {
        return position;
    }
    public boolean isBlocked() {
        return blocked;
    }
    public boolean isAI() {
        return ai;
    }

    public void setPosition(int newPos) {
        this.position = newPos;
    }

    public void move(int steps, int boardSize) {
        int newPos = position + steps;
        // Regola: se superi l'ultima casella, rimbalzi
        if (newPos >= boardSize) {
            int overshoot = newPos - (boardSize - 1);
            newPos = (boardSize - 1) - overshoot;
        }
        this.position = newPos;
    }

    public void setBlocked(int turns) {
        this.blocked = true;
        this.turnsBlocked = turns;
    }

    public void decrementBlockTurns() {
        if (turnsBlocked > 0) {
            turnsBlocked--;
        }
        if (turnsBlocked == 0) {
            blocked = false;
        }
    }
}

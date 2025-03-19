package it.univaq.giocooca;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int position;
    private boolean blocked;
    private int turnsBlocked;

    public Player(String name) {
        this.name = name;
        this.position = 0;
        this.blocked = false;
        this.turnsBlocked = 0;
    }

    public void move(int steps, int boardSize) {
        int newPos = position + steps;
        if (newPos >= boardSize) {
            int overshoot = newPos - (boardSize - 1);
            newPos = (boardSize - 1) - overshoot;
        }
        this.position = newPos;
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

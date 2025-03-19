package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

import java.io.Serializable;

public abstract class Square implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int index;

    public Square(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }


    public abstract void onLanding(Game game, Player player);
}

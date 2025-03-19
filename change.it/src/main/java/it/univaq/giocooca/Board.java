package it.univaq.giocooca;

import it.univaq.giocooca.squares.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Square> squares;

    public Board(int size) {
        squares = new ArrayList<>(size);
        initBoard(size);
    }

    private void initBoard(int size) {
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            int r = rand.nextInt(100);
            if (r < 10 && i != 0 && i != size - 1) {
                squares.add(new LocandaSquare(i)); 
            } else if (r < 15 && i != 0 && i != size - 1) {
                squares.add(new MorraSquare(i)); 
            } else if (r < 20 && i != 0 && i != size - 1) {
                squares.add(new TrisSquare(i));
            } else {
                squares.add(new NormalSquare(i)); 
            }
        }

        squares.set(0, new NormalSquare(0));
        squares.set(size - 1, new NormalSquare(size - 1));
    }

    public Square getSquare(int index) {
        return squares.get(index);
    }

    public int size() {
        return squares.size();
    }
}

package it.univaq.giocooca;

import java.io.Serializable;
import java.util.Random;

/**
 * Incapsula il dado a 'faces' facce (letto da application.properties).
 */
public class Dice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int faces;
    private transient Random rand;

    public Dice(int faces) {
        this.faces = faces;
        this.rand = new Random();
    }

    public int roll() {
        if (rand == null) {
            rand = new Random();
        }
        return rand.nextInt(faces) + 1;
    }
}

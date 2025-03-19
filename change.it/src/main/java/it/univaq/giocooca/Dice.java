package it.univaq.giocooca;
import java.io.Serializable;
import java.util.Random;

public class Dice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int faces;
    private transient Random random;

    public Dice(int faces) {
        this.faces = faces;
        this.random = new Random();
    }

    public int roll() {
        if (random == null) {
            random = new Random();
        }
        return random.nextInt(faces) + 1;
    }
}

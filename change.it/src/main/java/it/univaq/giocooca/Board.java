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
            } else if (r < 20 && i != 0 && i != size - 1) {
                squares.add(new MorraSquare(i));
            } else if (r < 30 && i != 0 && i != size - 1) {
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

    public void printBoard(List<Player> players) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < squares.size(); i++) {
            Square s = squares.get(i);
            
            String symbol = getSquareSymbol(s);

            String occupant = getPlayersInSquare(i, players);

            sb.append("[ ")
              .append(String.format("%2d", i))
              .append(": ")
              .append(symbol)
              .append(" (")
              .append(occupant)
              .append(") ]");

            if (i < squares.size() - 1) {
                sb.append(" - ");
            }

        }

        System.out.println("\n=== STATO DEL TABELLONE ===");
        System.out.println(sb.toString());
        System.out.println("===========================");
    }

    private String getSquareSymbol(Square s) {
        if (s instanceof NormalSquare) return "N";
        if (s instanceof LocandaSquare) return "L";
        if (s instanceof MorraSquare)  return "M";
        if (s instanceof TrisSquare)   return "T";
        return "?";
    }

    private String getPlayersInSquare(int index, List<Player> players) {
        List<String> names = new ArrayList<>();
        for (Player p : players) {
            if (p.getPosition() == index) {
                names.add(p.getName());
            }
        }
        if (names.isEmpty()) {
            return "";
        }
        return String.join(", ", names);
    }
}

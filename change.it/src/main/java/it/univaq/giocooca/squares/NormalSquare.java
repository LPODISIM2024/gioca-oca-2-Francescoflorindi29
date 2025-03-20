package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

/**
 * Casella normale senza effetti speciali.
 */
public class NormalSquare extends Square {
    private static final long serialVersionUID = 1L;

    public NormalSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player passer) {
        game.getLogger().logAction(
            passer.getName() + " si trova su una casella normale (index " + index + ")."
        );
    }
}

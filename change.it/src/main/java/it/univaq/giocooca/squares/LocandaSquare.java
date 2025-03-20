package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

/**
 * Casella Locanda: paga la posta e resta fermo 1 turno (solo come esempio).
 */
public class LocandaSquare extends Square {
    private static final long serialVersionUID = 1L;

    public LocandaSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player passer) {
        game.getLogger().logAction(
            passer.getName() + " entra nella Locanda e rimane fermo 1 turno."
        );
        passer.setBlocked(1);
    }
}

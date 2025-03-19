package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

public class LocandaSquare extends Square {
    public LocandaSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player player) {
        game.getLogger().logAction(player.getName() + " entra nella Locanda. Resta bloccato 1 turno.");
        player.setBlocked(1);
    }
}

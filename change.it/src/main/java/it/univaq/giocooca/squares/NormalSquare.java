package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

public class NormalSquare extends Square {

    public NormalSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player player) {
        game.getLogger().logAction(player.getName() + " è su una casella normale (index " + index + ").");
    }
}

package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

public abstract class MinigameSquare extends Square {

    public MinigameSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player passer) {
        playMinigame(game, passer);
    }

    protected abstract void playMinigame(Game game, Player passer);
}

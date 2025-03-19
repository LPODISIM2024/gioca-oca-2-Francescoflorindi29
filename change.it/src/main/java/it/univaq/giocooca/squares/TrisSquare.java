package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

public class TrisSquare extends MinigameSquare {

    public TrisSquare(int index) {
        super(index);
    }

    @Override
    protected void playMinigame(Game game, Player passer) {
        Player opponent = findRandomOpponent(game, passer);

        game.getLogger().logAction(
            passer.getName() + " sfida " + opponent.getName() + " a Tris."
        );

        Player winner = game.getMinigameService().playTris(passer, opponent);
        if (winner == null) {
            game.getLogger().logAction("La sfida di Tris finisce in pareggio!");
        } else {
            game.getLogger().logAction("Vince " + winner.getName() + " a Tris!");
        }
    }

    private Player findRandomOpponent(Game game, Player passer) {
        return game.getPlayers().stream()
                .filter(p -> p != passer)
                .findAny()
                .orElse(passer);
    }
}


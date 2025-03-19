package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

public class MorraSquare extends MinigameSquare {

    public MorraSquare(int index) {
        super(index);
    }

    @Override
    protected void playMinigame(Game game, Player passer) {
        Player opponent = findRandomOpponent(game, passer);

        game.getLogger().logAction(
            passer.getName() + " sfida " + opponent.getName() + " a Morra (Sasso-Carta-Forbice)."
        );

        Player winner = game.getMinigameService().playMorra(passer, opponent);
        if (winner == null) {
            game.getLogger().logAction("La Morra finisce in pareggio!");
        } else {
            game.getLogger().logAction("Vince " + winner.getName() + " a Morra!");
        }
    }

    private Player findRandomOpponent(Game game, Player passer) {
        return game.getPlayers().stream()
                .filter(p -> p != passer)
                .findAny()
                .orElse(passer); 
    }
}

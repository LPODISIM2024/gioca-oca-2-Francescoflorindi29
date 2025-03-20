package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casella Tris: se il passante non è primo, sfida il primo a Tris; se è già primo, sfida l'ultimo.
 * Al meglio di 3. In caso di pareggio, nessun cambio di posizione.
 */
public class TrisSquare extends Square {
    private static final long serialVersionUID = 1L;

    public TrisSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player passer) {
        // Determiniamo avversario
        Player opponent = findOpponent(game, passer);
        game.getLogger().logAction(
            passer.getName() + " atterra su TrisSquare e sfida " + opponent.getName() + " a Tris!"
        );
        System.out.println(passer.getName() 
            + " atterra su TRIS e sfida " + opponent.getName() + "! (al meglio di 3)");

        // Gioca Tris
        Player winner = game.getMinigameService().playTris(passer, opponent);
        if (winner == null) {
            // Pareggio
            game.getLogger().logAction("Tris finisce in pareggio. Nessun cambio di posizione.");
            System.out.println("Tris finisce in pareggio. Nessun cambio di posizione.");
        } else {
            // Scambio posizioni
            if (winner == passer) {
                game.getLogger().logAction(passer.getName() + " ha vinto a Tris contro " 
                    + opponent.getName() + ". Scambio posizioni.");
            } else {
                game.getLogger().logAction(opponent.getName() + " ha vinto a Tris contro " 
                    + passer.getName() + ". Scambio posizioni.");
            }
            int tmp = passer.getPosition();
            passer.setPosition(opponent.getPosition());
            opponent.setPosition(tmp);
        }
    }

    private Player findOpponent(Game game, Player passer) {
        List<Player> sorted = game.getPlayers().stream()
            .sorted(Comparator.comparingInt(Player::getPosition))
            .collect(Collectors.toList());

        Player first = sorted.get(sorted.size() - 1);
        Player last = sorted.get(0);

        // se passer è primo, sfida l'ultimo; altrimenti sfida il primo
        if (passer.getPosition() >= first.getPosition()) {
            return last;
        } else {
            return first;
        }
    }
}

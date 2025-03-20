package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casella Morra: obbliga il passante a sfidare l'ultimo della classifica
 * al meglio di 3. In caso di pareggio, posizioni inalterate.
 * Se il passante è ultimo, sfida il primo.
 */
public class MorraSquare extends Square {
    private static final long serialVersionUID = 1L;

    public MorraSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player passer) {
        // Troviamo "l'ultimo" in base alla posizione, o se passer è ultimo, troviamo "il primo"
        Player opponent = findOpponent(game, passer);
        game.getLogger().logAction(
            passer.getName() + " finisce su MorraSquare e sfida " + opponent.getName() + " alla Morra."
        );

        // Gioca la Morra (al meglio di 3)
        Player winner = game.getMinigameService().playMorra(passer, opponent);

        if (winner == null) {
            // Pareggio => posizioni inalterate
            game.getLogger().logAction("La sfida Morra finisce in pareggio! Posizioni inalterate.");
        } else {
            // Se c'è un vincitore, scambiamo le posizioni in classifica?
            // Le specifiche dicono: “In caso di pareggi i posti rimarranno invariati”.
            // Non dice esattamente di scambiare posizioni in classifica, ma potremmo:
            swapPositionsIfNeeded(game, passer, opponent, winner);
        }
    }

    private Player findOpponent(Game game, Player passer) {
        // Ordiniamo i giocatori in base alla loro position (crescente).
        List<Player> sorted = game.getPlayers().stream()
            .sorted(Comparator.comparingInt(Player::getPosition))
            .collect(Collectors.toList());

        Player first = sorted.get(sorted.size() - 1); 
        Player last  = sorted.get(0);

        // Se passer è l'ultimo, sfida il primo. Altrimenti sfida l'ultimo.
        if (passer.getPosition() <= last.getPosition()) {
            return first;
        } else {
            return last;
        }
    }

    private void swapPositionsIfNeeded(Game game, Player p1, Player p2, Player winner) {
        // Esempio: scambiamo le posizioni di p1 e p2 se il winner è quello che sta dietro
        // Nelle specifiche non è 100% chiaro, c'è scritto “In caso di pareggi, posizioni inalterate”
        // Quindi immaginiamo che se c'è un vincitore, scambiano la posizione:
        if (winner == p1) {
            game.getLogger().logAction(p1.getName() + " vince e scambia la posizione con " + p2.getName());
            int tmp = p1.getPosition();
            p1.setPosition(p2.getPosition());
            p2.setPosition(tmp);
        } else {
            game.getLogger().logAction(p2.getName() + " vince e scambia la posizione con " + p1.getName());
            int tmp = p2.getPosition();
            p2.setPosition(p1.getPosition());
            p1.setPosition(tmp);
        }
    }
}

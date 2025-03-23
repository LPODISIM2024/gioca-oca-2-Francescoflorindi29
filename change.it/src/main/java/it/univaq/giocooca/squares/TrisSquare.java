package it.univaq.giocooca.squares;

import it.univaq.giocooca.Game;
import it.univaq.giocooca.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casella Tris: se il passante non è primo, sfida il primo;
 * se il passante è primo, sfida l'ultimo.
 * Risultato:
 * - Se passante NON primo vince => scambia posizioni con il primo.
 * - Se passante NON primo perde o pareggia => nessun cambio.
 * - Se passante è primo e vince => nessun cambio (resta primo).
 * - Se passante è primo e perde => scambia posizioni con l'ultimo.
 * - Se c'è pareggio => nessun cambio di posizioni.
 */
public class TrisSquare extends Square {
    private static final long serialVersionUID = 1L;

    public TrisSquare(int index) {
        super(index);
    }

    @Override
    public void onLanding(Game game, Player passer) {
        // Ordiniamo i giocatori in base alla loro position (crescente)
        List<Player> sorted = game.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getPosition))
                .collect(Collectors.toList());

        Player first = sorted.get(sorted.size() - 1); // chi ha position più alta
        Player last  = sorted.get(0);                // chi ha position più bassa

        // Verifichiamo se passer è primo
        boolean isPasserFirst = (passer.getPosition() >= first.getPosition());

        // Determiniamo l'avversario
        Player opponent;
        if (isPasserFirst) {
            // se passer è primo, sfida l'ultimo
            opponent = last;
            game.getLogger().logAction(
                passer.getName() + " (PRIMO) sfida " + opponent.getName() + " (ULTIMO) a Tris."
            );
            System.out.println(passer.getName() + " (primo) sfida " 
                + opponent.getName() + " (ultimo) a TRIS!");
        } else {
            // se passer non è primo, sfida il primo
            opponent = first;
            game.getLogger().logAction(
                passer.getName() + " (NON primo) sfida il primo (" 
                + opponent.getName() + ") a Tris."
            );
            System.out.println(passer.getName() + " (non primo) sfida " 
                + opponent.getName() + " (primo) a TRIS!");
        }

        // Avviamo il minigioco
        Player winner = game.getMinigameService().playTris(passer, opponent);

        // Se c'è pareggio => winner == null => nessun cambio
        if (winner == null) {
            System.out.println("Tris finisce in pareggio: posizioni inalterate!");
            game.getLogger().logAction("Tris: pareggio, nessun cambio di posizioni.");
            return;
        }

        // C'è un vincitore
        if (!isPasserFirst) {
            // Passante NON primo
            if (winner == passer) {
                // Vittoria del passante => scambia col primo
                System.out.println(passer.getName() + " vince e scambia posizione col primo!");
                game.getLogger().logAction(
                    "Tris: passante vince e scambia posizioni col primo.");
                swapPositions(passer, opponent);
            } else {
                // passante perde => nessun cambio
                System.out.println(passer.getName() + " perde, nessun cambio di posizioni.");
                game.getLogger().logAction("Tris: passante perde, nessun cambio.");
            }
        } else {
            // Passante è primo
            if (winner == passer) {
                // primo vince => resta primo
                System.out.println("Il primo (" + passer.getName() + ") vince e resta primo!");
                game.getLogger().logAction("Tris: primo vince, nessun cambio di posizioni.");
            } else {
                // primo perde => swap con l'ultimo
                System.out.println("Il primo perde! L'ultimo prende il suo posto => scambio posizioni.");
                game.getLogger().logAction("Tris: primo perde, scambio posizioni con l'ultimo.");
                swapPositions(passer, opponent);
            }
        }
    }

    /**
     * Utility: scambia la position di due giocatori
     */
    private void swapPositions(Player p1, Player p2) {
        int tmp = p1.getPosition();
        p1.setPosition(p2.getPosition());
        p2.setPosition(tmp);
    }
}

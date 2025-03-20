package it.univaq.giocooca.minigame;

import it.univaq.giocooca.Player;

public interface IMinigameService {
    /**
     * Gioca a Morra (sasso-carta-forbice) al meglio di 3.
     * Ritorna il vincitore, o null se pareggio.
     */
    Player playMorra(Player p1, Player p2);

    /**
     * Gioca a Tris al meglio di 3 (demo). Ritorna il vincitore o null se pareggio.
     */
    Player playTris(Player p1, Player p2);
}

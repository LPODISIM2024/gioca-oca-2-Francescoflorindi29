package it.univaq.giocooca.minigame;

import it.univaq.giocooca.Player;


public interface IMinigameService {

    Player playMorra(Player p1, Player p2);

    Player playTris(Player p1, Player p2);
}

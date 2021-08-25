package com.ags.simplenpcs.api;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SimpleNPCService {

    public void createNPC(Profile p, Location l);

    public void deleteNPC(NPC n);

    public void hideNPCFromPlayer(NPC n, Player p);

    public void showNPCToPlayer(NPC n, Player p);

}

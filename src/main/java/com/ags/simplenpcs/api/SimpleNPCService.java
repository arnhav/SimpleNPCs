package com.ags.simplenpcs.api;

import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SimpleNPCService {

    boolean isFinishedLoading();

    NPC getNPC(int id);

    int getID(NPC npc);

    SNPC getSNPC(int id);

    NPC createNPC(Profile p, Location l);

    void deleteNPC(NPC n);

    void hideNPCFromPlayer(NPC n, Player p);

    void showNPCToPlayer(NPC n, Player p);

}

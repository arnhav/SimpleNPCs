package com.ags.simplenpcs.api;

import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SimpleNPCService {

    public NPC getNPC(int id);

    public int getID(NPC npc);

    public SNPC getSNPC(int id);

    public NPC createNPC(Profile p, Location l);

    public void deleteNPC(NPC n);

    public void hideNPCFromPlayer(NPC n, Player p);

    public void showNPCToPlayer(NPC n, Player p);

}

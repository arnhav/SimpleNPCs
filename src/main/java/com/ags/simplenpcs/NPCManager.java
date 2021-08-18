package com.ags.simplenpcs;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class NPCManager {

    private final NPCPool npcPool;

    public NPCManager(JavaPlugin plugin) {
        npcPool = NPCPool.builder(plugin)
                .spawnDistance(50)
                .tabListRemoveTicks(20)
                .build();
    }

    public void addNPC(Location location, Profile profile){
        NPC npc = NPC.builder()
                .profile(profile)
                .location(location)
                .lookAtPlayer(true)
                .build(npcPool);
    }

    public void removeNPC(NPC npc){
        npcPool.removeNPC(npc.getEntityId());
    }

    public void hideNPCForPlayer(NPC npc, Player player){
        npc.addExcludedPlayer(player);
    }

    public void showNPCForPlayer(NPC npc, Player player){
        npc.removeExcludedPlayer(player);
    }

    public Profile createProfile(String playerName, String customName){
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        if (uuid == null) uuid = UUID.randomUUID();
        Profile profile = new Profile(uuid);
        profile.complete();
        profile.setName(customName);
        profile.setUniqueId(UUID.randomUUID());
        return profile;
    }
}

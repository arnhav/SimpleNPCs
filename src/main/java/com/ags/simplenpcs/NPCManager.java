package com.ags.simplenpcs;

import com.ags.simplenpcs.data.FileManager;
import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.profile.Profile;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class NPCManager {

    private static NPCPool npcPool;

    public static BiMap<NPC, Integer> npcs = HashBiMap.create();
    public static HashMap<Integer, SNPC> snpcs = new HashMap<>();
    public static WeakHashMap<Player, NPC> selectedNPC = new WeakHashMap<>();

    public NPCManager(JavaPlugin plugin) {
        npcPool = NPCPool.builder(plugin)
                .spawnDistance(60)
                .actionDistance(30)
                .tabListRemoveTicks(20)
                .build();
    }

    public void addNPC(Location location, Profile profile){
        NPC npc= NPC.builder()
            .profile(profile)
            .location(location)
            .imitatePlayer(false)
            .lookAtPlayer(false)
            .build(npcPool);
        SNPC snpc = new SNPC();
        npcs.put(npc, FileManager.lastNPCID++);
        FileManager.saveNPC(FileManager.lastNPCID++, npc, snpc);
    }

    public static NPC spawnNPC(Location location, Profile profile){
        NPC npc= NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(npcPool);
        SNPC snpc = new SNPC();
        npcs.put(npc, FileManager.lastNPCID++);
        return npc;
    }

    public static NPC spawnNPC(Location location, Profile profile, int id, SNPC snpc){
        NPC npc= NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(npcPool);
        npcs.put(npc, id);
        snpcs.put(id, snpc);
        return npc;
    }

    public void removeNPC(NPC npc, boolean full){
        Integer id = npcs.get(npc);
        npcPool.removeNPC(npc.getEntityId());
        if (!full) return;
        FileManager.removeNPC(id);
        npcs.remove(npc);
    }

    public void removeNPCs(){
        npcPool.getNPCs().forEach(npc -> removeNPC(npc, false));
    }

    public void hideNPCForPlayer(NPC npc, Player player){
        npc.addExcludedPlayer(player);
    }

    public void showNPCForPlayer(NPC npc, Player player){
        npc.removeExcludedPlayer(player);
    }

    public Profile createProfile(String playerName, String customName){
        Random random = new Random();
        Profile profile = new Profile(playerName);
        profile.complete();
        profile.setName(customName);
        profile.setUniqueId(new UUID(random.nextLong(), 0));
        return profile;
    }

    public static Profile createProfile(String playerName, List<Profile.Property> list){
        Random random = new Random();
        Profile profile = new Profile(playerName, list);
        profile.complete(false);
        profile.setUniqueId(new UUID(random.nextLong(), 0));
        return profile;
    }
}

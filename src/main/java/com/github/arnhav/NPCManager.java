package com.github.arnhav;

import com.github.arnhav.objects.SNPC;
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

    private NPCPool npcPool;

    private int lastNPCID = -1;

    public BiMap<NPC, Integer> npcs = HashBiMap.create();
    public HashMap<Integer, SNPC> snpcs = new HashMap<>();
    public WeakHashMap<Player, NPC> selectedNPC = new WeakHashMap<>();

    public NPCManager(JavaPlugin plugin) {
        npcPool = NPCPool.builder(plugin)
                .spawnDistance(50)
                .actionDistance(25)
                .tabListRemoveTicks(10)
                .build();
    }

    public void setLastNPCID(int lastNPCID) {
        this.lastNPCID = lastNPCID;
    }

    public int getLastNPCID() {
        return lastNPCID;
    }

    public NPC addNPC(Location location, Profile profile) {
        NPC npc = NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(npcPool);
        SNPC snpc = new SNPC();
        lastNPCID += 1;
        int id = lastNPCID;
        npcs.put(npc, id);
        snpcs.put(id, snpc);
        return npc;
    }

    // Used by the API
    public NPC spawnNPC(Location location, Profile profile) {
        NPC npc = NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(npcPool);
        SNPC snpc = new SNPC();
        lastNPCID += 1;
        int id = lastNPCID;
        npcs.put(npc, id);
        snpcs.put(id, snpc);
        return npc;
    }

    // Used by internal stuff like commands and loading from the file
    public NPC spawnNPC(Location location, Profile profile, int id, SNPC snpc) {
        NPC npc = NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(npcPool);
        npcs.put(npc, id);
        snpcs.put(id, snpc);
        if (id > lastNPCID) lastNPCID = id;
        return npc;
    }

    public void removeNPC(NPC npc, boolean full) {
        Integer id = npcs.get(npc);
        npcPool.removeNPC(npc.getEntityId());
        if (!full) return;
        npcs.remove(npc);
        snpcs.remove(id);
    }

    public void removeNPCs(boolean full) {
        npcPool.getNPCs().forEach(npc -> removeNPC(npc, full));
    }

    public void hideNPCForPlayer(NPC npc, Player player) {
        npc.addExcludedPlayer(player);
    }

    public void showNPCForPlayer(NPC npc, Player player) {
        npc.removeExcludedPlayer(player);
    }

    public Profile createProfile(String playerName, String customName) {
        Random random = new Random();
        Profile profile = new Profile(playerName);
        profile.complete();
        profile.setName(customName);
        profile.setUniqueId(new UUID(random.nextLong(), 0));
        return profile;
    }

    public Profile createProfile(String playerName, List<Profile.Property> list) {
        Random random = new Random();
        Profile profile = new Profile(playerName, list);
        profile.complete(false);
        profile.setUniqueId(new UUID(random.nextLong(), 0));
        return profile;
    }

    public BiMap<NPC, Integer> getNpcs() {
        return npcs;
    }

    public HashMap<Integer, SNPC> getSnpcs() {
        return snpcs;
    }

    public WeakHashMap<Player, NPC> getSelectedNPC() {
        return selectedNPC;
    }
}

package com.ags.simplenpcs;

import com.ags.simplenpcs.data.FileManager;
import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;

public class NPCManager {

    private static NPCPool npcPool;

    public static HashMap<NPC, SNPC> npcs = new HashMap<>();
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
            .lookAtPlayer(true)
            .build(npcPool);
        npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();
        SNPC snpc = new SNPC(npcs.size());
        npcs.put(npc, snpc);
        FileManager.saveNPC(npc, snpc);
    }

    public static NPC spawnNPC(Location location, Profile profile){
        NPC npc= NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(true)
                .build(npcPool);
        npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();
        SNPC snpc = new SNPC(npcs.size());
        npcs.put(npc, snpc);
        return npc;
    }

    public static NPC spawnNPC(Location location, Profile profile, SNPC snpc){
        NPC npc= NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(true)
                .build(npcPool);
        npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();
        npcs.put(npc, snpc);
        return npc;
    }

    public void removeNPC(NPC npc){
        SNPC snpc = npcs.get(npc);
        npcPool.removeNPC(npc.getEntityId());
        FileManager.removeNPC(snpc);
        npcs.remove(npc);
    }

    public void removeNPCs(){
        npcPool.getNPCs().forEach(this::removeNPC);
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

    public static Profile createProfile(String playerName){
        Random random = new Random();
        Profile profile = new Profile(playerName);
        profile.complete();
        profile.setUniqueId(new UUID(random.nextLong(), 0));
        return profile;
    }
}

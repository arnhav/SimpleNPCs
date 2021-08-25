package com.ags.simplenpcs;

import com.ags.simplenpcs.data.FileManager;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;

public class NPCManager {

    private final NPCPool npcPool;

    private final Random random;

    public static WeakHashMap<Player, NPC> selectedNPC = new WeakHashMap<>();

    public NPCManager(JavaPlugin plugin) {
        npcPool = NPCPool.builder(plugin)
                .spawnDistance(60)
                .actionDistance(30)
                .tabListRemoveTicks(20)
                .build();

        this.random = new Random();
    }

    public void addNPC(Location location, Profile profile){
        NPC npc= NPC.builder()
            .profile(profile)
            .location(location)
            .imitatePlayer(false)
            .lookAtPlayer(true)
            .build(this.npcPool);
        npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();

        FileManager.saveNPC(npc);
    }

    public void removeNPC(NPC npc){
        npcPool.removeNPC(npc.getEntityId());
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
        Profile profile = new Profile(playerName);
        profile.complete();
        profile.setName(customName);
        profile.setUniqueId(new UUID(this.random.nextLong(), 0));
        return profile;
    }

    public Profile createProfile(String playerName){
        Profile profile = new Profile(playerName);
        profile.complete();
        return profile;
    }
}

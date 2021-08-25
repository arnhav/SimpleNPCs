package com.ags.simplenpcs;

import com.ags.simplenpcs.api.SimpleNPCService;
import com.ags.simplenpcs.commands.CommandHandler;
import com.ags.simplenpcs.data.FileManager;
import com.ags.simplenpcs.listeners.NPCListener;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleNPCs extends JavaPlugin implements SimpleNPCService {

    private NPCManager npcManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        npcManager = new NPCManager(this);

        getCommand("snpc").setExecutor(new CommandHandler(npcManager));

        getServer().getPluginManager().registerEvents(new NPCListener(), this);

        new FileManager(this);

        getServer().getServicesManager().register(SimpleNPCService.class, this, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        npcManager.removeNPCs();
    }

    public static SimpleNPCs instance(){
        return SimpleNPCs.getPlugin(SimpleNPCs.class);
    }

    @Override
    public void createNPC(Profile p, Location l) {
        npcManager.addNPC(l, p);
    }

    @Override
    public void deleteNPC(NPC n) {
        npcManager.removeNPC(n);
    }

    @Override
    public void hideNPCFromPlayer(NPC n, Player p) {
        npcManager.hideNPCForPlayer(n, p);
    }

    @Override
    public void showNPCToPlayer(NPC n, Player p) {
        npcManager.showNPCForPlayer(n, p);
    }
}

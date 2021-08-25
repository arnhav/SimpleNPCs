package com.ags.simplenpcs;

import com.ags.simplenpcs.api.SimpleNPCService;
import com.ags.simplenpcs.commands.CommandHandler;
import com.ags.simplenpcs.data.FileManager;
import com.ags.simplenpcs.listeners.NPCListener;
import org.bukkit.Location;
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
    public void createNPC(Location l) {

    }
}

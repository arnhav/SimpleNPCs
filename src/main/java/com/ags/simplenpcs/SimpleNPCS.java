package com.ags.simplenpcs;

import com.ags.simplenpcs.api.SimpleNPCService;
import org.bukkit.Location;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleNPCS extends JavaPlugin implements SimpleNPCService {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new NPCManager(this);

        getServer().getServicesManager().register(SimpleNPCService.class, this, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SimpleNPCS instance(){
        return SimpleNPCS.getPlugin(SimpleNPCS.class);
    }

    @Override
    public void createNPC(Location l) {

    }
}

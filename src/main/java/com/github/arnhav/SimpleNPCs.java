package com.github.arnhav;

import com.github.arnhav.api.SimpleNPCService;
import com.github.arnhav.commands.CommandHandler;
import com.github.arnhav.data.FileManager;
import com.github.arnhav.listeners.NPCListener;
import com.github.arnhav.objects.SNPC;
import com.github.arnhav.util.NPCUtil;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleNPCs extends JavaPlugin implements SimpleNPCService {

    private NPCManager npcManager;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        npcManager = new NPCManager(this);
        fileManager = new FileManager(this, npcManager);

        getCommand("snpc").setExecutor(new CommandHandler(npcManager, fileManager));

        getServer().getPluginManager().registerEvents(new NPCListener(npcManager), this);

        getServer().getServicesManager().register(SimpleNPCService.class, this, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        fileManager.updateLastNPCID(npcManager.getLastNPCID());
    }

    public static SimpleNPCs instance() {
        return SimpleNPCs.getPlugin(SimpleNPCs.class);
    }

    public static NPCManager npcManager() {
        return instance().npcManager;
    }

    public static FileManager fileManager() {
        return instance().fileManager;
    }

    @Override
    public boolean isFinishedLoading() {
        return fileManager.isFinishedLoading();
    }

    @Override
    public NPC getNPC(int id) {
        return npcManager.getNpcs().inverse().get(id);
    }

    @Override
    public int getID(NPC npc) {
        return npcManager.getNpcs().get(npc);
    }

    @Override
    public SNPC getSNPC(int id) {
        return npcManager.getSnpcs().get(id);
    }

    @Override
    public NPC createNPC(Profile p, Location l) {
        NPC npc = npcManager.spawnNPC(l, p);
        int id = getID(npc);
        SNPC snpc = getSNPC(id);
        fileManager.saveNPC(id, npc, snpc, true);
        return npc;
    }

    @Override
    public void deleteNPC(NPC n) {
        int id = getID(n);
        npcManager.removeNPC(n, true);
        fileManager.removeNPC(id);
    }

    @Override
    public void hideNPCFromPlayer(NPC n, Player p) {
        npcManager.hideNPCForPlayer(n, p);
    }

    @Override
    public void showNPCToPlayer(NPC n, Player p) {
        npcManager.showNPCForPlayer(n, p);
    }

    @Override
    public void teleportToLocation(NPC n, Location l) {
        NPCUtil.teleportToLocation(n, l);
    }

    @Override
    public void setLook(NPC n, Location l) {
        NPCUtil.setLook(n, l);
    }

    @Override
    public void toggleLook(NPC n) {
        NPCUtil.toggleLook(n);
    }

    @Override
    public void toggleImitate(NPC n) {
        NPCUtil.toggleImitate(n);
    }
}

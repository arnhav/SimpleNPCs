package com.ags.simplenpcs.data;

import com.github.juliarn.npc.NPC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FileManager {

    private JavaPlugin plugin;
    private static File npcFile;

    public FileManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createConfig();
        createNPCsFile();
    }

    private void createConfig() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            File file = new File(plugin.getDataFolder(), "config.yml");
            if (!file.exists()) {
                plugin.getLogger().info("config.yml not found, creating!");
                plugin.saveDefaultConfig();
            } else {
                plugin.getLogger().info("config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNPCsFile(){
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            npcFile = new File(plugin.getDataFolder(), "npcs.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
            if (!npcFile.exists()) {
                plugin.getLogger().info("npcs.yml not found, creating!");
                fc.createSection("npc");
                fc.save(npcFile);
            } else {
                plugin.getLogger().info("npcs.yml found, loading!");
            }
            loadNPCs(npcFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNPCs(File file){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection cs = fc.getConfigurationSection("npc");
        if (cs == null) return;
        for (String p : cs.getKeys(false)){
            // TODO: Do this once you figure out how properties are stored in profiles
        }
    }

    public static void saveNPC(NPC npc){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
        // TODO: Save profile info based on what properties look like
        fc.set(npc.getEntityId()+".loc.x", npc.getLocation().getX());
        fc.set(npc.getEntityId()+".loc.y", npc.getLocation().getY());
        fc.set(npc.getEntityId()+".loc.z", npc.getLocation().getZ());
        fc.set(npc.getEntityId()+".loc.u", npc.getLocation().getYaw());
        fc.set(npc.getEntityId()+".loc.w", npc.getLocation().getWorld().getName());
        saveFile(fc, npcFile);
    }

    private static void saveFile(FileConfiguration fc, File file){
        try {
            fc.save(file);
        } catch (Exception e) {

        }
    }
}

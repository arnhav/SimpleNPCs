package com.ags.simplenpcs.data;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
            int id = Integer.parseInt(p);
            SNPC snpc = new SNPC(id);

            String name = cs.getString(p+".name");

            Profile profile = NPCManager.createProfile("Notch");
            profile.setName(name);

            ConfigurationSection pcs = cs.getConfigurationSection(p+".properties");
            if (pcs == null) continue;
            for (String ps : pcs.getKeys(false)){
                String value = pcs.getString(ps+".value");
                String signature = pcs.getString(ps+".signature");
                if (value == null || signature == null) continue;
                Profile.Property property = new Profile.Property(ps, value, signature);
                profile.setProperty(property);
            }

            double x = cs.getDouble(p+".loc.x");
            double y = cs.getDouble(p+".loc.y");
            double z = cs.getDouble(p+".loc.z");
            float yaw = (float) cs.getDouble(p+".loc.u");
            float pitch = (float) cs.getDouble(p+".loc.p");
            String worldName = cs.getString(p+".loc.w");
            if (worldName == null) continue;
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            Location location = new Location(world, x, y, z, yaw, pitch);

            NPCManager.spawnNPC(location, profile, snpc);
        }
    }

    public static void saveNPC(NPC npc, SNPC snpc){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
        fc.set("npc."+snpc.getId()+".name", npc.getProfile().getName());
        for (Profile.Property p : npc.getProfile().getProperties()){
            fc.set("npc."+snpc.getId()+".properties."+p.getName()+".value", p.getValue());
            fc.set("npc."+snpc.getId()+".properties."+p.getName()+".signature", p.getSignature());
        }
        fc.set("npc."+snpc.getId()+".loc.x", npc.getLocation().getX());
        fc.set("npc."+snpc.getId()+".loc.y", npc.getLocation().getY());
        fc.set("npc."+snpc.getId()+".loc.z", npc.getLocation().getZ());
        fc.set("npc."+snpc.getId()+".loc.u", npc.getLocation().getYaw());
        fc.set("npc."+snpc.getId()+".loc.p", npc.getLocation().getPitch());
        fc.set("npc."+snpc.getId()+".loc.w", npc.getLocation().getWorld().getName());
        saveFile(fc, npcFile);
    }

    public static void removeNPC(SNPC snpc){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
        fc.set("npc."+snpc.getId(), null);
        saveFile(fc, npcFile);
    }

    private static void saveFile(FileConfiguration fc, File file){
        try {
            fc.save(file);
        } catch (Exception e) {

        }
    }
}

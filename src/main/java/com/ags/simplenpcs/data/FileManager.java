package com.ags.simplenpcs.data;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.SimpleNPCs;
import com.ags.simplenpcs.objects.NPCEquipmentSlot;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManager {

    private JavaPlugin plugin;
    private NPCManager npcManager;

    private File npcFile;

    public FileManager(JavaPlugin plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
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
            loadConfigFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfigFile() {
        FileConfiguration fc = plugin.getConfig();
        int lastNPCID = fc.getInt("lastNPCID");
        npcManager.setLastNPCID(lastNPCID);
    }

    public void updateLastNPCID(int lastNPCID) {
        FileConfiguration fc = plugin.getConfig();
        fc.set("lastNPCID", lastNPCID);
        plugin.saveConfig();
    }

    private void createNPCsFile() {
        try {
            npcFile = new File(plugin.getDataFolder(), "npcs.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
            if (!npcFile.exists()) {
                plugin.getLogger().info("npcs.yml not found, creating!");
                fc.createSection("npc");
                fc.save(npcFile);
            } else {
                plugin.getLogger().info("npcs.yml found, loading!");
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> loadNPCs(npcFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNPCs(File file) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection cs = fc.getConfigurationSection("npc");
        if (cs == null) return;
        for (String p : cs.getKeys(false)) {
            int id = Integer.parseInt(p);
            SNPC snpc = new SNPC();

            String name = cs.getString(p + ".name");
            boolean look = cs.getBoolean(p + ".look");
            boolean imitate = cs.getBoolean(p + ".imitate");

            ConfigurationSection tcs = cs.getConfigurationSection(p + ".properties");
            if (tcs == null) continue;
            List<Profile.Property> properties = new ArrayList<>();
            for (String ts : tcs.getKeys(false)) {
                String value = tcs.getString(ts + ".value");
                String signature = tcs.getString(ts + ".signature");
                if (value == null || signature == null) continue;
                Profile.Property property = new Profile.Property(ts, value, signature);
                properties.add(property);
            }

            Profile profile = npcManager.createProfile(name == null ? "NPC" : name, properties);

            tcs = cs.getConfigurationSection(p + ".equipment");
            if (tcs != null) {
                for (String ts : tcs.getKeys(false)) {
                    NPCEquipmentSlot nes = NPCEquipmentSlot.valueOf(ts);
                    String ti = tcs.getString(ts);
                    snpc.addEquipment(nes, ti);
                }
            }

            double x = cs.getDouble(p + ".loc.x");
            double y = cs.getDouble(p + ".loc.y");
            double z = cs.getDouble(p + ".loc.z");
            float yaw = (float) cs.getDouble(p + ".loc.u");
            float pitch = (float) cs.getDouble(p + ".loc.p");
            String worldName = cs.getString(p + ".loc.w");
            if (worldName == null) continue;
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            Location location = new Location(world, x, y, z, yaw, pitch);

            NPC npc = npcManager.spawnNPC(location, profile, id, snpc);

            npc.setLookAtPlayer(look);
            npc.setImitatePlayer(imitate);
        }
        plugin.getLogger().info("Loaded "+ npcManager.getNpcs().size() + " NPCs!");
    }

    public void loadCitizensFile() {
        File file = new File(SimpleNPCs.instance().getDataFolder(), "saves.yml");
        if (!file.exists()) return;
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        int lastNPCID = fc.getInt("last-created-npc-id");
        npcManager.setLastNPCID(lastNPCID);
        ConfigurationSection cs = fc.getConfigurationSection("npc");
        if (cs == null) return;
        for (String p : cs.getKeys(false)) {
            int id = Integer.parseInt(p);
            SNPC snpc = new SNPC();

            String name = cs.getString(p + ".name");

            double x = Double.parseDouble(cs.getString(p+".traits.location.x"));
            double y = Double.parseDouble(cs.getString(p+".traits.location.y"));
            double z = Double.parseDouble(cs.getString(p+".traits.location.z"));
            float yaw = Float.parseFloat(cs.getString(p+".traits.location.yaw"));
            float pitch = Float.parseFloat(cs.getString(p+".traits.location.pitch"));
            String worldName = cs.getString(p+".traits.location.world");
            if (worldName == null) continue;
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            Location location = new Location(world, x, y, z, yaw, pitch);

            boolean look = cs.getBoolean(p + ".traits.lookclose.enabled");

            String value = cs.getString(p + ".traits.skintrait.textureRaw");
            String signature = cs.getString(p + ".traits.skintrait.signature");

            if (value == null || signature == null) continue;

            Profile.Property property = new Profile.Property("textures", value, signature);
            Profile profile = npcManager.createProfile(name == null ? "NPC" : name, Collections.singletonList(property));

            NPC npc = npcManager.spawnNPC(location, profile, id, snpc);
            npc.setLookAtPlayer(look);

            saveNPC(id, npc, snpc, true);
        }

        plugin.getLogger().info("Loaded "+ npcManager.getNpcs().size() + " NPCs!");
    }

    public void saveNPC(int id, NPC npc, SNPC snpc, boolean update) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
        fc.set("npc." + id + ".name", npc.getProfile().getName());
        fc.set("npc." + id + ".look", npc.isLookAtPlayer());
        fc.set("npc." + id + ".imitate", npc.isImitatePlayer());
        for (Profile.Property p : npc.getProfile().getProperties()) {
            fc.set("npc." + id + ".properties." + p.getName() + ".value", p.getValue());
            fc.set("npc." + id + ".properties." + p.getName() + ".signature", p.getSignature());
        }
        for (NPCEquipmentSlot nes : snpc.getEquipment().keySet()) {
            String is = snpc.getEquipment().get(nes);
            fc.set("npc." + id + ".equipment." + nes.toString(), is);
        }
        fc.set("npc." + id + ".loc.x", npc.getLocation().getX());
        fc.set("npc." + id + ".loc.y", npc.getLocation().getY());
        fc.set("npc." + id + ".loc.z", npc.getLocation().getZ());
        fc.set("npc." + id + ".loc.u", npc.getLocation().getYaw());
        fc.set("npc." + id + ".loc.p", npc.getLocation().getPitch());
        fc.set("npc." + id + ".loc.w", npc.getLocation().getWorld().getName());
        saveFile(fc, npcFile);

        if (!update) return;
        npcManager.getNpcs().put(npc, id);
        npcManager.getSnpcs().put(id, snpc);
    }

    public void removeNPC(int id) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
        fc.set("npc." + id, null);
        saveFile(fc, npcFile);
    }

    private void saveFile(FileConfiguration fc, File file) {
        try {
            fc.save(file);
        } catch (Exception e) {

        }
    }
}

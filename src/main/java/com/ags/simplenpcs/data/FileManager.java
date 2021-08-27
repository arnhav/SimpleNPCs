package com.ags.simplenpcs.data;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.SimpleNPCs;
import com.ags.simplenpcs.objects.NPCEquipmentSlot;
import com.ags.simplenpcs.objects.SNPC;
import com.ags.simplenpcs.util.ItemUtils;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            npcFile = new File(plugin.getDataFolder(), "npcs.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
            if (!npcFile.exists()) {
                plugin.getLogger().info("npcs.yml not found, creating!");
                fc.createSection("npc");
                fc.save(npcFile);
            } else {
                plugin.getLogger().info("npcs.yml found, loading!");
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> loadNPCs(npcFile));
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
            boolean look = cs.getBoolean(p+".look");
            boolean imitate = cs.getBoolean(p+".imitate");

            ConfigurationSection tcs = cs.getConfigurationSection(p+".properties");
            if (tcs == null) continue;
            List<Profile.Property> properties = new ArrayList<>();
            for (String ts : tcs.getKeys(false)){
                String value = tcs.getString(ts+".value");
                String signature = tcs.getString(ts+".signature");
                if (value == null || signature == null) continue;
                Profile.Property property = new Profile.Property(ts, value, signature);
                properties.add(property);
            }

            Profile profile = NPCManager.createProfile(name==null?"NPC":name, properties);

            tcs = cs.getConfigurationSection(p+".equipment");
            if (tcs != null){
                for (String ts : tcs.getKeys(false)){
                    NPCEquipmentSlot nes = NPCEquipmentSlot.valueOf(ts);
                    String ti = tcs.getString(ts);
                    ItemStack is = ItemUtils.stringToItem(ti);

                    snpc.addEquipment(nes, is);
                }
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

            NPC npc = NPCManager.spawnNPC(location, profile, snpc);

            npc.setLookAtPlayer(look);
            npc.setImitatePlayer(imitate);

            for (NPCEquipmentSlot nes : snpc.getEquipment().keySet()){
                npc.equipment().queue(nes.getIndex(), snpc.getEquipment().get(nes)).send();
            }
        }
    }

    public static void loadCitizensFile(){
        File file = new File(SimpleNPCs.instance().getDataFolder(), "saves.yml");
        if (!file.exists()) return;
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection cs = fc.getConfigurationSection("npc");
        if (cs == null) return;
        for (String p : cs.getKeys(false)){
            int id = Integer.parseInt(p);
            SNPC snpc = new SNPC(id);

            String name = cs.getString(p+".name");

            double x = cs.getDouble(p+".traits.location.x");
            double y = cs.getDouble(p+".traits.location.y");
            double z = cs.getDouble(p+".traits.location.z");
            float yaw = (float) cs.getDouble(p+".traits.location.yaw");
            float pitch = (float) cs.getDouble(p+".traits.location.pitch");
            String worldName = cs.getString(p+".traits.location.world");
            if (worldName == null) continue;
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            Location location = new Location(world, x, y, z, yaw, pitch);

            boolean look = cs.getBoolean(p+".traits.lookclose.enabled");

            String value = cs.getString(p+".traits.skintrait.textureRaw");
            String signature = cs.getString(p+".traits.skintrait.signature");

            if (value == null || signature == null) continue;

            Profile.Property property = new Profile.Property("textures", value, signature);
            Profile profile = NPCManager.createProfile(name==null?"NPC":name, Collections.singletonList(property));

            NPC npc = NPCManager.spawnNPC(location, profile, snpc);
            npc.setLookAtPlayer(look);

            saveNPC(npc, snpc);
        }
    }

    public static void saveNPC(NPC npc, SNPC snpc){
        FileConfiguration fc = YamlConfiguration.loadConfiguration(npcFile);
        fc.set("npc."+snpc.getId()+".name", npc.getProfile().getName());
        fc.set("npc."+snpc.getId()+".look", npc.isLookAtPlayer());
        fc.set("npc."+snpc.getId()+".imitate", npc.isImitatePlayer());
        for (Profile.Property p : npc.getProfile().getProperties()){
            fc.set("npc."+snpc.getId()+".properties."+p.getName()+".value", p.getValue());
            fc.set("npc."+snpc.getId()+".properties."+p.getName()+".signature", p.getSignature());
        }
        for (NPCEquipmentSlot nes : snpc.getEquipment().keySet()){
            ItemStack is = snpc.getEquipment().get(nes);
            fc.set("npc."+snpc.getId()+".equipment."+nes.toString(), ItemUtils.itemToString(is));
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

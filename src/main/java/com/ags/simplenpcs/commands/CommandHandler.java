package com.ags.simplenpcs.commands;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.SimpleNPCs;
import com.ags.simplenpcs.data.FileManager;
import com.ags.simplenpcs.objects.NPCEquipmentSlot;
import com.ags.simplenpcs.objects.SNPC;
import com.ags.simplenpcs.util.ItemUtils;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandHandler implements CommandExecutor {

    private NPCManager npcManager;
    private FileManager fileManager;

    public CommandHandler(NPCManager npcManager, FileManager fileManager){
        this.npcManager = npcManager;
        this.fileManager = fileManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        if (!(sender instanceof Player)) return false;

        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, "snpc help");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(
                    "SimpleNPCS Help:",
                    "/snpc help",
                    "/snpc reload",
                    "/snpc create <playerSkinName> <NPCname>",
                    "/snpc delete",
                    "/snpc setskin <playerSkinName>",
                    "/snpc setequipment <equipmentSlot>",
                    "/snpc tphere",
                    "/snpc look",
                    "/snpc imitate",
                    "/snpc tp",
                    "/snpc sel <id>",
                    "/snpc info"
            );
        }

        if (args[0].equalsIgnoreCase("reload")){
            npcManager.removeNPCs(true);
            fileManager = new FileManager(SimpleNPCs.instance(), npcManager);
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 3) return false;
            StringBuilder npcName = new StringBuilder();
            for (int i = 2; i < args.length; i++){
                npcName.append(args[i]);
                if (i != args.length-1) npcName.append(" ");
            }
            Profile profile = npcManager.createProfile(args[1], npcName.toString());
            NPC npc = npcManager.addNPC(((Player) sender).getLocation(), profile);
            int id = npcManager.getNpcs().get(npc);
            SNPC snpc = npcManager.getSnpcs().get(id);
            fileManager.saveNPC(id, npc, snpc, true);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + args[2] + " created!"));
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            npcManager.removeNPC(selected, true);
            fileManager.removeNPC(id);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " deleted."));
        }

        if (args[0].equalsIgnoreCase("setskin")) {
            if (args.length != 2) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            npcManager.removeNPC(selected, true);
            Profile current = selected.getProfile();
            Profile profile = npcManager.createProfile(args[1], current.getName());
            NPC npc = npcManager.spawnNPC(selected.getLocation(), profile, id, snpc);
            npc.setLookAtPlayer(selected.isLookAtPlayer());
            npc.setImitatePlayer(selected.isImitatePlayer());
            fileManager.saveNPC(id, npc, snpc, true);
            npcManager.getSelectedNPC().put((Player) sender, npc);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " skin changed."));
        }

        if (args[0].equalsIgnoreCase("setequipment")) {
            if (args.length != 2) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            try {
                ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand();
                NPCEquipmentSlot nes = NPCEquipmentSlot.valueOf(args[1]);
                snpc.addEquipment(nes, ItemUtils.itemToString(itemStack));

                selected.equipment().queue(nes.getIndex(), itemStack).send();
            } catch (Exception e) {
                return false;
            }
            fileManager.saveNPC(id, selected, snpc, false);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " equipment changed."));
        }

        if (args[0].equalsIgnoreCase("tphere")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            npcManager.removeNPC(selected, true);
            NPC npc = npcManager.spawnNPC(((Player) sender).getLocation(), selected.getProfile(), id, snpc);
            npc.setLookAtPlayer(selected.isLookAtPlayer());
            npc.setImitatePlayer(selected.isImitatePlayer());
            fileManager.saveNPC(id, npc, snpc, true);
            npcManager.getSelectedNPC().put((Player) sender, npc);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " changed location."));
        }

        // TODO: Make this work
        if (args[0].equalsIgnoreCase("setlook")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            Location loc = selected.getLocation();
            float yaw = (float) Math.toDegrees(Math.atan2(((Player) sender).getLocation().getZ() - loc.getZ(), ((Player) sender).getLocation().getX() - loc.getX())) - 90;
            loc.setYaw(yaw);
            npcManager.removeNPC(selected, true);
            NPC npc = npcManager.spawnNPC(loc, selected.getProfile(), id, snpc);
            npc.setLookAtPlayer(selected.isLookAtPlayer());
            npc.setImitatePlayer(selected.isImitatePlayer());
            fileManager.saveNPC(id, npc, snpc, true);
            npcManager.getSelectedNPC().put((Player) sender, npc);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " made to look at you."));
        }

        if (args[0].equalsIgnoreCase("look")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            selected.setLookAtPlayer(!selected.isLookAtPlayer());
            fileManager.saveNPC(id, selected, snpc, false);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " toggled look."));
        }

        if (args[0].equalsIgnoreCase("imitate")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            selected.setImitatePlayer(!selected.isImitatePlayer());
            fileManager.saveNPC(id, selected, snpc, false);
            sender.sendMessage(Component.text(ChatColor.GRAY + "NPC: " + id + " toggled imitate."));
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            ((Player) sender).teleport(selected.getLocation());
        }

        if (args[0].equalsIgnoreCase("sel")) {
            if (args.length != 2) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            Integer id = Integer.parseInt(args[1]);
            NPC npc = npcManager.getNpcs().inverse().get(id);
            if (selected != null && selected.getEntityId() == npc.getEntityId()) return false;
            npcManager.getSelectedNPC().put((Player) sender, npc);
            sender.sendMessage(Component.text(ChatColor.YELLOW+"You have selected NPC: "+id));
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA + "--NPC Info--"));
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA + "ID: " + id));
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA + "Internal ID: " + selected.getEntityId()));
            if (!(sender.getName().equals("Tyrriel") || sender.getName().equals("arnhav"))) return true;
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA + "Properties:"));
            for (Profile.Property pr : selected.getProfile().getProperties()) {
                sender.sendMessage(Component.text(ChatColor.DARK_AQUA + " Name: " + pr.getName()));
                sender.sendMessage(Component.text(ChatColor.DARK_AQUA + " Value: " + pr.getValue()));
                sender.sendMessage(Component.text(ChatColor.DARK_AQUA + " Signature: " + pr.getSignature()));
            }
        }

        if (args[0].equalsIgnoreCase("equipmentslots")) {
            Arrays.stream(NPCEquipmentSlot.values()).forEach(nes -> sender.sendMessage(String.valueOf(nes)));
        }

        if (args[0].equalsIgnoreCase("rmall")) {
            for (int i : npcManager.getNpcs().values()){
                fileManager.removeNPC(i);
            }
            npcManager.removeNPCs(true);
        }

        if (args[0].equalsIgnoreCase("rmtmp")){
            npcManager.removeNPCs(false);
        }

        if (args[0].equalsIgnoreCase("migrateCitizens")) {
            Bukkit.getScheduler().runTaskAsynchronously(SimpleNPCs.instance(), fileManager::loadCitizensFile);
        }

        return true;
    }
}

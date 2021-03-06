package com.github.arnhav.commands;

import com.github.arnhav.NPCManager;
import com.github.arnhav.SimpleNPCs;
import com.github.arnhav.data.FileManager;
import com.github.arnhav.objects.NPCEquipmentSlot;
import com.github.arnhav.objects.SNPC;
import com.github.arnhav.util.ItemUtils;
import com.github.arnhav.util.NPCUtil;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public CommandHandler(NPCManager npcManager, FileManager fileManager) {
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

        if (args[0].equalsIgnoreCase("reload")) {
            npcManager.removeNPCs(true);
            fileManager = new FileManager(SimpleNPCs.instance(), npcManager);
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 3) return false;
            StringBuilder npcName = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                npcName.append(args[i]);
                if (i != args.length - 1) npcName.append(" ");
            }
            Profile profile = npcManager.createProfile(args[1], npcName.toString());
            NPC npc = npcManager.addNPC(((Player) sender).getLocation(), profile);
            int id = npcManager.getNpcs().get(npc);
            SNPC snpc = npcManager.getSnpcs().get(id);
            fileManager.saveNPC(id, npc, snpc, true);
            sender.sendMessage(ChatColor.GRAY + "NPC: " + args[2] + " created!");
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            npcManager.removeNPC(selected, true);
            fileManager.removeNPC(id);
            sender.sendMessage(ChatColor.GRAY + "NPC: " + id + " deleted.");
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
            sender.sendMessage(ChatColor.GRAY + "NPC: " + id + " skin changed.");
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
            sender.sendMessage(ChatColor.GRAY + "NPC: " + id + " equipment changed.");
        }

        if (args[0].equalsIgnoreCase("tphere")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            NPCUtil.teleportToLocation(selected, ((Player) sender).getLocation());
            sender.sendMessage(ChatColor.GRAY + "NPC changed location.");
        }

        if (args[0].equalsIgnoreCase("setlook")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            NPCUtil.setLook(selected, ((Player) sender).getLocation());
            sender.sendMessage(ChatColor.GRAY + "Made the NPC look at you.");
        }

        if (args[0].equalsIgnoreCase("look")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            NPCUtil.toggleLook(selected);
            sender.sendMessage(ChatColor.GRAY + "Toggled look.");
        }

        if (args[0].equalsIgnoreCase("imitate")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            NPCUtil.toggleImitate(selected);
            sender.sendMessage(ChatColor.GRAY + "Toggled imitate.");
        }

        if (args[0].equalsIgnoreCase("togglehide")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            if (selected.isShownFor((Player) sender)) selected.addExcludedPlayer((Player) sender);
            else selected.removeExcludedPlayer((Player) sender);
            fileManager.saveNPC(id, selected, snpc, false);
            sender.sendMessage(ChatColor.GRAY + "NPC: " + id + " toggled hide.");
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
            sender.sendMessage(ChatColor.YELLOW + "You have selected NPC: " + id);
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length != 1) return false;
            NPC selected = npcManager.getSelectedNPC().get(sender);
            if (selected == null) return false;
            Integer id = npcManager.getNpcs().get(selected);
            SNPC snpc = npcManager.getSnpcs().get(id);
            if (snpc == null) return false;
            sender.sendMessage(ChatColor.DARK_AQUA + "--NPC Info--");
            sender.sendMessage(ChatColor.DARK_AQUA + "ID: " + id);
            sender.sendMessage(ChatColor.DARK_AQUA + "Internal ID: " + selected.getEntityId());
        }

        if (args[0].equalsIgnoreCase("equipmentslots")) {
            Arrays.stream(NPCEquipmentSlot.values()).forEach(nes -> sender.sendMessage(String.valueOf(nes)));
        }

        if (args[0].equalsIgnoreCase("rmall")) {
            for (int i : npcManager.getNpcs().values()) {
                fileManager.removeNPC(i);
            }
            npcManager.removeNPCs(true);
        }

        if (args[0].equalsIgnoreCase("rmtmp")) {
            npcManager.removeNPCs(false);
        }

        if (args[0].equalsIgnoreCase("migrateCitizens")) {
            Bukkit.getScheduler().runTaskAsynchronously(SimpleNPCs.instance(), fileManager::loadCitizensFile);
        }

        return true;
    }
}

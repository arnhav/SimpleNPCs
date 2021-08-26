package com.ags.simplenpcs.commands;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.data.FileManager;
import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandHandler implements CommandExecutor {

    private NPCManager npcManager;

    public CommandHandler(NPCManager npcManager){
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        if (!(sender instanceof Player)) return false;

        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, "snpc help");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")){
            sender.sendMessage(
                    "SimpleNPCS Help:",
                    "/snpc help",
                    "/snpc create <playerSkinName> <NPCname>",
                    "/snpc delete",
                    "/snpc setskin <playerSkinName>",
                    "/snpc tphere",
                    "/snpc info"
            );
        }

        if (args[0].equalsIgnoreCase("create")){
            if (args.length != 3) return false;
            Profile profile = npcManager.createProfile(args[1], args[2]);
            npcManager.addNPC(((Player) sender).getLocation(), profile);
            sender.sendMessage(Component.text(ChatColor.GRAY+"NPC: "+args[2]+" created!"));
        }

        if (args[0].equalsIgnoreCase("delete")){
            if (args.length != 1) return false;
            NPC selected = NPCManager.selectedNPC.get(sender);
            if (selected == null) return false;
            npcManager.removeNPC(selected);
            sender.sendMessage(Component.text(ChatColor.GRAY+"NPC: "+selected.getEntityId()+" deleted."));
        }

        if (args[0].equalsIgnoreCase("setskin")){
            if (args.length != 2) return false;
            NPC selected = NPCManager.selectedNPC.get(sender);
            if (selected == null) return false;
            Profile current = selected.getProfile();
            Profile profile = npcManager.createProfile(args[1], current.getName());
            for (Profile.Property pr : profile.getProperties()){
                Profile.Property property = new Profile.Property(pr.getName(), pr.getValue(), pr.getSignature());
                current.setProperty(property);
            }
        }

        if (args[0].equalsIgnoreCase("tphere")){
            if (args.length != 1) return false;
            NPC selected = NPCManager.selectedNPC.get(sender);
            if (selected == null) return false;
            SNPC snpc = NPCManager.npcs.get(selected);
            if (snpc == null) return false;
            NPC npc = NPCManager.spawnNPC(((Player) sender).getLocation(), selected.getProfile(), snpc);
            npcManager.removeNPC(selected);
            FileManager.saveNPC(npc, snpc);
        }

        if (args[0].equalsIgnoreCase("info")){
            if (args.length != 1) return false;
            NPC selected = NPCManager.selectedNPC.get(sender);
            if (selected == null) return false;
            SNPC snpc = NPCManager.npcs.get(selected);
            if (snpc == null) return false;
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA+"--NPC Info--"));
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA+"Internal ID: "+selected.getEntityId()));
            sender.sendMessage(Component.text(ChatColor.DARK_AQUA+"ID: "+snpc.getId()));
        }

        return true;
    }
}

package com.ags.simplenpcs.commands;


import com.ags.simplenpcs.NPCManager;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
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
        }

        if (args[0].equalsIgnoreCase("help")){
            sender.sendMessage(
                    "SimpleNPCS Help:",
                    "/snpc help",
                    "/snpc create <playerSkinName> <NPCname>"
            );
        }

        if (args[0].equalsIgnoreCase("create")){
            if (args.length != 3) return false;
            Profile profile = npcManager.createProfile(args[1], args[2]);
            npcManager.addNPC(((Player) sender).getLocation(),profile);
        }

        return true;
    }
}

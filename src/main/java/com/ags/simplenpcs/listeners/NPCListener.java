package com.ags.simplenpcs.listeners;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.api.NPCLeftClickEvent;
import com.ags.simplenpcs.api.NPCRightClickEvent;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public class NPCListener implements Listener {

    @EventHandler
    public void onNPCInteract(PlayerNPCInteractEvent event){
        Player player = event.getPlayer();
        NPC npc = event.getNPC();
        PlayerNPCInteractEvent.EntityUseAction action = event.getUseAction();
        PlayerNPCInteractEvent.Hand hand = event.getHand();

        if (action == PlayerNPCInteractEvent.EntityUseAction.ATTACK){
            NPCLeftClickEvent nlce = new NPCLeftClickEvent(player,npc);
            if (nlce.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(nlce);
        }

        if (action == PlayerNPCInteractEvent.EntityUseAction.INTERACT_AT){
            NPCRightClickEvent nrce = new NPCRightClickEvent(player, npc);
            if (nrce.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(nrce);

            EquipmentSlot es = EquipmentSlot.valueOf(hand.toString());
            EntityEquipment ee = player.getEquipment();
            if (ee == null) return;
            if (ee.getItem(es).getType() != Material.STICK) return;
            NPCManager.selectedNPC.put(player, npc);
            player.sendMessage(Component.text(ChatColor.YELLOW+"You have selected NPC: "+npc.getEntityId()));
        }

        npc.rotation().queueLookAt(player.getEyeLocation()).send(player);
    }

}

package com.ags.simplenpcs.listeners;

import com.ags.simplenpcs.NPCManager;
import com.ags.simplenpcs.api.NPCLeftClickEvent;
import com.ags.simplenpcs.api.NPCRightClickEvent;
import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.MetadataModifier;
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
        Integer id = NPCManager.npcs.get(npc);
        SNPC snpc = NPCManager.snpcs.get(id);
        PlayerNPCInteractEvent.EntityUseAction action = event.getUseAction();
        PlayerNPCInteractEvent.Hand hand = event.getHand();

        if (snpc == null) return;

        if (action == PlayerNPCInteractEvent.EntityUseAction.ATTACK){
            NPCLeftClickEvent nlce = new NPCLeftClickEvent(player,npc);
            if (nlce.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(nlce);
        }

        if (action == PlayerNPCInteractEvent.EntityUseAction.INTERACT_AT){
            NPCRightClickEvent nrce = new NPCRightClickEvent(player, npc);
            if (nrce.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(nrce);

            EquipmentSlot es = EquipmentSlot.valueOf(hand==PlayerNPCInteractEvent.Hand.MAIN_HAND?"HAND":hand.toString());
            EntityEquipment ee = player.getEquipment();
            if (ee == null) return;
            if (ee.getItem(es).getType() != Material.STICK) return;
            NPC selected = NPCManager.selectedNPC.get(player);
            if (selected != null && selected.getEntityId()==npc.getEntityId()) return;
            NPCManager.selectedNPC.put(player, npc);
            player.sendMessage(Component.text(ChatColor.YELLOW+"You have selected NPC: "+id));
        }
    }

    @EventHandler
    public void onNPCShow(PlayerNPCShowEvent event){
        NPC npc = event.getNPC();
        event.send(npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true));
    }

}

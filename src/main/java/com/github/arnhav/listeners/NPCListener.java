package com.github.arnhav.listeners;

import com.github.arnhav.NPCManager;
import com.github.arnhav.api.NPCLeftClickEvent;
import com.github.arnhav.api.NPCRightClickEvent;
import com.github.arnhav.objects.NPCEquipmentSlot;
import com.github.arnhav.objects.SNPC;
import com.github.arnhav.util.ItemUtils;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.MetadataModifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class NPCListener implements Listener {

    private NPCManager npcManager;

    public NPCListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onNPCInteract(PlayerNPCInteractEvent event) {
        Player player = event.getPlayer();
        NPC npc = event.getNPC();
        Integer id = npcManager.getNpcs().get(npc);
        SNPC snpc = npcManager.getSnpcs().get(id);
        PlayerNPCInteractEvent.EntityUseAction action = event.getUseAction();
        PlayerNPCInteractEvent.Hand hand = event.getHand();

        if (snpc == null) return;

        if (action == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
            NPCLeftClickEvent nlce = new NPCLeftClickEvent(player, npc);
            if (nlce.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(nlce);

            EquipmentSlot es = EquipmentSlot.valueOf(hand == PlayerNPCInteractEvent.Hand.MAIN_HAND ? "HAND" : hand.toString());
            EntityEquipment ee = player.getEquipment();
            if (ee == null) return;
            if (ee.getItem(es).getType() != Material.STICK) return;
            NPC selected = npcManager.getSelectedNPC().get(player);
            if (selected != null && selected.getEntityId() == npc.getEntityId()) return;
            npcManager.getSelectedNPC().put(player, npc);
            player.sendMessage(ChatColor.YELLOW + "You have selected NPC: " + id);
        }

        if (action == PlayerNPCInteractEvent.EntityUseAction.INTERACT_AT) {
            NPCRightClickEvent nrce = new NPCRightClickEvent(player, npc);
            if (nrce.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(nrce);
        }
    }

    @EventHandler
    public void onNPCShow(PlayerNPCShowEvent event) {
        NPC npc = event.getNPC();
        npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();
        npc.rotation().queueRotate(npc.getLocation().getYaw(), npc.getLocation().getPitch()).send();
        SNPC snpc = npcManager.getSnpcs().get(npcManager.getNpcs().get(npc));
        if (snpc == null) return;
        for (NPCEquipmentSlot nes : snpc.getEquipment().keySet()) {
            ItemStack is = ItemUtils.stringToItem(snpc.getEquipment().get(nes));
            if (is == null) continue;
            try {
                npc.equipment().queue(nes.getIndex(), is).send();
            } catch (Exception ignored) {
            }
        }
    }

}

package com.ags.simplenpcs.util;

import com.ags.simplenpcs.SimpleNPCs;
import com.ags.simplenpcs.objects.SNPC;
import com.github.juliarn.npc.NPC;
import org.bukkit.Location;

public class NPCUtil {

    public static void teleportToLocation(NPC selected, Location location) {
        if (selected == null) return;
        Integer id = SimpleNPCs.npcManager().getNpcs().get(selected);
        SNPC snpc = SimpleNPCs.npcManager().getSnpcs().get(id);
        if (snpc == null) return;
        selected.teleport().queueTeleport(location).send();
        SimpleNPCs.fileManager().saveNPC(id, selected, snpc, true);
    }

    public static void setLook(NPC selected, Location location) {
        if (selected == null) return;
        Integer id = SimpleNPCs.npcManager().getNpcs().get(selected);
        SNPC snpc = SimpleNPCs.npcManager().getSnpcs().get(id);
        if (snpc == null) return;
        selected.rotation().queueLookAt(location).send();
        SimpleNPCs.fileManager().saveNPCRotation(id, selected, location);
    }

    public static void toggleLook(NPC selected) {
        if (selected == null) return;
        Integer id = SimpleNPCs.npcManager().getNpcs().get(selected);
        SNPC snpc = SimpleNPCs.npcManager().getSnpcs().get(id);
        if (snpc == null) return;
        selected.setLookAtPlayer(!selected.isLookAtPlayer());
        SimpleNPCs.fileManager().saveNPC(id, selected, snpc, false);
    }

    public static void toggleImitate(NPC selected) {
        if (selected == null) return;
        Integer id = SimpleNPCs.npcManager().getNpcs().get(selected);
        SNPC snpc = SimpleNPCs.npcManager().getSnpcs().get(id);
        if (snpc == null) return;
        selected.setImitatePlayer(!selected.isImitatePlayer());
        SimpleNPCs.fileManager().saveNPC(id, selected, snpc, false);
    }

}

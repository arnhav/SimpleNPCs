package com.ags.simplenpcs.objects;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SNPC {

    private HashMap<NPCEquipmentSlot, ItemStack> equipment;

    public SNPC(){
        this.equipment = new HashMap<>();
    }

    public void addEquipment(NPCEquipmentSlot slot, ItemStack itemStack){
        equipment.put(slot, itemStack);
    }

    public HashMap<NPCEquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }
}

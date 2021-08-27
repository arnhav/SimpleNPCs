package com.ags.simplenpcs.objects;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SNPC {

    private int id;
    private HashMap<NPCEquipmentSlot, ItemStack> equipment;

    public SNPC(int id){
        setId(id);
        this.equipment = new HashMap<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addEquipment(NPCEquipmentSlot slot, ItemStack itemStack){
        equipment.put(slot, itemStack);
    }

    public HashMap<NPCEquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }
}

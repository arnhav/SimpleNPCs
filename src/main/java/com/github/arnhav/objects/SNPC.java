package com.github.arnhav.objects;

import java.util.HashMap;

public class SNPC {

    private HashMap<NPCEquipmentSlot, String> equipment;

    public SNPC() {
        this.equipment = new HashMap<>();
    }

    public void addEquipment(NPCEquipmentSlot slot, String itemStack) {
        equipment.put(slot, itemStack);
    }

    public HashMap<NPCEquipmentSlot, String> getEquipment() {
        return equipment;
    }
}

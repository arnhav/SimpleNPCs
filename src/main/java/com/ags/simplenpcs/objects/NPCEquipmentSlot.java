package com.ags.simplenpcs.objects;

public enum NPCEquipmentSlot {

    MAINHAND(0),
    OFFHAND(1),
    FEET(2),
    LEGS(3),
    CHEST(4),
    HEAD(5);

    int index;

    NPCEquipmentSlot(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

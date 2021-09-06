package com.ags.simplenpcs.api;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NPCLeftClickEvent extends NPCClickEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    /**
     * Constructs a new event instance.
     *
     * @param who The player involved in this event
     * @param npc The npc involved in this event
     */
    public NPCLeftClickEvent(Player who, NPC npc) {
        super(who, npc);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}

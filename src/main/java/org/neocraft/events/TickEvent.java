package org.neocraft.events;

import org.neocraft.util.MultiplayerStateManager;

/**
 * Enhanced TickEvent with multiplayer state tracking and desynchronization detection.
 */
public class TickEvent extends NeoEvent {

    private final long tickCount;
    private final long currentTime;

    public TickEvent() {
        this.currentTime = System.currentTimeMillis();
        this.tickCount = currentTime / 50; // Approximate tick count (20 TPS = 50ms per tick)
        
        // Track multiplayer state
        MultiplayerStateManager.getInstance().onTick(tickCount);
    }

    public long getTickCount() {
        return tickCount;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Check if we're desynchronized from server
     */
    public boolean isDesynchronized() {
        return MultiplayerStateManager.getInstance().isDesynchronized();
    }

    /**
     * Get number of desync events that have occurred
     */
    public int getDesyncEventCount() {
        return MultiplayerStateManager.getInstance().getDesyncEventCount();
    }
}

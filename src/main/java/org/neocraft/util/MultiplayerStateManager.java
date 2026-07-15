package org.neocraft.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiplayer game state to prevent desynchronization crashes.
 * Tracks server state, player data, and entity information.
 */
public class MultiplayerStateManager {

    private static final String TAG = "MultiplayerState";
    private static final MultiplayerStateManager INSTANCE = new MultiplayerStateManager();

    private volatile long lastServerTick = 0;
    private volatile long lastClientTick = 0;
    private volatile int tickSkipCounter = 0;
    private volatile boolean isDesynchronized = false;
    private volatile int activePlayerCount = 0;
    private volatile int activeEntityCount = 0;
    
    private final Map<String, Long> playerLastSeenTime = new ConcurrentHashMap<>();
    private final Map<Integer, Object> entityCache = new ConcurrentHashMap<>();
    private final java.util.concurrent.atomic.AtomicInteger desyncEventCount = 
        new java.util.concurrent.atomic.AtomicInteger(0);

    private MultiplayerStateManager() {}

    public static MultiplayerStateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Called every game tick to track synchronization
     */
    public void onTick(long currentServerTick) {
        lastClientTick = System.currentTimeMillis();
        
        // Check for tick skips (indicates desync)
        if (lastServerTick != 0) {
            long tickDelta = currentServerTick - lastServerTick;
            
            if (tickDelta > 5) {
                isDesynchronized = true;
                tickSkipCounter++;
                desyncEventCount.incrementAndGet();
                
                Log.warn(TAG, "Tick desynchronization detected: " + tickDelta + " ticks skipped");
                Log.warn(TAG, "Total desync events: " + desyncEventCount.get());
                
                // Try to recover
                attemptResynchronization();
            } else if (tickDelta <= 0) {
                Log.warn(TAG, "Negative or zero tick delta: " + tickDelta);
            } else {
                isDesynchronized = false;
            }
        }
        
        lastServerTick = currentServerTick;
        
        // Clean up stale player data
        cleanupStalePlayerData();
    }

    /**
     * Track when a player is seen
     */
    public void onPlayerSeen(String playerName) {
        if (playerName != null && !playerName.isEmpty()) {
            playerLastSeenTime.put(playerName, System.currentTimeMillis());
        }
    }

    /**
     * Remove players who haven't been seen in a while
     */
    private void cleanupStalePlayerData() {
        long timeout = 30000; // 30 seconds
        long now = System.currentTimeMillis();
        
        playerLastSeenTime.entrySet().removeIf(entry -> 
            (now - entry.getValue()) > timeout
        );
    }

    /**
     * Cache entity data to reduce lookups
     */
    public void cacheEntity(int entityId, Object entity) {
        if (entity != null) {
            entityCache.put(entityId, entity);
        }
    }

    /**
     * Retrieve cached entity
     */
    public Object getCachedEntity(int entityId) {
        return entityCache.getOrDefault(entityId, null);
    }

    /**
     * Clear entity cache on disconnect
     */
    public void clearEntityCache() {
        entityCache.clear();
        playerLastSeenTime.clear();
        resetDesyncState();
    }

    /**
     * Attempt to resynchronize with server
     */
    private void attemptResynchronization() {
        Log.info(TAG, "Attempting resynchronization...");
        // In a full implementation, this would request a full world sync from server
        // For now, we just log and flag for monitoring
    }

    /**
     * Reset desync tracking
     */
    public void resetDesyncState() {
        isDesynchronized = false;
        tickSkipCounter = 0;
        lastServerTick = 0;
        lastClientTick = 0;
    }

    // Getters
    public boolean isDesynchronized() {
        return isDesynchronized;
    }

    public int getDesyncEventCount() {
        return desyncEventCount.get();
    }

    public int getTickSkipCounter() {
        return tickSkipCounter;
    }

    public int getActivePlayerCount() {
        return playerLastSeenTime.size();
    }

    public int getActiveEntityCount() {
        return entityCache.size();
    }

    public long getLastServerTick() {
        return lastServerTick;
    }
}

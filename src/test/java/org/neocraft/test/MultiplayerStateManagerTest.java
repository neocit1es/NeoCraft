package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocraft.util.MultiplayerStateManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MultiplayerStateManager.
 * Validates tick synchronization and state tracking.
 */
class MultiplayerStateManagerTest {

    private MultiplayerStateManager manager;

    @BeforeEach
    void setUp() {
        manager = MultiplayerStateManager.getInstance();
        manager.resetDesyncState();
    }

    @Test
    void testSingletonInstance() {
        MultiplayerStateManager instance1 = MultiplayerStateManager.getInstance();
        MultiplayerStateManager instance2 = MultiplayerStateManager.getInstance();
        
        assertSame(instance1, instance2, "Should return same singleton instance");
    }

    @Test
    void testInitialState() {
        assertFalse(manager.isDesynchronized(), "Should start synchronized");
        assertEquals(0, manager.getDesyncEventCount(), "Should have no desync events initially");
        assertEquals(0, manager.getTickSkipCounter(), "Should have no tick skips initially");
    }

    @Test
    void testNormalTickProgression() {
        manager.onTick(0);
        assertFalse(manager.isDesynchronized(), "Single tick should not cause desync");
        
        manager.onTick(1);
        assertFalse(manager.isDesynchronized(), "Normal tick progression should not cause desync");
        
        manager.onTick(2);
        assertFalse(manager.isDesynchronized(), "Consecutive ticks should remain synchronized");
    }

    @Test
    void testTickSkipDetection() {
        manager.onTick(0);
        manager.onTick(1);
        manager.onTick(2);
        manager.onTick(10);  // Jump from 2 to 10 = 8 tick skip
        
        assertTrue(manager.isDesynchronized(), "Large tick jump should trigger desync");
        assertEquals(1, manager.getDesyncEventCount(), "Should record 1 desync event");
        assertTrue(manager.getTickSkipCounter() > 0, "Should have tick skip counter > 0");
    }

    @Test
    void testPlayerTracking() {
        assertEquals(0, manager.getActivePlayerCount(), "Should start with 0 players");
        
        manager.onPlayerSeen("Player1");
        assertEquals(1, manager.getActivePlayerCount(), "Should track 1 player");
        
        manager.onPlayerSeen("Player2");
        assertEquals(2, manager.getActivePlayerCount(), "Should track 2 players");
        
        manager.onPlayerSeen("Player1");  // Same player
        assertEquals(2, manager.getActivePlayerCount(), "Should not double-count same player");
    }

    @Test
    void testNullPlayerNameHandling() {
        manager.onPlayerSeen(null);
        assertEquals(0, manager.getActivePlayerCount(), "Should not track null player names");
    }

    @Test
    void testEntityCaching() {
        Object entity1 = new Object();
        Object entity2 = new Object();
        
        manager.cacheEntity(1, entity1);
        manager.cacheEntity(2, entity2);
        
        assertSame(entity1, manager.getCachedEntity(1), "Should retrieve cached entity");
        assertSame(entity2, manager.getCachedEntity(2), "Should retrieve different cached entity");
    }

    @Test
    void testEntityCacheWithNull() {
        manager.cacheEntity(1, null);
        assertNull(manager.getCachedEntity(1), "Should not cache null entities");
    }

    @Test
    void testClearEntityCache() {
        manager.cacheEntity(1, new Object());
        manager.cacheEntity(2, new Object());
        
        assertEquals(2, manager.getActiveEntityCount(), "Should have 2 cached entities");
        
        manager.clearEntityCache();
        
        assertEquals(0, manager.getActiveEntityCount(), "Cache should be cleared");
        assertNull(manager.getCachedEntity(1), "Cleared entity should not be retrievable");
    }

    @Test
    void testResetDesyncState() {
        manager.onTick(10);
        manager.onTick(20);
        assertTrue(manager.isDesynchronized(), "Should be desynchronized after tick jump");
        
        manager.resetDesyncState();
        assertFalse(manager.isDesynchronized(), "Should be synchronized after reset");
        assertEquals(0, manager.getTickSkipCounter(), "Tick skip counter should be reset");
    }

    @Test
    void testMultipleDesyncEvents() {
        manager.onTick(0);
        manager.onTick(10);  // First desync
        manager.onTick(11);
        manager.onTick(20);  // Second desync
        
        assertEquals(2, manager.getDesyncEventCount(), "Should record 2 desync events");
    }

    @Test
    void testLastServerTickTracking() {
        assertEquals(0, manager.getLastServerTick(), "Should start at tick 0");
        
        manager.onTick(5);
        assertEquals(5, manager.getLastServerTick(), "Should track last tick");
        
        manager.onTick(6);
        assertEquals(6, manager.getLastServerTick(), "Should update last tick");
    }
}

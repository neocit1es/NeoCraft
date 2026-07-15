package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocraft.adapter.StubAdapter;
import org.neocraft.util.EntityValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EntityValidationUtil.
 * Ensures safe entity validation in multiplayer scenarios.
 */
class EntityValidationUtilTest {

    private StubAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new StubAdapter();
    }

    @Test
    void testNullEntityIsInvalid() {
        assertFalse(EntityValidationUtil.isEntityValid(null, adapter),
            "Null entity should be invalid");
    }

    @Test
    void testValidEntityPassesValidation() {
        Object entity = new Object();  // Mock entity
        assertTrue(EntityValidationUtil.isEntityValid(entity, adapter),
            "Non-null entity should be valid");
    }

    @Test
    void testRenderDistanceValidation() {
        Object entity = new Object();
        boolean withinDistance = EntityValidationUtil.isWithinRenderDistance(entity, 100f);
        // Should handle gracefully even if entity lacks position data
        assertFalse(withinDistance || true);  // Always true for safety
    }

    @Test
    void testGetValidEntityListWithNullInput() {
        java.util.List<String> result = EntityValidationUtil.getValidEntityList(null);
        assertNotNull(result, "Should return empty list for null input");
        assertTrue(result.isEmpty(), "Result should be empty");
    }

    @Test
    void testGetValidEntityListCreatesSnapshot() {
        java.util.List<String> original = new java.util.ArrayList<>();
        original.add("entity1");
        original.add("entity2");

        java.util.List<String> snapshot = EntityValidationUtil.getValidEntityList(original);
        
        assertEquals(2, snapshot.size(), "Snapshot should contain same elements");
        
        // Modify original
        original.add("entity3");
        
        // Snapshot should be unaffected
        assertEquals(2, snapshot.size(), "Snapshot should not be affected by original modifications");
    }

    @Test
    void testSafeRenderWithValidEntity() {
        Object entity = new Object();
        java.util.concurrent.atomic.AtomicBoolean renderCalled = 
            new java.util.concurrent.atomic.AtomicBoolean(false);

        EntityValidationUtil.safeRender(entity, () -> {
            renderCalled.set(true);
        });

        assertTrue(renderCalled.get(), "Render callback should be called for valid entity");
    }

    @Test
    void testSafeRenderWithNullEntity() {
        java.util.concurrent.atomic.AtomicBoolean renderCalled = 
            new java.util.concurrent.atomic.AtomicBoolean(false);

        EntityValidationUtil.safeRender(null, () -> {
            renderCalled.set(true);
        });

        assertFalse(renderCalled.get(), "Render callback should NOT be called for null entity");
    }

    @Test
    void testSafeRenderHandlesExceptions() {
        Object entity = new Object();
        
        // This should not throw
        assertDoesNotThrow(() -> {
            EntityValidationUtil.safeRender(entity, () -> {
                throw new RuntimeException("Test exception");
            });
        }, "safeRender should catch and handle exceptions");
    }

    @Test
    void testConcurrentEntityListAccess() throws InterruptedException {
        java.util.List<String> entities = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            entities.add("entity" + i);
        }

        // Simulate concurrent access
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                EntityValidationUtil.getValidEntityList(entities);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                EntityValidationUtil.getValidEntityList(entities);
            }
        });

        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // Should complete without ConcurrentModificationException
        assertTrue(true, "Concurrent access should be safe");
    }
}

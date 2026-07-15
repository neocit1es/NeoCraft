package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocraft.hud.HudElement;
import org.neocraft.hud.HudElementRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HudElementRegistry.
 * Ensures thread-safe HUD element management.
 */
class HudElementRegistryTest {

    private HudElementRegistry registry;
    private MockHudElement element1;
    private MockHudElement element2;

    @BeforeEach
    void setUp() {
        registry = new HudElementRegistry();
        element1 = new MockHudElement("Element1");
        element2 = new MockHudElement("Element2");
    }

    @Test
    void testRegisterElement() {
        registry.register(element1);
        assertTrue(registry.isRegistered(element1), "Element should be registered");
        assertEquals(1, registry.getElementCount(), "Should have 1 element");
    }

    @Test
    void testRegisterMultipleElements() {
        registry.register(element1);
        registry.register(element2);
        
        assertEquals(2, registry.getElementCount(), "Should have 2 elements");
        assertTrue(registry.isRegistered(element1));
        assertTrue(registry.isRegistered(element2));
    }

    @Test
    void testRegisterNullElement() {
        registry.register(null);
        assertEquals(0, registry.getElementCount(), "Should not register null element");
    }

    @Test
    void testUnregisterElement() {
        registry.register(element1);
        assertTrue(registry.isRegistered(element1));
        
        registry.unregister(element1);
        assertFalse(registry.isRegistered(element1), "Element should be unregistered");
        assertEquals(0, registry.getElementCount(), "Should have 0 elements");
    }

    @Test
    void testUnregisterNullElement() {
        // Should not throw
        assertDoesNotThrow(() -> registry.unregister(null));
    }

    @Test
    void testUnregisterNonexistentElement() {
        registry.register(element1);
        MockHudElement other = new MockHudElement("Other");
        
        registry.unregister(other);
        assertEquals(1, registry.getElementCount(), "Should still have element1");
    }

    @Test
    void testDuplicateRegistration() {
        registry.register(element1);
        registry.register(element1);
        
        assertEquals(1, registry.getElementCount(), "Should not register duplicate");
    }

    @Test
    void testClearRegistry() {
        registry.register(element1);
        registry.register(element2);
        assertEquals(2, registry.getElementCount());
        
        registry.clear();
        assertEquals(0, registry.getElementCount(), "Registry should be cleared");
    }

    @Test
    void testRenderAllElements() {
        registry.register(element1);
        registry.register(element2);
        
        // Should not throw
        assertDoesNotThrow(() -> registry.renderAll(1.0f));
        
        assertTrue(element1.renderCalled, "Element1 should be rendered");
        assertTrue(element2.renderCalled, "Element2 should be rendered");
    }

    @Test
    void testRenderDisabledElements() {
        element1.setEnabled(false);
        registry.register(element1);
        
        registry.renderAll(1.0f);
        assertFalse(element1.renderCalled, "Disabled element should not be rendered");
    }

    @Test
    void testRenderWithException() {
        MockHudElement badElement = new MockHudElement("Bad") {
            @Override
            public void render(float partialTicks) {
                throw new RuntimeException("Render failed");
            }
        };
        
        registry.register(element1);
        registry.register(badElement);
        
        // Should not throw despite bad element
        assertDoesNotThrow(() -> registry.renderAll(1.0f));
        assertTrue(element1.renderCalled, "Good element should still be rendered");
    }

    @Test
    void testGetElements() {
        registry.register(element1);
        registry.register(element2);
        
        var elements = registry.getElements();
        assertEquals(2, elements.size(), "Should return all elements");
        assertTrue(elements.contains(element1));
        assertTrue(elements.contains(element2));
    }

    @Test
    void testGetElementsReturnsCopy() {
        registry.register(element1);
        var elements = registry.getElements();
        
        elements.add(element2);
        
        // Registry should not be affected
        assertEquals(1, registry.getElementCount(), "Registry should not be affected by returned list");
    }

    @Test
    void testConcurrentRenderAndRegister() throws InterruptedException {
        Thread renderThread = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                registry.renderAll(1.0f);
            }
        });

        Thread registerThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                registry.register(new MockHudElement("Thread" + i));
            }
        });

        renderThread.start();
        registerThread.start();
        
        renderThread.join();
        registerThread.join();
        
        // Should complete without ConcurrentModificationException
        assertTrue(registry.getElementCount() > 0);
    }

    // Mock HudElement for testing
    static class MockHudElement extends HudElement {
        boolean renderCalled = false;
        private boolean enabled = true;

        MockHudElement(String name) {
            super(name);
        }

        @Override
        public void render(float partialTicks) {
            renderCalled = true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

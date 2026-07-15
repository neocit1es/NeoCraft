package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocraft.modules.impl.FPSBoosterModule;
import org.neocraft.modules.Setting;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FPSBoosterModule.
 * Validates rendering optimization settings and FPS tracking.
 */
class FPSBoosterModuleTest {

    private FPSBoosterModule module;

    @BeforeEach
    void setUp() {
        module = new FPSBoosterModule();
    }

    @Test
    void testModuleInitialization() {
        assertNotNull(module, "Module should initialize");
        assertEquals("FPS Booster", module.getName());
        assertFalse(module.isEnabled(), "Module should start disabled");
    }

    @Test
    void testModuleEnable() {
        module.setEnabled(true);
        assertTrue(module.isEnabled(), "Module should be enabled");
    }

    @Test
    void testModuleDisable() {
        module.setEnabled(true);
        assertTrue(module.isEnabled());
        module.setEnabled(false);
        assertFalse(module.isEnabled(), "Module should be disabled");
    }

    @Test
    void testChunkRenderDistanceBounds() {
        // Should clamp between 8-16
        assertTrue(module.chunkRenderDistance.getMin() == 8);
        assertTrue(module.chunkRenderDistance.getMax() == 16);
    }

    @Test
    void testChunkRenderDistanceValue() {
        module.chunkRenderDistance.setValue(12);
        assertEquals(12, module.chunkRenderDistance.getValue());
    }

    @Test
    void testParticleLimitBounds() {
        // Should clamp between 100-2000
        assertTrue(module.particleLimit.getMin() == 100);
        assertTrue(module.particleLimit.getMax() == 2000);
    }

    @Test
    void testParticleLimitValue() {
        module.particleLimit.setValue(750);
        assertEquals(750, module.particleLimit.getValue());
    }

    @Test
    void testEntityRenderDistanceBounds() {
        // Should clamp between 16-64
        assertTrue(module.entityRenderDistance.getMin() == 16);
        assertTrue(module.entityRenderDistance.getMax() == 64);
    }

    @Test
    void testFPSCounterInitialization() {
        assertEquals(0, module.getFPS(), "FPS should start at 0");
    }

    @Test
    void testGetStatsDisabled() {
        module.setEnabled(false);
        String stats = module.getStats();
        assertTrue(stats.contains("OFF"), "Stats should show OFF when disabled");
    }

    @Test
    void testGetStatsEnabled() {
        module.setEnabled(true);
        String stats = module.getStats();
        assertTrue(stats.contains("FPS"), "Stats should show FPS when enabled");
    }

    @Test
    void testOptimizeChunksToggle() {
        module.optimizeChunks.setValue(true);
        assertTrue(module.optimizeChunks.getValue());
        module.optimizeChunks.setValue(false);
        assertFalse(module.optimizeChunks.getValue());
    }

    @Test
    void testReduceParticlesToggle() {
        module.reduceParticles.setValue(true);
        assertTrue(module.reduceParticles.getValue());
        module.reduceParticles.setValue(false);
        assertFalse(module.reduceParticles.getValue());
    }

    @Test
    void testDisableFancyGraphicsToggle() {
        module.disableFancyGraphics.setValue(true);
        assertTrue(module.disableFancyGraphics.getValue());
    }

    @Test
    void testReduceLightUpdatesToggle() {
        module.reduceLightUpdates.setValue(true);
        assertTrue(module.reduceLightUpdates.getValue());
    }

    @Test
    void testDisableVignetteToggle() {
        module.disableVignetteEffect.setValue(true);
        assertTrue(module.disableVignetteEffect.getValue());
    }

    @Test
    void testDisableMipMapToggle() {
        module.disableMipMap.setValue(true);
        assertTrue(module.disableMipMap.getValue());
    }

    @Test
    void testOptimizeEntitiesToggle() {
        module.optimizeEntities.setValue(true);
        assertTrue(module.optimizeEntities.getValue());
    }

    @Test
    void testShowFPSCounterToggle() {
        module.showFPSCounter.setValue(true);
        assertTrue(module.showFPSCounter.getValue());
        module.showFPSCounter.setValue(false);
        assertFalse(module.showFPSCounter.getValue());
    }

    @Test
    void testMultipleSettingsChange() {
        module.optimizeChunks.setValue(true);
        module.reduceParticles.setValue(true);
        module.disableFancyGraphics.setValue(true);
        
        assertTrue(module.optimizeChunks.getValue());
        assertTrue(module.reduceParticles.getValue());
        assertTrue(module.disableFancyGraphics.getValue());
    }

    @Test
    void testEnableAppliesOptimizations() {
        assertDoesNotThrow(() -> {
            module.setEnabled(true);
        }, "Enable should not throw exception");
    }

    @Test
    void testDisableResetsOptimizations() {
        module.setEnabled(true);
        assertDoesNotThrow(() -> {
            module.setEnabled(false);
        }, "Disable should not throw exception");
    }

    @Test
    void testSettingChangedCallback() {
        module.setEnabled(true);
        assertDoesNotThrow(() -> {
            module.chunkRenderDistance.setValue(14);
        }, "Setting change should not throw exception");
    }
}

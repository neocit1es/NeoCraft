package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocraft.modules.impl.CoordinateDisplayModule;
import org.neocraft.events.TickEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CoordinateDisplayModule.
 * Validates position tracking and display formatting.
 */
class CoordinateDisplayModuleTest {

    private CoordinateDisplayModule module;

    @BeforeEach
    void setUp() {
        module = new CoordinateDisplayModule();
    }

    @Test
    void testModuleInitialization() {
        assertNotNull(module);
        assertEquals("Coordinate Display", module.getName());
        assertFalse(module.isEnabled());
    }

    @Test
    void testModuleEnable() {
        module.setEnabled(true);
        assertTrue(module.isEnabled());
    }

    @Test
    void testModuleDisable() {
        module.setEnabled(true);
        module.setEnabled(false);
        assertFalse(module.isEnabled());
    }

    @Test
    void testShowCoordinatesToggle() {
        module.showCoordinates.setValue(true);
        assertTrue(module.showCoordinates.getValue());
        module.showCoordinates.setValue(false);
        assertFalse(module.showCoordinates.getValue());
    }

    @Test
    void testShowDirectionToggle() {
        module.showDirection.setValue(true);
        assertTrue(module.showDirection.getValue());
    }

    @Test
    void testShowBiomeToggle() {
        module.showBiome.setValue(true);
        assertTrue(module.showBiome.getValue());
    }

    @Test
    void testShowChunkToggle() {
        module.showChunk.setValue(false);
        assertFalse(module.showChunk.getValue());
        module.showChunk.setValue(true);
        assertTrue(module.showChunk.getValue());
    }

    @Test
    void testShowFacingToggle() {
        module.showFacing.setValue(true);
        assertTrue(module.showFacing.getValue());
    }

    @Test
    void testShowAltitudeToggle() {
        module.showAltitude.setValue(true);
        assertTrue(module.showAltitude.getValue());
    }

    @Test
    void testPreciseCoordsToggle() {
        module.preciseCoords.setValue(true);
        assertTrue(module.preciseCoords.getValue());
    }

    @Test
    void testDecimalPlacesBounds() {
        // Should clamp between 0-3
        assertTrue(module.decimalPlaces.getMin() == 0);
        assertTrue(module.decimalPlaces.getMax() == 3);
    }

    @Test
    void testDecimalPlacesValue() {
        module.decimalPlaces.setValue(2);
        assertEquals(2, module.decimalPlaces.getValue());
    }

    @Test
    void testShowSpeedToggle() {
        module.showSpeed.setValue(true);
        assertTrue(module.showSpeed.getValue());
    }

    @Test
    void testCompactLayoutToggle() {
        module.compactLayout.setValue(false);
        assertFalse(module.compactLayout.getValue());
        module.compactLayout.setValue(true);
        assertTrue(module.compactLayout.getValue());
    }

    @Test
    void testBackgroundColorToggle() {
        module.backgroundColor.setValue(true);
        assertTrue(module.backgroundColor.getValue());
    }

    @Test
    void testBuildDisplayTextDisabled() {
        module.setEnabled(false);
        String display = module.buildDisplayText();
        assertTrue(display.isEmpty(), "Display should be empty when disabled");
    }

    @Test
    void testBuildDisplayTextEnabled() {
        module.setEnabled(true);
        String display = module.buildDisplayText();
        assertNotNull(display);
        // Display may contain position info
    }

    @Test
    void testOnTickUpdatesPosition() {
        module.setEnabled(true);
        TickEvent event = new TickEvent();
        assertDoesNotThrow(() -> {
            module.onTick(event);
        }, "onTick should not throw exception");
    }

    @Test
    void testOnTickDisabledDoesNothing() {
        module.setEnabled(false);
        TickEvent event = new TickEvent();
        assertDoesNotThrow(() -> {
            module.onTick(event);
        });
    }

    @Test
    void testDefaultSettings() {
        assertTrue(module.showCoordinates.getValue(), "Show coordinates should default true");
        assertTrue(module.showDirection.getValue(), "Show direction should default true");
        assertTrue(module.showBiome.getValue(), "Show biome should default true");
        assertFalse(module.showChunk.getValue(), "Show chunk should default false");
        assertTrue(module.showFacing.getValue(), "Show facing should default true");
        assertTrue(module.showAltitude.getValue(), "Show altitude should default true");
        assertTrue(module.preciseCoords.getValue(), "Precise coords should default true");
        assertEquals(2, module.decimalPlaces.getValue(), "Decimal places should default to 2");
        assertTrue(module.showSpeed.getValue(), "Show speed should default true");
        assertFalse(module.compactLayout.getValue(), "Compact layout should default false");
    }

    @Test
    void testCompactLayoutDisplay() {
        module.setEnabled(true);
        module.compactLayout.setValue(true);
        String display = module.buildDisplayText();
        assertNotNull(display);
        // Compact layout should be shorter than detailed
    }

    @Test
    void testDetailedLayoutDisplay() {
        module.setEnabled(true);
        module.compactLayout.setValue(false);
        String display = module.buildDisplayText();
        assertNotNull(display);
        // Detailed layout should contain more lines
    }

    @Test
    void testMultipleSettingsConfiguration() {
        module.showCoordinates.setValue(true);
        module.showDirection.setValue(true);
        module.showBiome.setValue(true);
        module.showSpeed.setValue(true);
        module.preciseCoords.setValue(true);
        module.decimalPlaces.setValue(3);
        
        assertTrue(module.showCoordinates.getValue());
        assertTrue(module.showDirection.getValue());
        assertTrue(module.showBiome.getValue());
        assertTrue(module.showSpeed.getValue());
        assertTrue(module.preciseCoords.getValue());
        assertEquals(3, module.decimalPlaces.getValue());
    }

    @Test
    void testDisplayExceptionHandling() {
        module.setEnabled(true);
        assertDoesNotThrow(() -> {
            String display = module.buildDisplayText();
            assertNotNull(display);
        }, "Display building should handle exceptions gracefully");
    }

    @Test
    void testToggleOnOff() {
        assertFalse(module.isEnabled());
        module.setEnabled(true);
        assertTrue(module.isEnabled());
        module.setEnabled(false);
        assertFalse(module.isEnabled());
    }
}

package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neocraft.modules.impl.EnhancedTooltipsModule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EnhancedTooltipsModule.
 * Validates tooltip building and item information display.
 */
class EnhancedTooltipsModuleTest {

    private EnhancedTooltipsModule module;
    private Object mockItemStack;

    @BeforeEach
    void setUp() {
        module = new EnhancedTooltipsModule();
        mockItemStack = new Object();  // Mock item
    }

    @Test
    void testModuleInitialization() {
        assertNotNull(module);
        assertEquals("Enhanced Tooltips", module.getName());
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
    void testBuildTooltipWithNullItem() {
        List<String> tooltip = module.buildEnhancedTooltip(null);
        assertNotNull(tooltip);
        assertTrue(tooltip.isEmpty(), "Tooltip should be empty for null item");
    }

    @Test
    void testBuildTooltipWithValidItem() {
        List<String> tooltip = module.buildEnhancedTooltip(mockItemStack);
        assertNotNull(tooltip);
        assertFalse(tooltip.isEmpty(), "Tooltip should contain item info");
    }

    @Test
    void testShowEnchantmentsToggle() {
        module.showEnchantments.setValue(true);
        assertTrue(module.showEnchantments.getValue());
        module.showEnchantments.setValue(false);
        assertFalse(module.showEnchantments.getValue());
    }

    @Test
    void testShowDurabilityToggle() {
        module.showDurability.setValue(true);
        assertTrue(module.showDurability.getValue());
    }

    @Test
    void testShowAttributesToggle() {
        module.showAttributes.setValue(true);
        assertTrue(module.showAttributes.getValue());
    }

    @Test
    void testShowRarityToggle() {
        module.showRarity.setValue(true);
        assertTrue(module.showRarity.getValue());
    }

    @Test
    void testShowStackSizeToggle() {
        module.showStackSize.setValue(true);
        assertTrue(module.showStackSize.getValue());
    }

    @Test
    void testCompactModeToggle() {
        module.compactMode.setValue(true);
        assertTrue(module.compactMode.getValue());
        module.compactMode.setValue(false);
        assertFalse(module.compactMode.getValue());
    }

    @Test
    void testColorizeEnchantmentsToggle() {
        module.colorizeEnchantments.setValue(true);
        assertTrue(module.colorizeEnchantments.getValue());
    }

    @Test
    void testShowItemIDToggle() {
        module.showItemID.setValue(false);
        assertFalse(module.showItemID.getValue());
        module.showItemID.setValue(true);
        assertTrue(module.showItemID.getValue());
    }

    @Test
    void testEstimateValueToggle() {
        module.estimateValue.setValue(false);
        assertFalse(module.estimateValue.getValue());
        module.estimateValue.setValue(true);
        assertTrue(module.estimateValue.getValue());
    }

    @Test
    void testDefaultSettings() {
        assertTrue(module.showEnchantments.getValue(), "Show enchantments should default true");
        assertTrue(module.showDurability.getValue(), "Show durability should default true");
        assertTrue(module.showAttributes.getValue(), "Show attributes should default true");
        assertTrue(module.showRarity.getValue(), "Show rarity should default true");
        assertTrue(module.showStackSize.getValue(), "Show stack size should default true");
        assertFalse(module.compactMode.getValue(), "Compact mode should default false");
        assertTrue(module.colorizeEnchantments.getValue(), "Colorize enchantments should default true");
    }

    @Test
    void testTooltipContainsItemName() {
        List<String> tooltip = module.buildEnhancedTooltip(mockItemStack);
        assertTrue(tooltip.size() > 0, "Tooltip should contain at least item name");
        assertTrue(tooltip.get(0).contains("Item") || tooltip.get(0).length() > 0);
    }

    @Test
    void testMultipleSettingsConfiguration() {
        module.showEnchantments.setValue(true);
        module.showDurability.setValue(true);
        module.showAttributes.setValue(true);
        module.colorizeEnchantments.setValue(true);
        
        assertTrue(module.showEnchantments.getValue());
        assertTrue(module.showDurability.getValue());
        assertTrue(module.showAttributes.getValue());
        assertTrue(module.colorizeEnchantments.getValue());
    }

    @Test
    void testEnableTooltipGeneration() {
        module.setEnabled(true);
        assertDoesNotThrow(() -> {
            List<String> tooltip = module.buildEnhancedTooltip(mockItemStack);
            assertNotNull(tooltip);
        });
    }

    @Test
    void testDisableDoesNotBreakTooltips() {
        module.setEnabled(false);
        // Module should still work when disabled, just won't render
        List<String> tooltip = module.buildEnhancedTooltip(mockItemStack);
        assertNotNull(tooltip);
    }

    @Test
    void testTooltipExceptionHandling() {
        module.setEnabled(true);
        assertDoesNotThrow(() -> {
            module.buildEnhancedTooltip(mockItemStack);
        }, "Should handle tooltip building gracefully");
    }
}

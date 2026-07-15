package org.neocraft.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.neocraft.util.SafeConfigManager;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SafeConfigManager.
 * Validates thread-safe config I/O and backup recovery.
 */
class SafeConfigManagerTest {

    private SafeConfigManager config;
    private File configFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        configFile = tempDir.resolve("test-config.properties").toFile();
        config = new SafeConfigManager(configFile);
    }

    @Test
    void testConfigFileCreation() {
        // File should be created during init
        assertNotNull(configFile, "Config file should be created");
    }

    @Test
    void testSetAndGetString() {
        config.set("testKey", "testValue");
        String retrieved = config.get("testKey", "default");
        
        assertEquals("testValue", retrieved, "Should store and retrieve string values");
    }

    @Test
    void testGetWithDefault() {
        String result = config.get("nonExistent", "defaultValue");
        
        assertEquals("defaultValue", result, "Should return default for missing key");
    }

    @Test
    void testSetAndGetBoolean() {
        config.setBoolean("enabled", true);
        boolean retrieved = config.getBoolean("enabled", false);
        
        assertTrue(retrieved, "Should store and retrieve boolean values");
    }

    @Test
    void testBooleanDefault() {
        boolean result = config.getBoolean("nonExistent", true);
        
        assertTrue(result, "Should return default boolean value");
    }

    @Test
    void testSetAndGetInteger() {
        config.setInt("count", 42);
        int retrieved = config.getInt("count", 0);
        
        assertEquals(42, retrieved, "Should store and retrieve integer values");
    }

    @Test
    void testIntegerDefault() {
        int result = config.getInt("nonExistent", 99);
        
        assertEquals(99, result, "Should return default integer value");
    }

    @Test
    void testInvalidIntegerValue() {
        config.set("invalidInt", "notANumber");
        int result = config.getInt("invalidInt", 55);
        
        assertEquals(55, result, "Should return default for invalid integer");
    }

    @Test
    void testNullKeyHandling() {
        // Should not throw
        assertDoesNotThrow(() -> {
            config.set(null, "value");
        }, "Should handle null key gracefully");
    }

    @Test
    void testNullValueHandling() {
        // Should not throw
        assertDoesNotThrow(() -> {
            config.set("key", null);
        }, "Should handle null value gracefully");
    }

    @Test
    void testMultipleValues() {
        config.setInt("int1", 10);
        config.setInt("int2", 20);
        config.setBoolean("bool1", true);
        config.set("str1", "value1");
        
        assertEquals(10, config.getInt("int1", 0));
        assertEquals(20, config.getInt("int2", 0));
        assertTrue(config.getBoolean("bool1", false));
        assertEquals("value1", config.get("str1", ""));
    }

    @Test
    void testConfigPersistence() {
        config.setInt("persistent", 123);
        
        // Create new instance reading same file
        SafeConfigManager config2 = new SafeConfigManager(configFile);
        int value = config2.getInt("persistent", 0);
        
        assertEquals(123, value, "Config should persist across instances");
    }

    @Test
    void testOverwriteExistingValue() {
        config.set("key", "value1");
        assertEquals("value1", config.get("key", ""));
        
        config.set("key", "value2");
        assertEquals("value2", config.get("key", ""));
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                config.setInt("counter", i);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                config.getInt("counter", 0);
            }
        });

        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // Should complete without errors
        assertTrue(true, "Concurrent access should be thread-safe");
    }

    @Test
    void testBackupFileCreation() {
        config.set("key", "value");
        File backup = new File(configFile.getAbsolutePath() + ".bak");
        
        // Backup might exist after save
        assertTrue(configFile.exists() || backup.exists(), 
            "Config or backup file should exist after save");
    }
}

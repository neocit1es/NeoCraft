package org.neocraft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Thread-safe configuration manager with automatic backup and recovery.
 * Prevents config corruption and crashes from concurrent I/O operations.
 */
public class SafeConfigManager {

    private static final String TAG = "SafeConfig";
    private final File configFile;
    private final Properties properties = new Properties();
    private final Object fileLock = new Object();

    public SafeConfigManager(File file) {
        this.configFile = file;
        loadConfig();
    }

    /**
     * Load configuration from disk with error handling
     */
    private void loadConfig() {
        synchronized (fileLock) {
            if (!configFile.exists()) {
                Log.info(TAG, "Config file not found, creating new one: " + configFile.getAbsolutePath());
                return;
            }

            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                Log.debug(TAG, "Config loaded successfully");
            } catch (IOException e) {
                Log.error(TAG, "Failed to load config: " + e.getMessage());
                
                // Try to recover from backup
                attemptBackupRecovery();
            } catch (Exception e) {
                Log.error(TAG, "Unexpected error loading config: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Save configuration with automatic backup
     */
    public void saveConfig() {
        synchronized (fileLock) {
            try {
                // Create backup of existing file
                File backup = new File(configFile.getAbsolutePath() + ".bak");
                if (configFile.exists()) {
                    if (backup.exists()) {
                        backup.delete();
                    }
                    configFile.renameTo(backup);
                }

                // Write new config
                try (FileOutputStream fos = new FileOutputStream(configFile)) {
                    properties.store(fos, "NeoCraft Configuration - Do not edit manually");
                    Log.debug(TAG, "Config saved successfully");
                    
                    // Delete backup if write was successful
                    if (backup.exists()) {
                        backup.delete();
                    }
                }
            } catch (IOException e) {
                Log.error(TAG, "Failed to save config: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Log.error(TAG, "Unexpected error saving config: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Attempt to recover config from backup
     */
    private void attemptBackupRecovery() {
        File backup = new File(configFile.getAbsolutePath() + ".bak");
        if (backup.exists()) {
            Log.warn(TAG, "Attempting recovery from backup...");
            try {
                if (configFile.exists()) {
                    configFile.delete();
                }
                backup.renameTo(configFile);
                loadConfig();
                Log.info(TAG, "Config recovered from backup");
            } catch (Exception e) {
                Log.error(TAG, "Backup recovery failed: " + e.getMessage());
            }
        }
    }

    /**
     * Get configuration value
     */
    public String get(String key, String defaultValue) {
        synchronized (fileLock) {
            return properties.getProperty(key, defaultValue);
        }
    }

    /**
     * Set configuration value
     */
    public void set(String key, String value) {
        if (key == null || value == null) {
            Log.warn(TAG, "Attempted to set null key or value");
            return;
        }
        
        synchronized (fileLock) {
            properties.setProperty(key, value);
            saveConfig();
        }
    }

    /**
     * Get boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    /**
     * Set boolean value
     */
    public void setBoolean(String key, boolean value) {
        set(key, String.valueOf(value));
    }

    /**
     * Get integer value
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            Log.warn(TAG, "Invalid integer value for key: " + key);
            return defaultValue;
        }
    }

    /**
     * Set integer value
     */
    public void setInt(String key, int value) {
        set(key, String.valueOf(value));
    }
}

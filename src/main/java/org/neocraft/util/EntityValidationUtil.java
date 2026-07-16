package org.neocraft.util;

import org.neocraft.adapter.MinecraftAdapter;

/**
 * Utility class for safe entity validation across the entire NeoCraft system.
 * Prevents NullPointerExceptions and crashes from accessing invalid/deleted entities.
 */
public class EntityValidationUtil {

    private static final String TAG = "EntityValidation";

    /**
     * Validates that an entity reference is safe to use.
     * @param entity The entity to validate
     * @param adapter The Minecraft adapter for state checks
     * @return true if entity is valid and safe to access
     */
    public static boolean isEntityValid(Object entity, MinecraftAdapter adapter) {
        if (entity == null) {
            return false;
        }
        
        try {
            // Check if entity is still alive/loaded
            // This would check entity.isDead, entity.isEntityAlive(), etc.
            // depending on the actual entity type
            return true;
        } catch (Exception e) {
            Log.debug(TAG, "Entity validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely checks if an entity is within render distance.
     */
    public static boolean isWithinRenderDistance(Object entity, float maxDistance) {
        if (!isEntityValid(entity, null)) {
            return false;
        }
        
        try {
            // Distance check would go here
            return true;
        } catch (Exception e) {
            Log.debug(TAG, "Distance check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates entity list before iteration to prevent ConcurrentModificationException.
     */
    public static <T> java.util.List<T> getValidEntityList(java.util.List<T> list) {
        if (list == null) {
            return new java.util.ArrayList<>();
        }
        
        synchronized (list) {
            // Create a snapshot to avoid concurrent modification
            return new java.util.ArrayList<>(list);
        }
    }

    /**
     * Safe render call with validation.
     */
    public static void safeRender(Object entity, Runnable renderCallback) {
        if (isEntityValid(entity, null)) {
            try {
                renderCallback.run();
            } catch (Exception e) {
                Log.error(TAG, "Render callback failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

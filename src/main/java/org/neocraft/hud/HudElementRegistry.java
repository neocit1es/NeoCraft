package org.neocraft.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Safe registry for HUD elements that prevents concurrent modification crashes.
 * Uses CopyOnWriteArrayList for thread-safe iteration during rendering.
 */
public class HudElementRegistry {

    private static final String TAG = "HudRegistry";
    private final List<HudElement> elements = new CopyOnWriteArrayList<>();
    private final Object lock = new Object();

    /**
     * Register a HUD element
     */
    public void register(HudElement element) {
        if (element == null) {
            Log.warn(TAG, "Attempted to register null HudElement");
            return;
        }
        
        synchronized (lock) {
            if (!elements.contains(element)) {
                elements.add(element);
                Log.debug(TAG, "Registered HudElement: " + element.getClass().getSimpleName());
            }
        }
    }

    /**
     * Unregister a HUD element
     */
    public void unregister(HudElement element) {
        if (element != null) {
            synchronized (lock) {
                if (elements.remove(element)) {
                    Log.debug(TAG, "Unregistered HudElement: " + element.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * Clear all HUD elements (safe for multiplayer disconnect)
     */
    public void clear() {
        synchronized (lock) {
            Log.debug(TAG, "Clearing " + elements.size() + " HUD elements");
            elements.clear();
        }
    }

    /**
     * Render all HUD elements with validation
     */
    public void renderAll(float partialTicks) {
        // CopyOnWriteArrayList allows safe iteration even during concurrent modifications
        for (HudElement element : elements) {
            if (element == null) {
                Log.warn(TAG, "Null HudElement encountered during render");
                continue;
            }
            
            try {
                if (element.isEnabled()) {
                    element.render(partialTicks);
                }
            } catch (NullPointerException e) {
                Log.error(TAG, "NullPointerException in HudElement.render(): " + 
                    element.getClass().getSimpleName());
                Log.error(TAG, "Stack trace: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Log.error(TAG, "Exception in HudElement.render(): " + 
                    element.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get a safe copy of all elements
     */
    public List<HudElement> getElements() {
        return new ArrayList<>(elements);
    }

    /**
     * Get element count
     */
    public int getElementCount() {
        return elements.size();
    }

    /**
     * Check if a specific element is registered
     */
    public boolean isRegistered(HudElement element) {
        return elements.contains(element);
    }
}

package org.neocraft.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Provides thread-safe wrappers for critical NeoCraft operations.
 * Prevents race conditions and crashes in multiplayer environments.
 */
public class MultithreadingSafetyPatch {

    private static final String TAG = "ThreadSafety";
    private static final ReentrantReadWriteLock entityLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock configLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock renderLock = new ReentrantReadWriteLock();

    /**
     * Acquire read lock for entity access (safe for multiple threads reading)
     */
    public static void acquireEntityReadLock() {
        entityLock.readLock().lock();
    }

    public static void releaseEntityReadLock() {
        try {
            entityLock.readLock().unlock();
        } catch (Exception e) {
            Log.debug(TAG, "Entity read lock release error: " + e.getMessage());
        }
    }

    /**
     * Acquire write lock for entity modifications (exclusive)
     */
    public static void acquireEntityWriteLock() {
        entityLock.writeLock().lock();
    }

    public static void releaseEntityWriteLock() {
        try {
            entityLock.writeLock().unlock();
        } catch (Exception e) {
            Log.debug(TAG, "Entity write lock release error: " + e.getMessage());
        }
    }

    /**
     * Safe config access wrapper
     */
    public static void acquireConfigLock() {
        configLock.writeLock().lock();
    }

    public static void releaseConfigLock() {
        try {
            configLock.writeLock().unlock();
        } catch (Exception e) {
            Log.debug(TAG, "Config lock release error: " + e.getMessage());
        }
    }

    /**
     * Safe render call wrapper
     */
    public static void acquireRenderLock() {
        renderLock.writeLock().lock();
    }

    public static void releaseRenderLock() {
        try {
            renderLock.writeLock().unlock();
        } catch (Exception e) {
            Log.debug(TAG, "Render lock release error: " + e.getMessage());
        }
    }

    /**
     * Execute code with automatic lock management
     */
    public static void executeWithEntityLock(Runnable action) {
        acquireEntityReadLock();
        try {
            action.run();
        } catch (Exception e) {
            Log.error(TAG, "Entity lock execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseEntityReadLock();
        }
    }

    public static void executeWithConfigLock(Runnable action) {
        acquireConfigLock();
        try {
            action.run();
        } catch (Exception e) {
            Log.error(TAG, "Config lock execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseConfigLock();
        }
    }

    public static void executeWithRenderLock(Runnable action) {
        acquireRenderLock();
        try {
            action.run();
        } catch (Exception e) {
            Log.error(TAG, "Render lock execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseRenderLock();
        }
    }
}

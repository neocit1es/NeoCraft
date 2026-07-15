package org.neocraft.test;

import org.junit.jupiter.api.Test;
import org.neocraft.util.MultithreadingSafetyPatch;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MultithreadingSafetyPatch.
 * Validates thread-safe lock management.
 */
class MultithreadingSafetyPatchTest {

    @Test
    void testEntityLockAcquireAndRelease() {
        // Should not throw
        assertDoesNotThrow(() -> {
            MultithreadingSafetyPatch.acquireEntityReadLock();
            MultithreadingSafetyPatch.releaseEntityReadLock();
        });
    }

    @Test
    void testEntityWriteLock() {
        // Should not throw
        assertDoesNotThrow(() -> {
            MultithreadingSafetyPatch.acquireEntityWriteLock();
            MultithreadingSafetyPatch.releaseEntityWriteLock();
        });
    }

    @Test
    void testConfigLock() {
        // Should not throw
        assertDoesNotThrow(() -> {
            MultithreadingSafetyPatch.acquireConfigLock();
            MultithreadingSafetyPatch.releaseConfigLock();
        });
    }

    @Test
    void testRenderLock() {
        // Should not throw
        assertDoesNotThrow(() -> {
            MultithreadingSafetyPatch.acquireRenderLock();
            MultithreadingSafetyPatch.releaseRenderLock();
        });
    }

    @Test
    void testExecuteWithEntityLock() {
        AtomicInteger counter = new AtomicInteger(0);
        
        MultithreadingSafetyPatch.executeWithEntityLock(() -> {
            counter.incrementAndGet();
        });
        
        assertEquals(1, counter.get(), "Runnable should be executed");
    }

    @Test
    void testExecuteWithConfigLock() {
        AtomicInteger counter = new AtomicInteger(0);
        
        MultithreadingSafetyPatch.executeWithConfigLock(() -> {
            counter.incrementAndGet();
        });
        
        assertEquals(1, counter.get(), "Runnable should be executed");
    }

    @Test
    void testExecuteWithRenderLock() {
        AtomicInteger counter = new AtomicInteger(0);
        
        MultithreadingSafetyPatch.executeWithRenderLock(() -> {
            counter.incrementAndGet();
        });
        
        assertEquals(1, counter.get(), "Runnable should be executed");
    }

    @Test
    void testExecuteWithExceptionHandling() {
        // Should not throw even if runnable throws
        assertDoesNotThrow(() -> {
            MultithreadingSafetyPatch.executeWithEntityLock(() -> {
                throw new RuntimeException("Test exception");
            });
        }, "Lock execution should handle exceptions gracefully");
    }

    @Test
    void testLockReleaseOnException() {
        AtomicInteger releaseCount = new AtomicInteger(0);
        
        // Manually test lock release after exception
        MultithreadingSafetyPatch.executeWithEntityLock(() -> {
            throw new RuntimeException("Test");
        });
        
        // If lock wasn't released, this would deadlock
        MultithreadingSafetyPatch.executeWithEntityLock(() -> {
            releaseCount.incrementAndGet();
        });
        
        assertEquals(1, releaseCount.get(), "Lock should be released after exception");
    }

    @Test
    void testConcurrentLockAccess() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger(0);
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                MultithreadingSafetyPatch.executeWithEntityLock(() -> {
                    successCount.incrementAndGet();
                });
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                MultithreadingSafetyPatch.executeWithEntityLock(() -> {
                    successCount.incrementAndGet();
                });
            }
        });

        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        assertEquals(20, successCount.get(), "All operations should complete");
    }

    @Test
    void testReadLockAllowsConcurrentReads() throws InterruptedException {
        AtomicInteger readCount = new AtomicInteger(0);
        
        Thread reader1 = new Thread(() -> {
            MultithreadingSafetyPatch.acquireEntityReadLock();
            try {
                readCount.incrementAndGet();
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                MultithreadingSafetyPatch.releaseEntityReadLock();
            }
        });

        Thread reader2 = new Thread(() -> {
            MultithreadingSafetyPatch.acquireEntityReadLock();
            try {
                readCount.incrementAndGet();
            } finally {
                MultithreadingSafetyPatch.releaseEntityReadLock();
            }
        });

        reader1.start();
        // reader2 should acquire read lock immediately (parallel reads)
        reader2.start();
        
        reader1.join();
        reader2.join();
        
        assertEquals(2, readCount.get(), "Both readers should complete");
    }
}

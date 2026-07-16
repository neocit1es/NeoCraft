package org.neocraft.client;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Captures and logs crash information for debugging multiplayer issues.
 * Generates detailed crash reports with full stack traces.
 */
public class CrashReporter {

    private static final String TAG = "CrashReporter";
    private static final File CRASH_DIR = new File("crash-reports");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    static {
        if (!CRASH_DIR.exists()) {
            CRASH_DIR.mkdirs();
        }
    }

    /**
     * Report a crash with full context
     */
    public static void reportCrash(String module, String description, Throwable throwable) {
        Log.error(TAG, "CRASH REPORTED in " + module + ": " + description);
        
        try {
            String timestamp = dateFormat.format(new Date());
            File crashFile = new File(CRASH_DIR, "crash_" + timestamp + ".log");
            
            try (FileWriter fw = new FileWriter(crashFile);
                 PrintWriter pw = new PrintWriter(fw)) {
                
                pw.println("=== NeoCraft Crash Report ===");
                pw.println("Timestamp: " + new Date());
                pw.println("Module: " + module);
                pw.println("Description: " + description);
                pw.println();
                
                pw.println("=== System Information ===");
                pw.println("Java Version: " + System.getProperty("java.version"));
                pw.println("OS Name: " + System.getProperty("os.name"));
                pw.println("OS Version: " + System.getProperty("os.version"));
                pw.println();
                
                pw.println("=== Thread Information ===");
                pw.println("Current Thread: " + Thread.currentThread().getName());
                pw.println("Active Threads: " + Thread.activeCount());
                pw.println();
                
                pw.println("=== Exception Stack Trace ===");
                if (throwable != null) {
                    throwable.printStackTrace(pw);
                    pw.println();
                    
                    // Print cause chain
                    Throwable cause = throwable.getCause();
                    int depth = 1;
                    while (cause != null && depth < 10) {
                        pw.println("Caused by (" + depth + "): " + cause.getClass().getName());
                        cause.printStackTrace(pw);
                        pw.println();
                        cause = cause.getCause();
                        depth++;
                    }
                }
                
                pw.println("=== End of Report ===");
                Log.info(TAG, "Crash report saved to: " + crashFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.error(TAG, "Failed to write crash report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Report a non-fatal warning that could lead to crashes
     */
    public static void reportWarning(String module, String description) {
        Log.warn(TAG, "WARNING in " + module + ": " + description);
    }

    /**
     * Get the last crash report file
     */
    public static File getLastCrashReport() {
        File[] files = CRASH_DIR.listFiles((dir, name) -> name.startsWith("crash_"));
        if (files == null || files.length == 0) {
            return null;
        }
        
        File latest = files[0];
        for (File f : files) {
            if (f.lastModified() > latest.lastModified()) {
                latest = f;
            }
        }
        return latest;
    }
}

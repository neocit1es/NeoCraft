#!/bin/bash
mkdir -p src/main/java/org/neocraft/client src/main/java/org/neocraft/adapter src/main/java/org/neocraft/events src/test/java/org/neocraft

cat << 'EOF' > settings.gradle
rootProject.name = 'NeoCraft'
EOF

cat << 'EOF' > build.gradle
plugins { id 'java' }
group = 'org.neocraft'
version = '1.0.0'
java { sourceCompatibility = JavaVersion.VERSION_17 }
repositories { mavenCentral() }
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.0'
}
test { useJUnitPlatform() }
EOF

cat << 'EOF' > .gitignore
.gradle/
build/
.idea/
*.iml
*.zip
*.class
EOF

cat << 'EOF' > src/main/java/org/neocraft/client/NeoCraft.java
package org.neocraft.client;
import org.neocraft.adapter.Adapter;
import org.neocraft.events.EventManager;
public class NeoCraft {
    private static NeoCraft instance;
    private final Adapter adapter;
    private final EventManager eventManager;
    private NeoCraft(Adapter adapter) { this.adapter = adapter; this.eventManager = new EventManager(); }
    public static void init(Adapter adapter) { if (instance != null) throw new IllegalStateException("Already initialized!"); instance = new NeoCraft(adapter); }
    public static NeoCraft get() { if (instance == null) throw new IllegalStateException("Not initialized!"); return instance; }
    public Adapter adapter() { return adapter; }
    public EventManager events() { return eventManager; }
    public void shutdown() { instance = null; }
}
EOF

cat << 'EOF' > src/main/java/org/neocraft/adapter/Adapter.java
package org.neocraft.adapter;
public interface Adapter {
    String getGameDirectory();
    Object getMinecraftInstance();
    void drawString(String text, float x, float y, int color);
    void drawRect(float x, float y, float width, float height, int color);
    String getFontRenderer();
    void saveConfig(String key, String value);
    String loadConfig(String key);
}
EOF

cat << 'EOF' > src/main/java/org/neocraft/adapter/McpAdapter.java
package org.neocraft.adapter;
public class McpAdapter implements Adapter {
    @Override public String getGameDirectory() { return "./.minecraft"; }
    @Override public Object getMinecraftInstance() { return null; }
    @Override public void drawString(String text, float x, float y, int color) {}
    @Override public void drawRect(float x, float y, float width, float height, int color) {}
    @Override public String getFontRenderer() { return "StubFontRenderer"; }
    @Override public void saveConfig(String key, String value) {}
    @Override public String loadConfig(String key) { return "default"; }
}
EOF

cat << 'EOF' > src/main/java/org/neocraft/events/EventManager.java
package org.neocraft.events;
import java.util.ArrayList;
import java.util.List;
public class EventManager {
    private final List<Object> listeners = new ArrayList<>();
    public void register(Object listener) { listeners.add(listener); }
    public void unregister(Object listener) { listeners.remove(listener); }
    public void post(Object event) {}
}
EOF

cat << 'EOF' > src/main/java/org/neocraft/events/TickEvent.java
package org.neocraft.events;
public class TickEvent {}
EOF

cat << 'EOF' > src/main/java/org/neocraft/events/Render2DEvent.java
package org.neocraft.events;
public class Render2DEvent {
    public final float partialTicks;
    public Render2DEvent(float partialTicks) { this.partialTicks = partialTicks; }
}
EOF

cat << 'EOF' > src/main/java/org/neocraft/events/KeyInputEvent.java
package org.neocraft.events;
public class KeyInputEvent {
    public final int keyCode;
    public KeyInputEvent(int keyCode) { this.keyCode = keyCode; }
}
EOF

cat << 'EOF' > src/test/java/org/neocraft/AdapterStubTest.java
package org.neocraft;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.neocraft.client.NeoCraft;
import org.neocraft.adapter.McpAdapter;
public class AdapterStubTest {
    @Test
    public void testInitialization() {
        NeoCraft.init(new McpAdapter());
        assertNotNull(NeoCraft.get());
        assertEquals("StubFontRenderer", NeoCraft.get().adapter().getFontRenderer());
        NeoCraft.get().shutdown();
    }
}
EOF
echo "✅ Script created all files!"

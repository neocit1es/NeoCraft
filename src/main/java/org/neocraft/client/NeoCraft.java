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

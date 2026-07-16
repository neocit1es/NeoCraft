package org.neocraft.events;
import java.util.ArrayList;
import java.util.List;
public class EventManager {
    private final List<Object> listeners = new ArrayList<>();
    public void register(Object listener) { listeners.add(listener); }
    public void unregister(Object listener) { listeners.remove(listener); }
    public void post(Object event) {}
}

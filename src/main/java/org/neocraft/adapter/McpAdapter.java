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

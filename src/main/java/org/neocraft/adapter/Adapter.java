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

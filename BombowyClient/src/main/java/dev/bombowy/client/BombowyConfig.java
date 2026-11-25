package dev.bombowy.client;

import java.util.LinkedHashMap;
import java.util.Map;

public class BombowyConfig {
    public boolean bombBadgeEnabled = true;
    public boolean preserveServerRankColors = true;
    public boolean killEffectsEnabled = true;
    public String selectedKillEffect = "shark";
    public boolean closeGuiOnKillTest = true;
    public boolean coldOwnerTimerEnabled = true;
    public int coldOwnerDurationSeconds = 15;
    public boolean customTextureEventItems = true;
    public boolean hudVisible = true;
    public String activeCategory = "Wszystkie";
    public String searchQuery = "";

    public final Map<String, Boolean> modules = new LinkedHashMap<>();

    public BombowyConfig() {
        modules.put("Hitboxy", true);
        modules.put("AutoSprint", false);
        modules.put("Efekty zabójstwa", true);
        modules.put("Czysty obraz", true);
        modules.put("Bez cząsteczek", true);
        modules.put("Boost kolorów", true);
        modules.put("Jakość 2K/4K", true);
        modules.put("Jasność", true);
        modules.put("Pokaż FPS", true);
        modules.put("Bomba przy nicku", true);
        modules.put("Kolory rang serwera", true);
        modules.put("Licznik COLDOWNER", true);
        modules.put("Przedmioty eventowe TXT", true);
    }

    public boolean isEnabled(String name) {
        return modules.getOrDefault(name, true);
    }

    public void toggle(String name) {
        modules.put(name, !isEnabled(name));
    }
}

package ru.newclient.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    public static void init() {
        // Добавляем тестовые модули, чтобы они появились в меню
        modules.add(new Module("AutoSprint", "Movement"));
        modules.add(new Module("KillAura", "Combat"));
        modules.add(new Module("ESP", "Render"));
        modules.add(new Module("Fullbright", "Render"));
    }

    public static void onTick() {
        for (Module m : modules) {
            if (m.enabled) {
                m.onTick();
            }
        }
    }

    public static Module getModule(String name) {
        for (Module m : modules) {
            if (m.name.equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}

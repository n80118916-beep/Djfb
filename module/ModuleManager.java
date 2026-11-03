package ru.forgeclient.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    static {
        modules.add(new Module("KillAura", "Combat"));
        modules.add(new Module("Velocity", "Combat"));
        modules.add(new Module("AutoSprint", "Movement"));
        modules.add(new Module("Flight", "Movement"));
        modules.add(new Module("ESP", "Render"));
        modules.add(new Module("Fullbright", "Render"));
    }
}

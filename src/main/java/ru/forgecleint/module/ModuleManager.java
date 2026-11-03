package ru.forgeclient.module;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    static {
        modules.add(new ru.forgeclient.module.impl.combat.KillAura()); // Добавляем нашу КиллАуру
        modules.add(new Module("Velocity", "Combat"));
        modules.add(new Module("AutoSprint", "Movement"));
        modules.add(new Module("Flight", "Movement"));
        modules.add(new Module("ESP", "Render"));
        modules.add(new Module("Fullbright", "Render"));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Module m : modules) {
                if (m.enabled) {
                    m.onUpdate();
                }
            }
        }
    }
}

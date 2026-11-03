package ru.forgeclient.module;

import net.minecraft.client.Minecraft;

public class Module {
    public String name;
    public String category;
    public boolean enabled;
    public static Minecraft mc = Minecraft.getInstance();

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    // Этот метод будет вызываться каждый тик игры (для логики ауры)
    public void onUpdate() {}
}

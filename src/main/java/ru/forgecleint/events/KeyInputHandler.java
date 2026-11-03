package ru.forgeclient.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import ru.forgeclient.gui.MenuScreen;

public class KeyInputHandler {
    public static final KeyBinding OPEN_GUI = new KeyBinding("key.forgeclient.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, "ForgeClient");

    public KeyInputHandler() {
        ClientRegistry.registerKeyBinding(OPEN_GUI);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // ИСПРАВЛЕНО: screen вместо currentScreen, setScreen вместо displayGuiScreen
            if (OPEN_GUI.isKeyDown() && Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new MenuScreen());
            }
        }
    }
}

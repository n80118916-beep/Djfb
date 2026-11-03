package ru.forgeclient.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
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
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // ИСПРАВЛЕНО: isDown() вместо isPressed()
        if (OPEN_GUI.isDown() && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new MenuScreen());
        }
    }
}

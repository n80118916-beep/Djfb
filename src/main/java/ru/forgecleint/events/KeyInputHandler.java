package ru.forgeclient.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import ru.forgeclient.gui.MenuScreen;

public class KeyInputHandler {
    // Регистрируем кнопку Right Shift
    public static final KeyBinding OPEN_GUI = new KeyBinding("key.forgeclient.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, "ForgeClient");

    public KeyInputHandler() {
        ClientRegistry.registerKeyBinding(OPEN_GUI);
    }

    // Используем событие нажатия клавиши (работает безотказно)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Проверяем, что нажата именно наша кнопка
        if (OPEN_GUI.isPressed()) {
            // Если в игре сейчас нет открытых меню (чата, инвентаря)
            if (Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new MenuScreen());
            }
        }
    }
}

package ru.newclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.newclient.gui.ClickGUI;
import ru.newclient.module.ModuleManager;

public class NewClient implements ClientModInitializer {
    
    // Создаем две кнопки для надежности (Right Shift и Right Control)
    public static KeyBinding openGuiKey;
    public static KeyBinding openGuiKeyAlt;

    @Override
    public void onInitializeClient() {
        // Инициализируем модули
        ModuleManager.init();

        // 1. Основная кнопка - Правый Shift
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.newclient.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "TusiClient"
        ));

        // 2. Дублирующая кнопка - Правый Control (на случай, если на телефоне шифт глючит)
        openGuiKeyAlt = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.newclient.gui_alt",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "TusiClient"
        ));

        // Слушаем каждый тик игры
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Обновляем модули
            ModuleManager.onTick();
            
            // Проверка нажатия основной кнопки (Right Shift)
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) { // Открываем только если нет другого экрана (чата, инвентаря)
                    client.openScreen(new ClickGUI());
                }
            }

            // Проверка нажатия дублирующей кнопки (Right Control)
            while (openGuiKeyAlt.wasPressed()) {
                if (client.currentScreen == null) {
                    client.openScreen(new ClickGUI());
                }
            }
        });
    }
}

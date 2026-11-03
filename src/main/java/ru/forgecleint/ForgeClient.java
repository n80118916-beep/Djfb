package ru.forgeclient;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.forgeclient.events.KeyInputHandler;
import ru.forgeclient.module.ModuleManager;

@Mod("forgeclient")
public class ForgeClient {
    public ForgeClient() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        
        // Регистрируем обработчик клавиш
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        
        // Регистрируем менеджер модулей (чтобы аура обновлялась)
        MinecraftForge.EVENT_BUS.register(new ModuleManager());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        System.out.println("ForgeClient initialized!");
    }
}

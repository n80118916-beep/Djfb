package ru.forgeclient;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.forgeclient.events.KeyInputHandler;

@Mod("forgeclient")
public class ForgeClient {
    public ForgeClient() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        System.out.println("ForgeClient initialized!");
    }
}

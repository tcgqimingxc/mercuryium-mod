package com.example.mercuryium;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = mercuryium.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = mercuryium.MODID, value = Dist.CLIENT)
public class mercuryiumClient {
    public mercuryiumClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        Runtime.getRuntime().addShutdownHook(new Thread(AsyncRenderingManager::shutdown, "mercuryium-shutdown"));
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        mercuryium.LOGGER.info("HELLO FROM CLIENT SETUP");
        mercuryium.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        AsyncRenderingManager.initialize();
    }

    @SubscribeEvent
    static void onClientConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == ClientConfig.SPEC) {
            AsyncRenderingManager.initialize();
        }
    }
}

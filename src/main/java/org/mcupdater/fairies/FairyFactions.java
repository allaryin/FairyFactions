package org.mcupdater.fairies;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mcupdater.fairies.client.renderer.RenderFairy;
import org.mcupdater.fairies.init.Registry;

@Mod(FairyFactions.MOD_ID)
public class FairyFactions {
	public static final String MOD_ID = "fairyfactions";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public FairyFactions() {
		final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setupCommon);
		eventBus.addListener(this::setupClient);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FairyConfig.SPEC);
	}

	public void setupCommon(final FMLCommonSetupEvent event) {
		LOGGER.info("Setup common");
	}

	public void setupClient(final FMLClientSetupEvent event) {
		LOGGER.info("Setup client");
		RenderingRegistry.registerEntityRenderingHandler(Registry.FAIRY_ENTITY_TYPE, RenderFairy::new);
	}
}

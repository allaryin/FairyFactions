package fairies;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fairies.entity.EntityFairy;
import fairies.event.FairyEventListener;
import fairies.proxy.CommonProxy;
import fairies.world.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = Version.MOD_ID, version = Version.VERSION)
public class FairyFactions {

	@Instance
	public static FairyFactions		INSTANCE;

	@SidedProxy(clientSide = Version.PROXY_CLIENT, serverSide = Version.PROXY_COMMON)
	public static CommonProxy		proxy;

	public static final Logger		LOGGER	= LogManager.getFormatterLogger(Version.MOD_ID);

	private File					BaseDir;
	private static FairyConfig		Config;

	private Spawner					fairySpawner;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		BaseDir = new File(event.getModConfigurationDirectory(), Version.MOD_ID);
		Config = new FairyConfig(event.getSuggestedConfigurationFile());

		if (!BaseDir.exists())
			BaseDir.mkdir();

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FairyEventListener listener = new FairyEventListener();
		MinecraftForge.EVENT_BUS.register(listener);

		proxy.initEntities();
		LOGGER.debug("Registered entities");

		proxy.initChannel(listener);
		LOGGER.debug("Registered channel");

		/*
		FMLCommonHandler.instance().bus().register(this);
		LOGGER.debug("Registered events");
		*/

		proxy.initGUI();
		LOGGER.debug("Registered GUI");

		LOGGER.info("Loaded version %s", Version.VERSION);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		fairySpawner = new Spawner();
		// TODO: move these thresholds into config file
		final int maxNum = 18;
		final int freqNum = 8;
		fairySpawner.setMaxAnimals(maxNum);
		fairySpawner.AddCustomSpawn(EntityFairy.class, freqNum, EnumCreatureType.creature);
		FMLCommonHandler.instance().bus().register(fairySpawner);

        // TODO: register entity localization
		LOGGER.debug("Spawner is a modified version of CustomSpawner, created by DrZhark.");

		proxy.postInit();
	}

	/**
	 * Find a fairy in the world by entity id. This method was in the base class
	 * in the original mod, and I can't find a better place to put it for now...
	 *
	 * @param fairyID
	 * @return The fairy in question, null if not found.
	 */
	public static EntityFairy getFairy(int fairyID) {
		for( WorldServer dim : DimensionManager.getWorlds() ) {
			if( dim != null ) {
				List<Entity> entities = dim.loadedEntityList;
				if( entities != null && !entities.isEmpty() ) {
					for( Entity entity : entities ) {
						if( entity instanceof EntityFairy && entity.getEntityId() == fairyID )
							return (EntityFairy)entity;
					}
				}
			}
		}
		return null;
	}

}

package fairies;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fairies.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Version.MOD_ID, version = Version.VERSION)
public class FairyFactions
{
	// This only works client side
    //static final Minecraft MC = Minecraft.getMinecraft();

	@SidedProxy(clientSide = Version.PROXY_CLIENT, serverSide = Version.PROXY_COMMON)
	public static CommonProxy proxy;
	
    static final Logger  LOGGER  = LogManager.getFormatterLogger(Version.MOD_ID);
    static final Boolean DEV     = Boolean.parseBoolean( System.getProperty("development", "false") );

    static File          BaseDir;
    static Configuration Config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        BaseDir = new File(event.getModConfigurationDirectory(), Version.MOD_ID);
        Config  = new Configuration( event.getSuggestedConfigurationFile() );

        if ( !BaseDir.exists() )
            BaseDir.mkdir();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Need events? Uncomment these:
        //MinecraftForge.EVENT_BUS.register(this);
        //FMLCommonHandler.instance().bus().register(this);
        //LOGGER.debug("Registered events");
    	
    	proxy.initChannel();
    	proxy.initEntities();
    	proxy.initGUI();

        LOGGER.info("Loaded version %s", Version.VERSION);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}

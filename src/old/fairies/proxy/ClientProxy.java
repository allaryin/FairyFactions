package fairies.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fairies.FairyFactions;
import fairies.client.gui.GuiName;
import fairies.client.render.ModelFairy;
import fairies.client.render.RenderFairy;
import fairies.client.render.RenderFish;
import fairies.entity.EntityFairy;
import fairies.entity.FairyEntityFishHook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
		
		initRenderer();
	}
	
	private void initRenderer() {
	    // map.put(FRY_EntityFairy.class, new FRY_RenderFairy(new FRY_ModelFairy(), 0.25F));
	    // map.put(FRY_EntityFishHook.class, new FRY_RenderFish());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, 
				new RenderFairy(new ModelFairy(), 0.25f));
		RenderingRegistry.registerEntityRenderingHandler(FairyEntityFishHook.class, new RenderFish());
	}
	
    @Override
    public void initGUI() {
    	// TODO: something goes here, probably
    }
    
    @Override
    public void openRenameGUI(EntityFairy fairy) {
		FairyFactions.LOGGER.info("ClientProxy.openRenameGUI");

    	if( fairy.isRuler(getCurrentPlayer()) ) {
    		FairyFactions.LOGGER.info("ClientProxy.openRenameGUI: current player is ruler, displaying gui");
    		fairy.setNameEnabled(false);
    		Minecraft.getMinecraft().displayGuiScreen(new GuiName(fairy));
    	}
    }
	
    @Override
    public EntityPlayer getCurrentPlayer() {
		return (EntityPlayer)Minecraft.getMinecraft().thePlayer; 
	}

}

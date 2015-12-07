package fairies.proxy;

import cpw.mods.fml.common.registry.EntityRegistry;
import fairies.FairyFactions;
import fairies.entity.EntityFairy;

public class CommonProxy {

	public void initChannel() {
		// TODO Auto-generated method stub
		
	}

	public void initEntities() {
		EntityRegistry.registerGlobalEntityID( EntityFairy.class, "Fairy", EntityRegistry.findGlobalUniqueEntityId() );
		// defaults reference vanilla's EntityFishhook
		// EntityRegistry.registerModEntity( EntityFairyFishHook.class, "FairyFishhook", EntityRegistry.findGlobalUniqueEntityId(), FairyFactions.INSTANCE, 64, 4, true );
	}

	public void initGUI() {
		// TODO Auto-generated method stub
		
	}

	public void preInit() {
		// TODO Auto-generated method stub
		
	}

	public void postInit() {
		// TODO Auto-generated method stub
		
	}

	public void sendFairyRename(String s1) {
		// TODO Auto-generated method stub
		
	}

}

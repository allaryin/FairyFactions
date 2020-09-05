package org.mcupdater.fairies.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mcupdater.fairies.FairyFactions;
import org.mcupdater.fairies.entity.FairyEntity;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registry {

	public static EntityType<FairyEntity> FAIRY_ENTITY_TYPE;

	@SubscribeEvent
	public static void registerEntity(final RegistryEvent.Register<EntityType<?>> event) {
		FAIRY_ENTITY_TYPE = EntityType.Builder.create(FairyEntity::new, EntityClassification.CREATURE)
				.setTrackingRange(64).setUpdateInterval(1).size(0.6F, 0.85F)
				.build(FairyEntity.NAME);
		FAIRY_ENTITY_TYPE.setRegistryName(FairyFactions.MOD_ID, FairyEntity.NAME);
		event.getRegistry().register(FAIRY_ENTITY_TYPE);
	}
}

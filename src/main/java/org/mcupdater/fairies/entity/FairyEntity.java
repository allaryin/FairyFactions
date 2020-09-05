package org.mcupdater.fairies.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FairyEntity extends AnimalEntity {
	public static final String NAME = "fairy";

	public FairyEntity(EntityType<? extends FairyEntity> fairy, World world) {
		super(fairy, world);
	}

	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		// This is probably createChild(), so we can safely do nothing here since we're not allowing breeding
		return null;
	}

	@Override
	public boolean canBreed() {	return false; }
}

package org.mcupdater.fairies.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.mcupdater.fairies.FairyConfig;

public class FairyEntity extends AnimalEntity {
	public static final String NAME = "fairy";

	private final int skinVariant;

	private boolean isQueen;

	public FairyEntity(EntityType<? extends FairyEntity> fairy, World world) {
		super(fairy, world);

		skinVariant = rand.nextInt(4);
		isQueen = false;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new WaterAvoidingRandomWalkingGoal(this, this.getAIMoveSpeed()));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (!world.isRemote) {
			// server-side data updates here
		} else {
			// client-side visual/etc updates here
		}
	}

	public int skinVariant() { return skinVariant; }
	public boolean isQueen() { return isQueen; }

	public static AttributeModifierMap.MutableAttribute getAttributes() {
		double max_health = 15.0D;
		double movement_speed = 1.0D;

		// NOTE: this may not work since the attributes are only got at initial load time?
		if (FairyConfig.SPEC.isLoaded()) {
			max_health = FairyConfig.GENERAL.healthBase.get();
			movement_speed = FairyConfig.GENERAL.speedBase.get();
		}

		return MobEntity.func_233666_p_()
				.func_233815_a_(Attributes.field_233818_a_, max_health)
				.func_233815_a_(Attributes.field_233821_d_, movement_speed);
	}

	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		// This is probably createChild(), so we can safely do nothing here since we're not allowing breeding
		return null;
	}

	@Override
	public boolean canBreed() {	return false; }
}

package fairies.ai;

import fairies.FairyConfig;
import fairies.FairyFactions;
import fairies.entity.EntityFairy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;

public class FairyAIFear extends EntityAIBase {
	
	private EntityFairy theFairy;
	protected double speed;
	
	public FairyAIFear(EntityFairy fairy, double speedIn) {
		this.theFairy = fairy;
		this.speed = speedIn;
		
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase entityFear = this.theFairy.getEntityFear();
		if( entityFear == null ) {
			return false;
		} else if( entityFear.isDead ) {
			this.theFairy.setEntityFear(null);
			return false;
		} else if( !this.theFairy.hasPath() 
				&& this.theFairy.canEntityBeSeen(entityFear) 
				&& this.theFairy.willCower() ) {
			
			float dist = this.theFairy.getDistanceToEntity(entityFear);
			if( dist > FairyConfig.BEHAVIOR_FEAR_RANGE ) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void startExecuting() {
		FairyFactions.LOGGER.debug(this.theFairy.toString()+": starting fear");
		final PathEntity dest = this.theFairy.roam(this.theFairy.getEntityFear(), this.theFairy, EntityFairy.PATH_AWAY);
		this.theFairy.setCryTime( this.theFairy.getCryTime() + 120 );
		this.theFairy.getNavigator().setPath(dest, this.speed);
	}
	
	@Override
	public boolean continueExecuting() {
		return !this.theFairy.getNavigator().noPath();
	}
	
	/*
	 * Original fear handler method from EntityFairy
	 * 
	private void handleFear() {
		if (getEntityFear() != null) {
			if (getEntityFear().isDead) {
				// Don't fear the dead.
				setEntityFear(null);
			} else if (!hasPath() && canEntityBeSeen(getEntityFear())
					&& willCower()) {
				float dist = getDistanceToEntity(getEntityFear());

				// Run from entityFear if you can see it and it is close.
				if (dist < FairyConfig.BEHAVIOR_FEAR_RANGE) {
					PathEntity dest = roam(getEntityFear(), this, PATH_AWAY);

					if (dest != null) {
						// setPathToEntity(dest);
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());
						setCryTime(getCryTime() + 120);
					}
				}
			}
		}
	}
	*/

}

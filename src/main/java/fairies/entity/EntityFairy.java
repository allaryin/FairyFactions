package fairies.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFairy extends EntityAnimal {
	
	// TODO: put these into config file
	public static final double	DEF_BASE_HEALTH	= 15.0D;
	public static final float	DEF_BASE_SPEED	= 0.9F;
	public static final float	DEF_SCOUT_SPEED	= 1.05F;
	public static final float	DEF_WITHER_MULT = 0.75F;
	
	public static final double	DEF_FLOAT_RATE		= -0.2D; // fall speed
	public static final double	DEF_FLAP_RATE		= 0.15D; // fly speed
	public static final double	DEF_SOLO_FLAP_MULT	= 1.25D; // bonus to flight while unburdened
	public static final double	DEF_LIFTOFF_MULT	= 2.0D;  // bonus to flight when launching
	
	public static final int		DEF_MAX_PARTICLES	= 5;
	
	public static final float	DEF_PATH_RANGE		= 16F;   // how far will we path to something?
	public static final float	DEF_PURSUE_RANGE	= DEF_PATH_RANGE; // how far will we chase something?
	public static final float	DEF_DEFEND_RANGE	= DEF_PURSUE_RANGE / 2; // how close will guards protect the queen from?
	public static final float	DEF_FEAR_RANGE		= 12F;   // how far will we flee from something?

	public static final int		DEF_AGGRO_TIMER		= 15;    // how long will tame fairies stay mad? (3x for wild)
		
	public float sinage;	// what does this mean?
	public int flyTime;
	public boolean cower;
	private int postX, postY, postZ;
	public boolean createGroup;
	private boolean didHearts;
	private int particleCount;
	private boolean wasFishing;
	private Entity fishEntity;
	private boolean flyBlocked;
	private int healTime;
	private int cryTime;
	private Entity entityFear;
	private int listActions;
	private int loseInterest;
	private Entity ruler;
	
	public EntityFairy(World world) {
		super(world);
		this.setSize(0.6F, 0.85F);
		
		// fairy-specific init
		setSkin(rand.nextInt(4));
		setJob(rand.nextInt(4));
		setSpecialJob(false);
		setFaction(0);
		setFairyName(rand.nextInt(16), rand.nextInt(16));
		
		setFlymode(false);
		this.sinage = rand.nextFloat();
		this.flyTime = 400 + rand.nextInt(200);
		this.cower = rand.nextBoolean();
		this.postX = this.postY = this.postZ = -1;
		
		// TODO: Set texture
	}
	
	// DataWatcher object indices
	protected final static int B_FLAGS		= 17;
	protected final static int B_TYPE		= 18;	// skin, job, faction
	protected final static int B_NAME_ORIG	= 19;	// generated original name
	protected final static int S_OWNER		= 20;	// owner name
	protected final static int B_FLAGS2		= 21;	// capabilities, activities, etc...
	protected final static int B_HEALTH		= 22;
	protected final static int S_NAME_REAL	= 23;	// custom name
	protected final static int I_TOOL		= 24;	// temporary tool

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(B_FLAGS,		new Byte((byte)0));
		dataWatcher.addObject(B_FLAGS2,		new Byte((byte)0));
		dataWatcher.addObject(B_TYPE,		new Byte((byte)0));
		dataWatcher.addObject(B_HEALTH,		new Byte((byte)0));
		dataWatcher.addObject(B_NAME_ORIG,	new Byte((byte)0));
		dataWatcher.addObject(S_OWNER,		"");
		dataWatcher.addObject(S_NAME_REAL,	"");
		dataWatcher.addObject(I_TOOL,		new Integer(0));
	}
	
	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(DEF_BASE_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(DEF_BASE_SPEED);
        // this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        // this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(10.0D);
    }
	
	@SuppressWarnings("unused")
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if( this.createGroup ) {
			createGroup = false;
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(boundingBox.minY) - 1;
			int k = MathHelper.floor_double(posZ);

			// TODO: FairyGroup.generate()
			/*
			if ((new FRY_FairyGroup(8, 10, getFaction())).generate(worldObj, rand, i, j, k))
            {
                //This is good.
            }
            else
            {
                setDead(); //For singleplayer mode
                //mod_FairyMod.fairyMod.sendFairyDespawn(this);
            }
			*/
		}
		
		if( scout() ) {
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(DEF_SCOUT_SPEED);
		} else {
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(DEF_BASE_SPEED);
		}
		
		if( withered() ) {
			IAttributeInstance speed = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			speed.setBaseValue(speed.getAttributeValue() * DEF_WITHER_MULT);
		}
		
		if( !worldObj.isRemote ) {
			updateWithering();
			setHealth(getHealth());
			setFairyClimbing(flymode() && canFlap() && hasPath() && isCollidedHorizontally);
			if( isSitting() && (ridingEntity != null || !onGround) ) {
				setSitting(false);
			}
			
			setPosted(postY > -1);
		}
		
		if( getHealth() > 0.0F ) {
			// wing animations
			if( !this.onGround ) {
				sinage += 0.75F;
			} else {
				sinage += 0.15F;
			}
			
			if( sinage > Math.PI * 2F) {
				sinage -= Math.PI * 2F;
			}
			
			if( flymode() ) {
				if( !liftOff() && ridingEntity != null && !ridingEntity.onGround && ridingEntity instanceof EntityLiving ) {
					ridingEntity.fallDistance = 0F;
					
					if( ridingEntity.motionY < DEF_FLOAT_RATE ) {
						ridingEntity.motionY = DEF_FLOAT_RATE;
					}
					
					// TODO: research how to find this now
					final boolean isJumping = false; // ((EntityLiving)ridingEntity).isJumping
					if( isJumping && ridingEntity.motionY < DEF_FLAP_RATE && canFlap() ) {
						ridingEntity.motionY = DEF_FLAP_RATE;
					}
				} else {
					if( motionY < DEF_FLOAT_RATE ) {
						motionY = DEF_FLOAT_RATE;
					}
					
					if( canFlap() && checkGroundBelow() && motionY < 0D ) {
						motionY = DEF_FLOAT_RATE * DEF_SOLO_FLAP_MULT;
					}
					
					if( liftOff() && ridingEntity != null ) {
						ridingEntity.fallDistance = 0F;
						motionY = ridingEntity.motionY = DEF_FLAP_RATE * DEF_LIFTOFF_MULT;
					}
				}
			}
			
			if( hearts() != didHearts ) {
				didHearts = !didHearts;
				showHeartsOrSmokeFX(tamed());
			}
			
			++particleCount;
			if( particleCount >= DEF_MAX_PARTICLES ) {
				particleCount = rand.nextInt(DEF_MAX_PARTICLES >> 1);
				
				if( angry() || (crying() && queen()) ) {
					// anger smoke, queens don't cry :P
					worldObj.spawnParticle("smoke", posX, boundingBox.maxY, posZ, 0D, 0D, 0D);
				} else if( crying() ) {
					// crying effect
					worldObj.spawnParticle("splash", posX, boundingBox.maxY, posZ, 0D, 0D, 0D);
				}
				
				if( liftOff() ) {
					// liftoff effect below feet
					worldObj.spawnParticle("explode", posX, boundingBox.minY, posZ, 0D, 0D, 0D);
				}
				
				if( withered() || (rogue() && canHeal()) ) {
					// TODO: more proxying
					/*
					 * Offload to proxy for client-side rendering
					 *
					double a = posX - 0.2D + (0.4D * rand.nextDouble());
					double b = posY + 0.45D + (0.15D * rand.nextDouble());
					double c = posZ - 0.2D + (0.4D * rand.nextDouble());
					EntitySmokeFX smoke = new EntitySmokeFX(worldObj, a,b,c, 0D, 0D, 0D);
					a = 0.3D + (0.15D * rand.nextDouble());
					b = 0.5D + (0.2D * rand.nextDouble());
					c = 0.3D + (0.15D * rand.nextDouble());
					smoke.setRBGColorF((float)a, (float)b, (float)c);
					MC.effectRenderer.addEffect(smoke);
					 */
				}
				
				if( nameEnabled() && tamed() && !rulerName().isEmpty() ) {
					// TODO: proxy display rename gui
				}
			}
			
			processSwinging();
		}
	}// end: onUpdate
	
	@Override
	public float getEyeHeight() {
		if( !worldObj.isRemote && this.onGround && rand.nextBoolean() ) {
			int a = MathHelper.floor_double(posX);
			int b = MathHelper.floor_double(boundingBox.minY);
			int c = MathHelper.floor_double(posZ);
			
			if( isAirySpace(a,b,c) && isAirySpace(a,b+1,c) ) {
				return height * 1.375F;
			}
		}
		return super.getEyeHeight();
	}
	
	@Override
	public boolean isEntityInsideOpaqueBlock() {
		for( int i = 0; i < 8; ++i ) {
			float f = ((float)((i >> 0) % 2) - 0.5F) * width * 0.8F;
            float f1 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
            float f2 = ((float)((i >> 2) % 2) - 0.5F) * width * 0.8F;
            int j = MathHelper.floor_double(posX + (double)f);
            int k = MathHelper.floor_double(posY + (double)super.getEyeHeight() + (double)f1);
            int l = MathHelper.floor_double(posZ + (double)f2);

            if (worldObj.getBlock(j, k, l).isNormalCube()) {
                return true;
            }
		}
		return false;
	}
	
	// Fixes the head shaking glitch.
    @Override
    public void faceEntity(Entity entity, float f, float f1) {
        double d = entity.posX - posX;
        double d2 = entity.posZ - posZ;
        double d1;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving)entity;
            d1 = (posY + (double)(height * 0.85F)) - (entityliving.posY + (double)entityliving.getEyeHeight());
        } else {
            d1 = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2D - (posY + (double)(height * 0.85F));
        }

        double d3 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f2 = (float)((Math.atan2(d2, d) * 180D) / Math.PI) - 90F;
        float f3 = (float)(-((Math.atan2(d1, d3) * 180D) / Math.PI));
        rotationPitch = -updateRotation(rotationPitch, f3, f1);
        rotationYaw = updateRotation(rotationYaw, f2, f);
    }
    
    // Had to redo this because it is private.
    private float updateRotation(float f, float f1, float f2) {
        float f3;

        for (f3 = f1 - f; f3 < -180F; f3 += 360F) { }
        for (; f3 >= 180F; f3 -= 360F) { }

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }
    
    @Override
    protected void fall(float f) {
    	// HAH!
    }
    
    // ---------- behaviors ----------

    // maximum number of times to try pathfinding
	public static final int		MAX_PATHING_TRIES	= 32; 
	public static final float	PATH_TOWARD			= 0F;
	public static final float	PATH_AWAY			= (float)Math.PI;
    
    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();

        if (wasFishing) {
            wasFishing = false;

            if (isSitting() && fishEntity == null) {
                setSitting(false);
            }
        }

        if (isSitting()) {
            handlePosted(false);
            return;
        }

        flyBlocked = checkFlyBlocked();

        if (flyTime > 0) {
            --flyTime;
        }

        boolean liftFlag = false;

        if (flymode()) {
            fallDistance = 0F;

            if (ridingEntity != null) {
                if (entityToAttack != null && ridingEntity == entityToAttack) {
                    flyTime = 200;
                    liftFlag = true;

                    if ((attackTime <= 0) || flyBlocked) {
                        attackTime = 0;
                        attackEntity(ridingEntity, 0F);
                        liftFlag = false;
                    }
                } else if (tamed()) {
                    if (ridingEntity.onGround || ridingEntity.isInWater()) {
                        flyTime = (queen() || scout() ? 60 : 40);

                        if (withered()) {
                            flyTime -= 10;
                        }
                    }
                }
            }

            if (flyTime <= 0 || (flyBlocked && (ridingEntity == null || (entityToAttack != null && ridingEntity == entityToAttack)))) {
                setCanFlap(false);
            } else {
                setCanFlap(true);
            }

            if (ridingEntity == null && (onGround || inWater)) {
                setFlymode(false);
                flyTime = 400 + rand.nextInt(200);

                // Scouts are less likely to want to walk.
                if (scout()) {
                    flyTime /= 3;
                }
            }
        } else {
            if (flyTime <= 0 && !flyBlocked) {
                jump();
                setFlymode(true);
                setCanFlap(true);
                flyTime = 400 + rand.nextInt(200);

                //Scouts are more likely to want to fly.
                if (scout())  {
                    flyTime *= 3;
                }
            }

            if (ridingEntity != null && !flymode()) {
                setFlymode(true);
                setCanFlap(true);
            }

            if (!flymode() && !onGround && fallDistance >= 0.5F && ridingEntity == null) {
                setFlymode(true);
                setCanFlap(true);
                flyTime = 400 + rand.nextInt(200);
            }
        }

        setLiftOff(liftFlag);

        if (healTime > 0) {
            --healTime;
        }

        if (cryTime > 0) {
            --cryTime;

            if (cryTime <= 0) {
                entityFear = null;
            }

            if (cryTime > 600) {
                cryTime = 600;
            }
        }

        ++listActions;

        if (listActions >= 8) {
            listActions = rand.nextInt(3);

            if (angry()) {
                handleAnger();
            } else if (crying()) {
                handleFear();
            } else {
                handleRuler();

                if (medic()) {
                    handleHealing();
                } else if (rogue()) {
                    handleRogue();
                } else {
                    handleSocial();
                }

                handlePosted(true);
            }
        }

        if (worldObj.difficultySetting.getDifficultyId() <= 0 && entityToAttack != null && entityToAttack instanceof EntityPlayer) {
            entityFear = entityToAttack;
            cryTime = Math.max(cryTime, 100);
            setTarget((Entity)null);
        }

        setCrying(cryTime > 0);
        setAngry(entityToAttack != null);
        setCanHeal(healTime <= 0);
    }// end: updateEntityActionState
	
    /**
     * if griniscule is 0F, entity2 will roam towards entity1.
     * if griniscule is pi, entity2 will roam away from entity1.
     * Also, a griniscule is a portmanteau of grin and miniscule.
     * 
     * @param target
     * @param actor
     * @param griniscule 
     * @return
     */
    public PathEntity roam(Entity target, Entity actor, float griniscule) {
        double a = target.posX - actor.posX;
        double b = target.posZ - actor.posZ;
        
        double crazy = Math.atan2(a, b);
        crazy += (rand.nextFloat() - rand.nextFloat()) * 0.25D;
        crazy += griniscule;
        
        double c = actor.posX + (Math.sin(crazy) * 8F);
        double d = actor.posZ + (Math.cos(crazy) * 8F);
        
        int x = MathHelper.floor_double(c);
        int y = MathHelper.floor_double(actor.boundingBox.minY);
        int z = MathHelper.floor_double(d);

        for (int q = 0; q < MAX_PATHING_TRIES; q++) {
            int i = x + rand.nextInt(5) - rand.nextInt(5);
            int j = y + rand.nextInt(5) - rand.nextInt(5);
            int k = z + rand.nextInt(5) - rand.nextInt(5);

            if (j > 4 && j < worldObj.getHeight() - 1 && isAirySpace(i, j, k) && !isAirySpace(i, j - 1, k)) {
                PathEntity dogs = worldObj.getEntityPathToXYZ(actor, i, j, k, DEF_PATH_RANGE, false, false, true, true);

                if (dogs != null) {
                    return dogs;
                }
            }
        }

        return null;
    }
    
    // TODO: combine this with roam()
    public PathEntity roamBlocks(double t, double u, double v, Entity actor, float griniscule) {
        // t is an X coordinate, u is a Y coordinate, v is a Z coordinate.
        // Griniscule of 0.0 means towards, 3.14 means away.
        double a = t - actor.posX;
        double b = v - actor.posZ;
        double crazy = Math.atan2(a, b);
        crazy += (rand.nextFloat() - rand.nextFloat()) * 0.25D;
        crazy += griniscule;
        double c = actor.posX + (Math.sin(crazy) * 8F);
        double d = actor.posZ + (Math.cos(crazy) * 8F);
        int x = MathHelper.floor_double(c);
        int y = MathHelper.floor_double(actor.boundingBox.minY + (rand.nextFloat() * (u - actor.boundingBox.minY)));
        int z = MathHelper.floor_double(d);

        for (int q = 0; q < MAX_PATHING_TRIES; q++) {
            int i = x + rand.nextInt(5) - rand.nextInt(5);
            int j = y + rand.nextInt(5) - rand.nextInt(5);
            int k = z + rand.nextInt(5) - rand.nextInt(5);

            if (j > 4 && j < worldObj.getHeight() - 1 && isAirySpace(i, j, k) && !isAirySpace(i, j - 1, k)) {
                PathEntity dogs = worldObj.getEntityPathToXYZ(actor, i, j, k, DEF_PATH_RANGE, false, false, true, true);

                if (dogs != null) {
                    return dogs;
                }
            }
        }

        return (PathEntity)null;
    }
    
	private void handleAnger() {
		entityFear = null;
		
		// Lose interest in an entity that is far away or out of sight over time.
		if( entityToAttack != null ) {
			final float enemy_dist = getDistanceToEntity(entityToAttack);
			
			if( enemy_dist >= DEF_PURSUE_RANGE || (rand.nextBoolean() && !canEntityBeSeen(entityToAttack)) ) {
				++loseInterest;
				
				if( loseInterest >= (tamed() ? DEF_AGGRO_TIMER : DEF_AGGRO_TIMER * 3) ) {
					setTarget(null);
					loseInterest = 0;
				}
			} else {
				loseInterest = 0;
			}
			
			// Guards can fight for a queen - will make her run away instead
            if (guard() && getFaction() > 0 && ruler != null && ruler instanceof EntityFairy) {
                EntityFairy fairy = (EntityFairy)ruler;

                if (fairy.entityToAttack != null){
                    float queen_dist = getDistanceToEntity(fairy);

                    if (queen_dist < DEF_DEFEND_RANGE && enemy_dist < DEF_DEFEND_RANGE && canEntityBeSeen(fairy)) {
                        this.setTarget(fairy.entityToAttack);
                        fairy.setTarget(null);
                        fairy.cryTime = 100;
                        fairy.entityFear = entityToAttack;
                    }
                }
            }
		}
	}
	private void handleFear() {
		if( entityFear != null ) {
			if( entityFear.isDead) {
				// Don't fear the dead.
				entityFear = null;
			} else if ( !hasPath() && canEntityBeSeen(entityFear) && cower) {
	            float dist = getDistanceToEntity(entityFear);
	
	            // Run from entityFear if you can see it and it is close.
	            if( dist < DEF_FEAR_RANGE ) {
	                PathEntity doug = roam(entityFear, this, PATH_AWAY);
	
	                if (doug != null) {
	                    setPathToEntity(doug);
	                    cryTime += 120;
	                }
	            }
			}
        }
	}
	private void handleRuler() {
		// TODO Auto-generated method stub
		
	}
	// This handles actions concerning teammates and entities atacking their ruler.
	private void handleSocial() {
		// TODO Auto-generated method stub
		
	}
	// This handles actions of the medics.
	private void handleHealing() {
		// TODO Auto-generated method stub
		
	}
	// A handler specifically for the rogue class.
	private void handleRogue() {
		// TODO Auto-generated method stub
		
	}
	
	public int postedCount;
	// The AI method which handles post-related activities.
	private void handlePosted(boolean b) {
		// TODO Auto-generated method stub
		
	}
    
	// ---------- flag 1 -----------	

	protected boolean getFairyFlag(int i) {
    	return (dataWatcher.getWatchableObjectByte(B_FLAGS) & (1 << i)) != 0;
    }
    protected void setFairyFlag(int i, boolean flag) {
    	byte byte0 = dataWatcher.getWatchableObjectByte(B_FLAGS);
    	if( flag ) {
    		byte0 |= 1 << i;
    	} else {
    		byte0 &= ~(1 << i);
    	}
		dataWatcher.updateObject(B_FLAGS2, Byte.valueOf(byte0));
    }
    
    public static final int FLAG_ARM_SWING	= 0;
    public static final int FLAG_FLY_MODE	= 1;
    public static final int FLAG_CAN_FLAP	= 2;
    public static final int FLAG_TAMED		= 3;
    public static final int FLAG_ANGRY		= 4;
    public static final int FLAG_CRYING		= 5;
    public static final int FLAG_LIFTOFF	= 6;
    public static final int FLAG_HEARTS		= 7;
    
    public boolean getArmSwing() {
        return getFairyFlag(FLAG_ARM_SWING);
    }
    protected void armSwing(boolean flag) {
        setFairyFlag(FLAG_ARM_SWING, flag);
        setTempItem(0);
    }

    public boolean flymode() {
        return getFairyFlag(FLAG_FLY_MODE);
    }
    protected void setFlymode(boolean flag) {
        setFairyFlag(FLAG_FLY_MODE, flag);
    }

    public boolean canFlap() {
        return getFairyFlag(FLAG_CAN_FLAP);
    }
    protected void setCanFlap(boolean flag) {
        setFairyFlag(FLAG_CAN_FLAP, flag);
    }

    public boolean tamed() {
        return getFairyFlag(FLAG_TAMED);
    }
    protected void setTamed(boolean flag) {
        setFairyFlag(FLAG_TAMED, flag);
    }

    public boolean angry() {
        return getFairyFlag(FLAG_ANGRY);
    }
    protected void setAngry(boolean flag) {
        setFairyFlag(FLAG_ANGRY, flag);
    }

    public boolean crying() {
        return getFairyFlag(FLAG_CRYING);
    }
    protected void setCrying(boolean flag) {
        setFairyFlag(FLAG_CRYING, flag);
    }

    public boolean liftOff() {
        return getFairyFlag(FLAG_LIFTOFF);
    }
    protected void setLiftOff(boolean flag) {
        setFairyFlag(FLAG_LIFTOFF, flag);
    }

    public boolean hearts() {
        return getFairyFlag(FLAG_HEARTS);
    }
    protected void setHearts(boolean flag) {
        setFairyFlag(FLAG_HEARTS, flag);
    }
    
	public static final int MAX_SKIN	= 3;
	public static final int MAX_JOB		= 3;
	public static final int MAX_FACTION	= 15;
	public static final int MAX_NAMEIDX = 15;
	
	protected int getSkin() {
		return dataWatcher.getWatchableObjectByte(B_FLAGS) & 0x03;
	}
	protected void setSkin(int skin) {
		if( skin < 0 ) {
			skin = 0;
		} else if ( skin > MAX_SKIN ) {
			skin = MAX_SKIN;
		}
		
		byte byte0 = dataWatcher.getWatchableObjectByte(B_FLAGS);
		byte0 = (byte)(byte0 & 0xfc);
		byte0 |= (byte)skin << 2;
		
		dataWatcher.updateObject(B_FLAGS, Byte.valueOf(byte0));
	}
	
	protected int getJob() {
		return (dataWatcher.getWatchableObjectByte(B_FLAGS) >> 2) & 0x03;
	}
    protected void setJob(int job) {
		if( job < 0 ) {
			job = 0;
		} else if ( job > MAX_JOB ) {
			job = MAX_JOB;
		}
        
        byte byte0 = dataWatcher.getWatchableObjectByte(B_FLAGS);
        byte0 = (byte)(byte0 & 0xf3);
        byte0 |= (byte)job << 2;
        
        dataWatcher.updateObject(B_FLAGS, Byte.valueOf(byte0));
    }
    
	protected static final int NJOB_NORMAL	= 0;
	protected static final int NJOB_GUARD	= 1;
	protected static final int NJOB_SCOUT	= 2;
	protected static final int NJOB_MEDIC	= 3;
	protected static final int SJOB_QUEEN	= 0;
	protected static final int SJOB_ROGUE	= 1;
    
    public boolean normal() {
    	return getJob() == NJOB_NORMAL && !specialJob();
    }
    public boolean guard() {
    	return getJob() == NJOB_GUARD && !specialJob();
    }
    public boolean scout() {
    	return getJob() == NJOB_SCOUT && !specialJob();
    }
    public boolean medic() {
    	return getJob() == NJOB_MEDIC && !specialJob();
    }
    
    public boolean queen() {
    	return getJob() == SJOB_QUEEN && specialJob();
    }
    public boolean rogue() {
    	return getJob() == SJOB_ROGUE && specialJob();
    }
    
    protected int getFaction() {
    	return (dataWatcher.getWatchableObjectByte(B_FLAGS) >> 4) & 0x0f;
    }
    protected void setFaction(int faction) {
		if( faction < 0 ) {
			faction = 0;
		} else if ( faction > MAX_FACTION ) {
			faction = MAX_FACTION;
		}
        
        byte byte0 = dataWatcher.getWatchableObjectByte(B_FLAGS);
        byte0 = (byte)(byte0 & 0x0f);
        byte0 |= (byte)faction << 4;
        
        dataWatcher.updateObject(B_FLAGS, Byte.valueOf(byte0));
    }
    
    // ---------- name ----------
    
    protected void setFairyName(int prefix, int suffix) {
        if( prefix < 0 ) {
            prefix = 0;
        } else if( prefix > 15 ) {
            prefix = 15;
        }

        if( suffix < 0 ) {
            suffix = 0;
        } else if( suffix > 15 ) {
            suffix = 15;
        }

        byte byte0 = (byte)(((byte)prefix & 0x0f) | (((byte)suffix & 0x0f) << 4));
        dataWatcher.updateObject(B_NAME_ORIG, Byte.valueOf(byte0));
    }

    public int getNamePrefix() {
        return (byte)dataWatcher.getWatchableObjectByte(B_NAME_ORIG) & 0x0f;
    }
    public int getNameSuffix() {
        return (byte)(dataWatcher.getWatchableObjectByte(B_NAME_ORIG) >> 4) & 0x0f;
    }
    
    public String getActualName(int prefix, int suffix) {
    	final String custom = "";//getCustomName();
    	if( custom != null && !custom.isEmpty() )
    		return custom;
    	
    	if( prefix < 0 || prefix > MAX_NAMEIDX || suffix < 0 || suffix > MAX_NAMEIDX ) {
    		return "Error-name";
    	} else {
    		return name_prefixes[prefix] + "-" + name_suffixes[suffix];
    	}
    }
    
    public String getQueenName(int prefix, int suffix, int faction) {
    	if( faction < 0 || faction > MAX_FACTION )
    		return "Queen Error-faction";

    	return faction_colors[faction] + "Queen " + getActualName(prefix, suffix);
    }

    public String getFactionName(int faction) {
    	if( faction < 0 || faction > MAX_FACTION )
    		return "Error-faction";

    	return faction_colors[faction] + faction_names[faction];
    }
    
    public String getDisplayName() {
    	if( getFaction() != 0 ) {
    		if( queen() ) {
    			return getQueenName(getNamePrefix(), getNameSuffix(), getFaction());
    		} else {
    			return getFactionName(getFaction());
    		}
    	} else if( tamed() ) {
    		String woosh = getActualName(getNamePrefix(), getNameSuffix());

            if( queen() ) {
                woosh = "Queen " + woosh;
            }

            /**
             * TODO: Escape out to proxy for this.
             * 
            if (ModLoader.getMinecraftInstance().thePlayer.username.equals(rulerName()))
            {
                woosh = (posted() ? "�a" : "�c") + "@�f" + woosh + (posted() ? "�a" : "�c") + "@";
            }
            */

            return woosh;
    	} else {
    		return null;
    	}
    }
    
    // ---------- flag 2 ----------
    
    protected static final int FLAG2_CAN_HEAL		= 0;
    protected static final int FLAG2_RARE_POTION	= 1;
    protected static final int FLAG2_SPECIAL_JOB	= 2;
    protected static final int FLAG2_NAME_ENABLED	= 3;
    protected static final int FLAG2_CLIMBING		= 4;
    protected static final int FLAG2_POSTED			= 5;
    protected static final int FLAG2_WITHERED		= 6;
    protected static final int FLAG3_HAIR_TYPE		= 7;
    
    protected boolean getFairyFlagTwo(int i) {
    	return (dataWatcher.getWatchableObjectByte(B_FLAGS2) & (1 << i)) != 0;
    }
    protected void setFairyFlagTwo(int i, boolean flag) {
    	byte byte0 = dataWatcher.getWatchableObjectByte(B_FLAGS2);
    	if( flag ) {
    		byte0 |= 1 << i;
    	} else {
    		byte0 &= ~(1 << i);
    	}
		dataWatcher.updateObject(B_FLAGS2, Byte.valueOf(byte0));
    }
    
    public boolean canHeal() {
    	return getFairyFlagTwo(FLAG2_CAN_HEAL);
    }
    public void setCanHeal(boolean flag) {
    	setFairyFlagTwo(FLAG2_CAN_HEAL, flag);
    }
    
    public boolean rarePotion() {
    	return getFairyFlagTwo(FLAG2_RARE_POTION);
    }
    public void setRarePotion(boolean flag) {
    	setFairyFlagTwo(FLAG2_RARE_POTION, flag);
    }
    
    public boolean specialJob() {
    	return getFairyFlagTwo(FLAG2_SPECIAL_JOB);
    }
    public void setSpecialJob(boolean flag) {
    	setFairyFlagTwo(FLAG2_SPECIAL_JOB, flag);
    }
    
    public boolean nameEnabled() {
    	return getFairyFlagTwo(FLAG2_NAME_ENABLED);
    }
    public void setNameEnabled(boolean flag) {
    	setFairyFlagTwo(FLAG2_NAME_ENABLED, flag);
    }
    
    public boolean climbing() {
    	return getFairyFlagTwo(FLAG2_CLIMBING);
    }
    public void setClimbing(boolean flag) {
    	setFairyFlagTwo(FLAG2_CLIMBING, flag);
    }
    
    public boolean posted() {
    	return getFairyFlagTwo(FLAG2_POSTED);
    }
    public void setPosted(boolean flag) {
    	setFairyFlagTwo(FLAG2_POSTED, flag);
    }
    
    public boolean withered() {
    	return getFairyFlagTwo(FLAG2_WITHERED);
    }
    public void setWithered(boolean flag) {
    	setFairyFlagTwo(FLAG2_WITHERED, flag);
    }
    
    public boolean hairType() {
    	return getFairyFlagTwo(FLAG3_HAIR_TYPE);
    }
    public void setHairType(boolean flag) {
    	setFairyFlagTwo(FLAG3_HAIR_TYPE, flag);
    }
    
    // ----------
    
    // Custom name of the fairy, enabled by paper.
    public String getCustomName() {
        return dataWatcher.getWatchableObjectString(S_NAME_REAL);
    }
    public void setCustomName(String s) {
        dataWatcher.updateObject(S_NAME_REAL, s);
    }

    // A temporary item shown while arm is swinging, related to jobs.
    public int getTempItem() {
        return dataWatcher.getWatchableObjectInt(I_TOOL);
    }
    public void setTempItem(int i) {
        dataWatcher.updateObject(I_TOOL, i);
    }
    
    // ----------

	// no baby fairies for now
	@Override
	public EntityAgeable createChild(EntityAgeable parent) {
		return null;
	}

    private static final String name_prefixes[] = {
        "Silly",
        "Fire",
        "Twinkle",
        "Bouncy",
        "Speedy",
        "Wiggle",
        "Fluffy",
        "Cloudy",
        "Floppy",
        "Ginger",
        "Sugar",
        "Winky",
        "Giggle",
        "Cutie",
        "Sunny",
        "Honey"
    };

    private static final String name_suffixes[] = {
        "puff",
        "poof",
        "butt",
        "munch",
        "star",
        "bird",
        "wing",
        "shine",
        "snap",
        "kins",
        "bee",
        "chime",
        "button",
        "bun",
        "heart",
        "boo"
    };

    private static final String faction_colors[] = {
        "�0",
        "�1",
        "�2",
        "�3",
        "�4",
        "�5",
        "�6",
        "�7",
        "�8",
        "�9",
        "�a",
        "�b",
        "�c",
        "�d",
        "�e",
        "�f"
    };
        
    private static final String faction_names[] = {
        "no queen",
        "<Aviary Army>",
        "<Bantam Brawlers>",
        "<Charging Cherubs>",
        "<Dainty Demons>",
        "<Enigmatic Escorts>",
        "<Floating Fury>",
        "<Graceful Gliders>",
        "<Hardy Handmaids>",
        "<Iron Imps>",
        "<Opulent Order>",
        "<Kute Killers>",
        "<Lethal Ladies>",
        "<Maiden Militia>",
        "<Nimble Nymphs>",
        "<Petite Pugilists>"
    };

    // ---------- stubs ----------
    
    private void processSwinging() {
		// TODO Auto-generated method stub
		
	}

	private String rulerName() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean checkGroundBelow() {
		// TODO Auto-generated method stub
		return false;
	}

	private void showHeartsOrSmokeFX(boolean tamed) {
		// TODO Auto-generated method stub
		
	}

	private void setSitting(boolean b) {
		// TODO Auto-generated method stub
		
	}

	private boolean isSitting() {
		// TODO Auto-generated method stub
		return false;
	}

	private void setFairyClimbing(boolean b) {
		// TODO Auto-generated method stub
		
	}

	private void updateWithering() {
		// TODO Auto-generated method stub
		
	}

	private boolean isAirySpace(int a, int b, int c) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkFlyBlocked() {
		// TODO Auto-generated method stub
		return false;
	}

}

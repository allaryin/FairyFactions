package fairies.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fairies.FairyFactions;
import fairies.Version;
import fairies.ai.FairyJob;
import fairies.world.FairyGroupGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

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
		
	private boolean cower;
	public boolean didHearts;
	public boolean didSwing;
	private boolean wasFishing;
	private int snowballin;
	
	private int flyTime;
	private int healTime;
	private int cryTime;
	private int loseInterest;
	private int loseTeam;
	
	private int postX, postY, postZ;	// where is our sign?

	private EntityLivingBase ruler;
	private EntityLivingBase entityHeal;
	private Entity entityFear;
	public FairyEntityFishHook fishEntity;

	// non-persistent fields
	public float sinage;				// what does this mean?
	private boolean flag;				// flagged for what, precisely?
	private boolean createGroup;
	private int listActions;
	public int postedCount;
	public int witherTime;
	private ItemStack tempItem;
	private boolean isSwinging;
	private int particleCount;
	private boolean flyBlocked;

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
		this.setFlyTime( 400 + rand.nextInt(200) );
		this.setCower(rand.nextBoolean());
		this.postX = this.postY = this.postZ = -1;
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
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("flags",	dataWatcher.getWatchableObjectByte(B_FLAGS));
		nbt.setByte("flags2",	dataWatcher.getWatchableObjectByte(B_FLAGS2));
		nbt.setByte("type",		dataWatcher.getWatchableObjectByte(B_TYPE));
		nbt.setByte("nameOrig",	dataWatcher.getWatchableObjectByte(B_NAME_ORIG));

		nbt.setString("rulerName",	rulerName());
		nbt.setString("customName",	getCustomName());
		nbt.setIntArray("post", new int[] { postX, postY, postZ } );
		
		nbt.setShort("flyTime",		(short) flyTime);
		nbt.setShort("healTime",	(short) healTime);
		nbt.setShort("cryTime",		(short) cryTime);
		nbt.setShort("loseInterest", (short) loseInterest);
		nbt.setShort("loseTeam",	(short) loseTeam);

		nbt.setBoolean("cower",		this.cower);
		nbt.setBoolean("didHearts",	this.didHearts);
		nbt.setBoolean("didSwing",	this.didSwing);		
		if (this.fishEntity != null) {
			this.wasFishing = true;
			nbt.setBoolean("wasFishing", this.wasFishing);
		}
		nbt.setShort("snowballin",	(short) snowballin);

		nbt.setBoolean("isSitting", isSitting());
	}
	  
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		dataWatcher.updateObject(B_FLAGS,		nbt.getByte("flags"));
		dataWatcher.updateObject(B_FLAGS2,		nbt.getByte("flags2"));
		dataWatcher.updateObject(B_TYPE,		nbt.getByte("type"));
		dataWatcher.updateObject(B_NAME_ORIG,	nbt.getByte("nameOrig"));

		setRulerName(nbt.getString("rulerName"));
		setCustomName(nbt.getString("customName"));
		final int[] post = nbt.getIntArray("post");
		if( post.length > 0 ) {
			postX = post[0]; postY = post[1]; postZ = post[2];
		}

		flyTime = nbt.getShort("flyTime");
		healTime = nbt.getShort("healTime");
		cryTime = nbt.getShort("cryTime");
		loseInterest = nbt.getShort("loseInterest");
		loseTeam = nbt.getShort("loseTeam");
		
		cower = nbt.getBoolean("cower");
		didHearts = nbt.getBoolean("didHearts");
		didSwing = nbt.getBoolean("didSwing");
		wasFishing = nbt.getBoolean("wasFishing");
		snowballin = nbt.getShort("snowballin");

		setSitting(nbt.getBoolean("isSitting"));

		if (!this.worldObj.isRemote) {
			setCanHeal(healTime <= 0);
			setPosted(postY > -1);
		}
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

			// TODO: move group sizes into config
			final FairyGroupGenerator group = new FairyGroupGenerator(8, 10, getFaction());
			if( group.generate( worldObj, rand, i, j, k ) ) {
				// This is good.
			} else {
				// issue a kill
				if( !worldObj.isRemote ) {
					FairyFactions.proxy.sendFairyDespawn(this);
				}
			}			
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
				
				if( nameEnabled() && tamed() && hasRuler() ) {
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

            if (worldObj.getBlock(j, k, l).isBlockNormalCube()) {
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
    
    @Override
    public boolean canDespawn() {
        return ruler == null && !tamed();
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

            if (isSitting() && getFishEntity() == null) {
                setSitting(false);
            }
        }

        if (isSitting()) {
            handlePosted(false);
            return;
        }

        flyBlocked = checkFlyBlocked();

        if (getFlyTime() > 0) {
            setFlyTime( getFlyTime() - 1 );
        }

        boolean liftFlag = false;

        if (flymode()) {
            fallDistance = 0F;

            if (ridingEntity != null) {
                if (entityToAttack != null && ridingEntity == entityToAttack) {
                    setFlyTime( 200 );
                    liftFlag = true;

                    if ((attackTime <= 0) || flyBlocked) {
                        attackTime = 0;
                        attackEntity(ridingEntity, 0F);
                        liftFlag = false;
                    }
                } else if (tamed()) {
                    if (ridingEntity.onGround || ridingEntity.isInWater()) {
                        setFlyTime( (queen() || scout() ? 60 : 40) );

                        if (withered()) {
                            setFlyTime( getFlyTime() - 10 );
                        }
                    }
                }
            }

            if (getFlyTime() <= 0 || (flyBlocked && (ridingEntity == null || (entityToAttack != null && ridingEntity == entityToAttack)))) {
                setCanFlap(false);
            } else {
                setCanFlap(true);
            }

            if (ridingEntity == null && (onGround || inWater)) {
                setFlymode(false);
                setFlyTime( 400 + rand.nextInt(200) );

                // Scouts are less likely to want to walk.
                if (scout()) {
                    setFlyTime( getFlyTime() / 3 );
                }
            }
        } else {
            if (getFlyTime() <= 0 && !flyBlocked) {
                jump();
                setFlymode(true);
                setCanFlap(true);
                setFlyTime( 400 + rand.nextInt(200) );

                //Scouts are more likely to want to fly.
                if (scout())  {
                    setFlyTime( getFlyTime() * 3 );
                }
            }

            if (ridingEntity != null && !flymode()) {
                setFlymode(true);
                setCanFlap(true);
            }

            if (!flymode() && !onGround && fallDistance >= 0.5F && ridingEntity == null) {
                setFlymode(true);
                setCanFlap(true);
                setFlyTime( 400 + rand.nextInt(200) );
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

            if (getCryTime() > 600) {
                setCryTime( 600 );
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
            setEntityFear(entityToAttack);
            setCryTime( Math.max(getCryTime(), 100) );
            setTarget((Entity)null);
        }

        setCrying(getCryTime() > 0);
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

	private boolean canTeleportToRuler( EntityPlayer player ) {
		return player.inventory != null
				&& (player.inventory.hasItem( Items.ender_pearl ) || player.inventory.hasItem( Items.ender_eye ));
	}

	// Can teleport to the ruler if he has an enderman drop in his inventory.
	private void teleportToRuler( EntityLivingBase entity ) {
		int i = MathHelper.floor_double( entity.posX ) - 2;
		int j = MathHelper.floor_double( entity.posZ ) - 2;
		int k = MathHelper.floor_double( entity.boundingBox.minY );

		for ( int l = 0; l <= 4; l++ ) {
			for ( int i1 = 0; i1 <= 4; i1++ ) {
				if ( (l < 1 || i1 < 1 || l > 3 || i1 > 3) && worldObj.getBlock( i + l, k - 1, j + i1 ).isNormalCube()
						&& !worldObj.getBlock( i + l, k, j + i1 ).isNormalCube()
						&& !worldObj.getBlock( i + l, k + 1, j + i1 ).isNormalCube()
						&& isAirySpace( i + l, k, j + i1 ) ) {
					setLocationAndAngles( (float) (i + l) + 0.5F, k, (float) (j + i1) + 0.5F, rotationYaw,
							rotationPitch );
					return;
				}
			}
		}
	}
    
	private void handleAnger() {
		setEntityFear(null);
		
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
                        fairy.setCryTime( 100 );
                        fairy.setEntityFear(entityToAttack);
                    }
                }
            }
		}
	}
	private void handleFear() {
		if( getEntityFear() != null ) {
			if( getEntityFear().isDead) {
				// Don't fear the dead.
				setEntityFear(null);
			} else if ( !hasPath() && canEntityBeSeen(getEntityFear()) && willCower()) {
	            float dist = getDistanceToEntity(getEntityFear());
	
	            // Run from entityFear if you can see it and it is close.
	            if( dist < DEF_FEAR_RANGE ) {
	                PathEntity doug = roam(getEntityFear(), this, PATH_AWAY);
	
	                if (doug != null) {
	                    setPathToEntity(doug);
	                    setCryTime( getCryTime() + 120 );
	                }
	            }
			}
        }
	}
	private void handleRuler() {
		// TODO: create constants for all of these ranges and time limits
		
        if (ruler != null)   {
            if (ruler.getHealth() <= 0 || ruler.isDead) {
            	// get rid of that ruler.
                ruler = null;
            }
        }

        if (ruler == null) {
        	// Looking for a queen to follow.
            if (!tamed() && !queen())   {
                double d = 40D;

                if (getFaction() == 0) {
                    d = 16D;
                }

                List list = worldObj.getEntitiesWithinAABB(EntityFairy.class, boundingBox.expand(d, d, d));

                for (int j = 0; j < list.size(); j++) {
                	EntityFairy fairy = (EntityFairy)list.get(j);

                    if (fairy != this && fairy.getHealth() > 0 && fairy.queen())   {
                        if (getFaction() > 0 && fairy.getFaction() == this.getFaction()) {
                        	// Fairy finds the queen of its faction, fairly standard.
                            ruler = fairy;
                            break;
                        } else if (getFaction() == 0 && fairy.getFaction() > 0 && canEntityBeSeen(fairy)) {
                        	// A factionless fairy may find a new ruler on its own.
                            ruler = fairy;
                            setFaction(fairy.getFaction());
                            break;
                        }
                    }
                }
            } else if (getFaction() == 0 && tamed()) {
            	// Looking for a player to follow.
                List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(16D, 16D, 16D));

                for (int j = 0; j < list.size(); j++) {
                    EntityPlayer player = (EntityPlayer)list.get(j);
                    
                    if (player.getHealth() > 0 && isRuler(player) && canEntityBeSeen(player)) {
                        ruler = player;
                        break;
                    }
                }
            }
        }

        // This makes fairies walk towards their ruler.
        if (ruler != null && !hasPath() && !posted()) {
            float dist = getDistanceToEntity(ruler);

            // Guards and Queens walk closer to the player (Medic healing?)
            if ((guard() || queen()) && canEntityBeSeen(ruler) && dist > 5F && dist < 16F) {
                PathEntity path = worldObj.getPathEntityToEntity(this, ruler, 16F, false, false, true, true);

                if (path != null) {
                    setPathToEntity(path);
                }
            } else {
                if (scout() && ruler instanceof EntityFairy)   {
                	// Scouts stay way out there on the perimeter.
                    if (dist < 12F) {
                        PathEntity doug = roam(ruler, this, (float)Math.PI);

                        if (doug != null) {
                            setPathToEntity(doug);
                        }
                    } else if (dist > 24F) {
                        PathEntity doug = roam(ruler, this, 0F);

                        if (doug != null) {
                            setPathToEntity(doug);
                        }
                    }
                } else {
                	// Regular fairies stay moderately close.
                    if (dist > 16F && ruler instanceof EntityPlayer && canTeleportToRuler((EntityPlayer)ruler)) {
                    	// Can teleport to the owning player if he has an ender eye or an ender pearl.
                        teleportToRuler(ruler); 
                    } else if (dist > (ruler instanceof EntityFairy ? 12F : 6F)) {
                        PathEntity doug = roam(ruler, this, 0F);

                        if (doug != null) {
                            setPathToEntity(doug);
                        }
                    }
                }
            }
        }

        if (snowballin > 0 && attackTime <= 0 && ruler != null && entityToAttack == null && entityFear == null && cryTime == 0) {
            float dist = getDistanceToEntity(ruler);

            if (dist < 10F && canEntityBeSeen(ruler)) {
                tossSnowball(ruler);
            } else if (!hasPath() && dist < 16F) {
                PathEntity doug = roam(ruler, this, 0F);

                if (doug != null) {
                    setPathToEntity(doug);
                }
            }
        }

        if (getFaction() > 0) {
        	// This is a method for making sure that fairies eventually realize they're alone
            boolean flag = false;

            if (!queen() && (ruler == null || getDistanceToEntity(ruler) > 40F)) {
            	// If a follower has lost its leader
                flag = true;
            } else if (queen()) {
            	// If a leader has lost her followers
                flag = true;
                List list = worldObj.getEntitiesWithinAABB(EntityFairy.class, boundingBox.expand(40D, 40D, 40D));

                for (int j = 0; j < list.size(); j++) {
                	EntityFairy fairy = (EntityFairy)list.get(j);

                    if (fairy != this && fairy.sameTeam(this) && fairy.getHealth() > 0) {
                        flag = false;
                        break;
                    }
                }
            } else if (ruler != null && ruler instanceof EntityFairy) {
            	//If a fairy queen was tamed in peaceful mode
            	EntityFairy fairy = (EntityFairy)ruler;

                if (!sameTeam(fairy)) {
                    flag = true;

                    if (loseTeam < 65) {
                        loseTeam = 65 + rand.nextInt(8);
                    }
                }
            }

            if (flag) {
            	// Takes a while for it to take effect.
                ++loseTeam; 

                if (loseTeam >= 75) {
                    ruler = null;
                    disband();
                    loseTeam = 0;
                    setCryTime( 0 );
                    setPathToEntity((PathEntity)null);
                }
            } else {
                loseTeam = 0;
            }
        }
	}

	private void tossSnowball(EntityLivingBase attackTarget) {
        EntitySnowball entitysnowball = new EntitySnowball(worldObj, this);
        double d = attackTarget.posX - this.posX;
        double d1 = (attackTarget.posY + (double)attackTarget.getEyeHeight()) - 1.1000000238418579D - entitysnowball.posY;
        double d2 = attackTarget.posZ - this.posZ;
        float f = MathHelper.sqrt_double(d * d + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d, d1 + (double)f, d2, 1.6F, 12F);
        worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        worldObj.spawnEntityInWorld(entitysnowball);
        attackTime = 30;
        armSwing(!didSwing);
        faceEntity(attackTarget, 180F, 180F);
        snowballin -= 1;

        if (snowballin < 0) {
            snowballin = 0;
        }		
	}

	// This handles actions concerning teammates and entities atacking their ruler.
	private void handleSocial() {
		if ( rand.nextBoolean()) {
			return;
		}

		List list = worldObj.getEntitiesWithinAABBExcludingEntity( this, boundingBox.expand( 16D, 16D, 16D ) );
		Collections.shuffle( list, rand );

		for ( int j = 0; j < list.size(); j++ ) {
			Entity entity = (Entity) list.get( j );

			if ( canEntityBeSeen( entity ) && !entity.isDead ) {
				if ( (ruler != null || queen()) && entity instanceof EntityFairy && sameTeam( (EntityFairy) entity ) ) {
					EntityFairy fairy = (EntityFairy) list.get( j );

					if ( fairy.getHealth() > 0 ) {
						Entity scary = (Entity) null;

						if ( fairy.getEntityFear() != null ) {
							scary = fairy.getEntityFear();
						} else if ( fairy.entityToAttack != null ) {
							scary = fairy.entityToAttack;
						}

						if ( scary != null ) {
							float dist = getDistanceToEntity( scary );

							if ( dist > 16F || !canEntityBeSeen( scary ) ) {
								scary = null;
							}
						}

						if ( scary != null ) {
							if ( willCower() ) {
								if ( fairy.entityToAttack == scary && canEntityBeSeen( scary ) ) {
									setCryTime( 120 );
									this.setEntityFear( scary );
									PathEntity doug = roam( entity, this, (float) Math.PI );

									if ( doug != null ) {
										setPathToEntity( doug );
									}

									break;
								} else if ( fairy.getCryTime() > 60 ) {
									setCryTime( Math.max( fairy.getCryTime() - 60, 0 ) );
									this.setEntityFear( scary );
									PathEntity doug = roam( entity, this, (float) Math.PI );

									if ( doug != null ) {
										setPathToEntity( doug );
									}

									break;
								}
							} else {
								this.setTarget( (Entity) scary );
								break;
							}
						}
					}
				} else if ( ruler != null && (guard() || queen()) && entity instanceof EntityCreature
						&& !(entity instanceof EntityCreeper)
						&& (!(entity instanceof EntityAnimal) || (!peacefulAnimal( (EntityAnimal) entity ))) ) {
					// Guards proactivley seeking holstile enemies. Will add
					// slimes? Maybe dunno.
					EntityCreature creature = (EntityCreature) entity;

					if ( creature.getHealth() > 0 && creature.getEntityToAttack() != null && creature.getEntityToAttack() == ruler ) {
						this.setTarget( (Entity) creature );
						break;
					}
				} else if ( entity instanceof EntityTNTPrimed && !hasPath() ) {
					// Running away from lit TNT.
					float dist = getDistanceToEntity( entity );

					if ( dist < 8F ) {
						PathEntity doug = roam( entity, this, (float) Math.PI );

						if ( doug != null ) {
							setPathToEntity( doug );

							if ( !flymode() ) {
								setFlymode( true );
								jump();
								setFlyTime( 100 );
							}

							break;
						}
					}
				}
			}
		}
	}

	public boolean peacefulAnimal( EntityAnimal animal ) {
        Class thing = animal.getClass();
        return thing == EntityPig.class ||
                thing == EntityCow.class ||
                thing == EntityChicken.class ||
                thing == EntitySheep.class ||
                thing == EntityMooshroom.class;
	}

	// This handles actions of the medics.
	private void handleHealing() {
		if ( healTime > 0 ) {
			return;
		}

		if ( entityHeal != null ) {
			if ( entityHeal.getHealth() <= 0 || entityHeal.isDead ) {
				entityHeal = null;
			} else if ( !hasPath() ) {
				PathEntity doug = worldObj.getPathEntityToEntity( this, entityHeal, 16F, false, false, true, true );

				if ( doug != null ) {
					setPathToEntity( doug );
				} else {
					entityHeal = null;
				}
			} else {
				float g = getDistanceToEntity( entityHeal );

				if ( g < 2.5F && canEntityBeSeen( entityHeal ) ) {
					healThatAss( entityHeal );
					entityHeal = null;
				}
			}
		}

		if ( entityHeal == null && healTime <= 0 ) {
			List list = worldObj.getEntitiesWithinAABBExcludingEntity( this, boundingBox.expand( 16D, 16D, 16D ) );

			for ( int j = 0; j < list.size(); j++ ) {
				Entity entity = (Entity) list.get( j );

				if ( canEntityBeSeen( entity ) && !entity.isDead ) {
					if ( entity instanceof EntityFairy ) {
						EntityFairy fairy = (EntityFairy) list.get( j );

						if ( fairy.getHealth() > 0 && sameTeam( fairy ) && fairy.getHealth() < fairy.getMaxHealth() ) {
							this.entityHeal = fairy;
							PathEntity doug = worldObj.getPathEntityToEntity( this, entityHeal, 16F, false, false, true,
									true );

							if ( doug != null ) {
								setPathToEntity( doug );
							}

							break;
						}
					} else if ( entity instanceof EntityLiving && ruler != null && ((EntityLiving) entity) == ruler ) {
						if ( ruler.getHealth() > 0 && ruler.getHealth() < ruler.getMaxHealth() ) {
							this.entityHeal = ruler;
							PathEntity doug = worldObj.getPathEntityToEntity( this, entityHeal, 16F, false, false, true,
									true );

							if ( doug != null ) {
								setPathToEntity( doug );
							}

							break;
						}
					}
				}
			}

			if ( entityHeal == null && getHealth() < getMaxHealth() ) {
				healThatAss( this );
			}
		}
	}
	
    public static final ItemStack healPotion = new ItemStack(Items.potionitem, 1, 16389);
    public static final ItemStack restPotion = new ItemStack(Items.potionitem, 1, 16385);
    public static final ItemStack fishingStick = new ItemStack(Items.stick, 1);

    public ItemStack handPotion() {
        return (rarePotion() ? restPotion : healPotion);
    }
    // TODO: fix childish method name :P
	private void healThatAss( EntityLivingBase guy ) {
        armSwing(!didSwing); //Swings arm and heals the specified person.
        EntityPotion potion = new EntityPotion(worldObj, this, handPotion().getItemDamage());
        worldObj.spawnEntityInWorld(potion);
        
        // potion.onImpact(new MovingObjectPosition(guy));
        try {
            final Method onImpact = ReflectionHelper.findMethod(EntityPotion.class, potion, new String[]{ "onImpact" }, MovingObjectPosition.class);
			onImpact.invoke(potion, new MovingObjectPosition(guy));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        setPathToEntity((PathEntity)null);
        healTime = 200;
        setRarePotion(rand.nextInt(4) == 0);
	}

	// A handler specifically for the rogue class.
	private void handleRogue() {
		if ( rand.nextBoolean() ) {
			return;
		}

		List list = worldObj.getEntitiesWithinAABBExcludingEntity( this, boundingBox.expand( 16D, 16D, 16D ) );
		Collections.shuffle( list, rand );

		for ( int j = 0; j < list.size(); j++ ) {
			Entity entity = (Entity) list.get( j );

			if ( canEntityBeSeen( entity ) && !entity.isDead ) {
				if ( (ruler != null || queen()) && entity instanceof EntityFairy
						&& sameTeam( (EntityFairy) entity ) ) {
					EntityFairy fairy = (EntityFairy) list.get( j );

					if ( fairy.getHealth() > 0 ) {
						Entity scary = (Entity) null;

						if ( fairy.getEntityFear() != null ) {
							scary = fairy.getEntityFear();
						} else if ( fairy.entityToAttack != null ) {
							scary = fairy.entityToAttack;
						}

						if ( scary != null ) {
							float dist = getDistanceToEntity( scary );

							if ( dist > 16F || !canEntityBeSeen( scary ) ) {
								scary = null;
							}
						}

						if ( scary != null ) {
							if ( canHeal() ) {
								if ( fairy.entityToAttack == scary && canEntityBeSeen( scary ) ) {
									setCryTime( 120 );
									this.setEntityFear( scary );
									PathEntity doug = roam( entity, this, (float) Math.PI );

									if ( doug != null ) {
										setPathToEntity( doug );
									}

									break;
								} else if ( fairy.getCryTime() > 60 ) {
									setCryTime( Math.max( fairy.getCryTime() - 60, 0 ) );
									this.setEntityFear( scary );
									PathEntity doug = roam( entity, this, (float) Math.PI );

									if ( doug != null ) {
										setPathToEntity( doug );
									}

									break;
								}
							} else {
								this.setTarget( (Entity) scary );
								break;
							}
						}
					}
				} else if ( ruler != null && canHeal() && entity instanceof EntityCreature
						&& !(entity instanceof EntityCreeper)
						&& (!(entity instanceof EntityAnimal) || (!peacefulAnimal( (EntityAnimal) entity ))) ) {
					EntityCreature creature = (EntityCreature) entity;

					if ( creature.getHealth() > 0 && creature.getEntityToAttack() != null && creature.getEntityToAttack() == ruler ) {
						this.setTarget( (Entity) creature );
						break;
					}
				} else if ( entity instanceof EntityTNTPrimed && !hasPath() ) {
					// Running away from lit TNT.
					float dist = getDistanceToEntity( entity );

					if ( dist < 8F ) {
						PathEntity doug = roam( entity, this, (float) Math.PI );

						if ( doug != null ) {
							setPathToEntity( doug );

							if ( !flymode() ) {
								setFlymode( true );
								jump();
								setFlyTime( 100 );
							}

							break;
						}
					}
				}
			}
		}
	}
	
	// The AI method which handles post-related activities.
	private void handlePosted(boolean b) {
		if ( !tamed() || getFaction() > 0 || postedCount <= (posted() ? 2 : 5) ) {
			postedCount++;
			return; // Avoid processing too often or when not necessary.
		}

		postedCount = 0;
		boolean farFlag = false;

		if ( postY > -1 ) {
			if ( ridingEntity != null && ruler != null && ridingEntity == ruler ) {
				abandonPost();
				return; // When a the player takes a tamed fairy away, it
						// automatically cancels the post.
			}

			// Check to see if the chunk is loaded.
			Chunk chunk = worldObj.getChunkFromBlockCoords( postX, postZ );

			if ( chunk != null && !(chunk instanceof EmptyChunk) ) {
				Block block = worldObj.getBlock( postX, postY, postZ );

				if ( block == null || !(block instanceof BlockSign) ) {
					// If the saved position is not a sign block.
					abandonPost();
				} else {
					TileEntity tileentity = worldObj.getTileEntity( postX, postY, postZ );
					if ( tileentity == null || !(tileentity instanceof TileEntitySign) ) {
						// Make sure the tile entity is right
						abandonPost();
					} else {
						TileEntitySign sign = (TileEntitySign) tileentity;
						if ( !mySign( sign ) ) {
							// Make sure the name still matches
							abandonPost();
						} else if ( canRoamFar( sign ) ) {
							farFlag = true;
						}
					}
				}
			}
		} else {
			// Try to find a post. The ruler has to be nearby for it to work.
			if ( ruler != null && (ridingEntity == null || ridingEntity != ruler)
					&& getDistanceSqToEntity( ruler ) <= 64F && canEntityBeSeen( ruler ) ) {
				// Gets the fairy's relative position
				int aa = MathHelper.floor_double( posX );
				int bb = MathHelper.floor_double( boundingBox.minY );
				int cc = MathHelper.floor_double( posZ );
				
				for ( int i = 0; i < 245; i++ ) {
					int x = -3 + (i % 7); // Look around randomly.
					int y = -2 + (i / 49);
					int z = -3 + ((i / 7) % 7);

					if ( Math.abs( x ) == 3 && Math.abs( z ) == 3 ) {
						continue;
					}

					x += aa;
					y += bb;
					z += cc;

					if ( y >= 0 && y < worldObj.getHeight() ) {
						final Block block = worldObj.getBlock( x, y, z );

						if ( block == Blocks.standing_sign || block == Blocks.wall_sign ) {
							TileEntity tileentity = worldObj.getTileEntity( x, y, z );

							if ( tileentity != null && tileentity instanceof TileEntitySign ) {
								TileEntitySign sign = (TileEntitySign) tileentity;

								if ( mySign( sign ) ) {
									postX = x;
									postY = y;
									postZ = z;
									setPosted( true );
									break;
								}
							}
						}
					}
				}
			}
		}

		if ( !flag ) // Processes fishing, then returns, if sitting.
		{
			if ( fishEntity != null ) {
				if ( fishEntity.gotBite() ) {
					castRod();
					attackTime = 10;
				} else if ( rand.nextFloat() < 0.1F ) {
					// TODO: handle packet
					/*
					mod_FairyMod.setPrivateValueBoth( EntityLiving.class, this, "currentTarget", "ay", fishEntity );
					*/
					numTicksToChaseTarget = 10 + this.rand.nextInt( 20 );
				}
			} else if ( rand.nextInt( 2 ) == 0 ) {
				new FairyJob( this ).sittingFishing( worldObj );
			}

			return;
		}

		if ( posted() && !hasPath() && !angry() && !crying() ) {
			double aa = (double) postX + 0.5D;
			double bb = (double) postY + 0.5D;
			double cc = (double) postZ + 0.5D;
			double dd = posX - aa;
			double ee = boundingBox.minY - bb;
			double ff = posZ - cc;
			double gg = Math.sqrt( (dd * dd) + (ee * ee) + (ff * ff) );

			if ( gg >= (farFlag ? 12D : 6D) ) {
				PathEntity doug = roamBlocks( aa, bb, cc, this, 0F );

				if ( doug != null ) {
					setPathToEntity( doug );
				}
			}
		}

		if ( posted() ) {
			new FairyJob( this ).discover( worldObj );
		}
	}

	public void castRod() {
		if ( fishEntity != null ) {
			fishEntity.catchFish();
			armSwing( !didSwing );
			setSitting( false );
		} else {
			worldObj.playSoundAtEntity( this, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F) );
			FairyEntityFishHook hook = new FairyEntityFishHook( worldObj, this );
			worldObj.spawnEntityInWorld( hook );
			armSwing( !didSwing );
			setTempItem( Items.stick );
			setSitting( true );
			isJumping = false;
			setPathToEntity( (PathEntity) null );
			setTarget( (Entity) null );
			entityFear = null;
		}
	}

	private boolean signContains( TileEntitySign sign, String str ) {
		// If the sign's text is messed up or something
		if ( sign.signText == null ) {
			return false;
		}

		// makes the subsequence
		final CharSequence mySeq = str.subSequence( 0, str.length() - 1 );

		// loops through for all sign lines
		for ( int i = 0; i < sign.signText.length; i++ ) {
			// name just has to be included in full on one of the lines.
			if ( sign.signText[i].contains( mySeq ) ) {
				return true;
			}
		}

		return false;		
	}
	private boolean canRoamFar( TileEntitySign sign ) {
		return signContains( sign, "~f" );
	}
	private boolean mySign( TileEntitySign sign ) {
		// Converts actual name
		final String actualName = getActualName( getNamePrefix(), getNameSuffix() );
		return signContains( sign, actualName );
	}

	// Leave a post.
	public void abandonPost() {
		postX = postY = postZ = -1;
		setPosted( false );
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
    public void armSwing(boolean flag) {
        setFairyFlag(FLAG_ARM_SWING, flag);
        setTempItem(null);
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
    public void setHearts(boolean flag) {
        setFairyFlag(FLAG_HEARTS, flag);
    }
    
	public static final int MAX_SKIN	= 3;
	public static final int MAX_JOB		= 3;
	public static final int MAX_FACTION	= 15;
	public static final int MAX_NAMEIDX = 15;
	
	public int getSkin() {
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
	
	public int getJob() {
		return (dataWatcher.getWatchableObjectByte(B_FLAGS) >> 2) & 0x03;
	}
    public void setJob(int job) {
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
    public void setFaction(int faction) {
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
    
    public String toString() {
    	return getActualName(getNamePrefix(), getNameSuffix());
    }
    
    // ---------- flag 2 ----------
    
    protected static final int FLAG2_CAN_HEAL		= 0;
    protected static final int FLAG2_RARE_POTION	= 1;
    protected static final int FLAG2_SPECIAL_JOB	= 2;
    protected static final int FLAG2_NAME_ENABLED	= 3;
    protected static final int FLAG2_CLIMBING		= 4;
    protected static final int FLAG2_POSTED			= 5;
    protected static final int FLAG2_WITHERED		= 6;
    protected static final int FLAG2_HAIR_TYPE		= 7;
    
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
    	return getFairyFlagTwo(FLAG2_HAIR_TYPE);
    }
    public void setHairType(boolean flag) {
    	setFairyFlagTwo(FLAG2_HAIR_TYPE, flag);
    }
    
    // ----------
    
    public boolean isRuler( EntityPlayer player ) {
    	return tamed() && rulerName().equals(player.getGameProfile().getName());
    }
    public boolean hasRuler() {
    	return ruler != null && rulerName() != null;
    }
	public String rulerName() {
		final String name = dataWatcher.getWatchableObjectString(S_OWNER);
		return name;
	}
	public void setRulerName(String s) {
		dataWatcher.updateObject( S_OWNER, s);
	}
    
    // Custom name of the fairy, enabled by paper.
    public String getCustomName() {
        return dataWatcher.getWatchableObjectString(S_NAME_REAL);
    }
    public void setCustomName(String s) {
        dataWatcher.updateObject(S_NAME_REAL, s);
    }

    // A temporary item shown while arm is swinging, related to jobs.
    public Item getTempItem() {
        return Item.getItemById( dataWatcher.getWatchableObjectInt(I_TOOL) );
    }
    public void setTempItem(Item item) {
        dataWatcher.updateObject(I_TOOL, Item.getIdFromItem(item));
    }
    
    // ----------

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
		if ( getArmSwing() != didSwing ) {
			didSwing = !didSwing;
			// if(!isSwinging || swingProgressInt >= 3 || swingProgressInt < 0)
			// {
			swingProgressInt = -1;
			isSwinging = true;
			tempItem = null;
			// }
		}

		if ( isSwinging ) {
			swingProgressInt++;

			if ( swingProgressInt >= 6 ) {
				swingProgressInt = 0;
				isSwinging = false;

				if ( tempItem != null && tempItem != fishingStick ) {
					tempItem = null;
				}
			} else if ( tempItem == null && getTempItem() != null ) {
				tempItem = new ItemStack( getTempItem(), 1, 0 );
			}
		}

		swingProgress = (float) swingProgressInt / 6F;

		if ( !isSitting() && tempItem != null && tempItem == fishingStick ) {
			tempItem = null;
		}
	}

	private boolean checkGroundBelow() {
		int a = MathHelper.floor_double( posX );
		int b = MathHelper.floor_double( boundingBox.minY );
		int b1 = MathHelper.floor_double( boundingBox.minY - 0.5D );
		int c = MathHelper.floor_double( posZ );

		if ( !isAirySpace( a, b - 1, c ) || !isAirySpace( a, b1 - 1, c ) ) {
			return true;
		}

		return false;
	}

	private void showHeartsOrSmokeFX( boolean flag ) {
		final String s = (flag ? "heart" : "smoke");

		for ( int i = 0; i < 7; i++ ) {
			double d = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			worldObj.spawnParticle( s, (posX + (double) (rand.nextFloat() * width * 2.0F)) - (double) width,
					posY + 0.5D + (double) (rand.nextFloat() * height),
					(posZ + (double) (rand.nextFloat() * width * 2.0F)) - (double) width, d, d1, d2 );
		}
	}

	public static final int FLAG_SITTING = 1;
	public void setSitting( boolean flag ) {
		setFlag( FLAG_SITTING, flag );
	}
	public boolean isSitting() {
		return getFlag( FLAG_SITTING );
	}

	protected void setFairyClimbing(boolean flag) {
		setClimbing(flag);
	}

	private void updateWithering() {
		if ( rogue() ) {
			return;
		}

		witherTime++;

		if ( withered() ) {
			// Deplete Health Very Quickly.
			if ( witherTime >= 8 ) {
				witherTime = 0;

				if ( getHealth() > 1 ) {
					heal( -1 );
				}

				if ( worldObj.isDaytime() ) {
					int a = MathHelper.floor_double( posX );
					int b = MathHelper.floor_double( boundingBox.minY );
					int c = MathHelper.floor_double( posZ );
					float f = getBrightness( 1.0F );

					if ( f > 0.5F && worldObj.canBlockSeeTheSky( a, b, c )
							&& rand.nextFloat() * 5F < (f - 0.4F) * 2.0F ) {
						setWithered( false );

						if ( tamed() ) {
							setHearts( !didHearts );
						}

						witherTime = 0;
						return;
					}
				}
			}

			setWithered( true );
		} else {
			if ( witherTime % 10 == 0 ) {
				int a = MathHelper.floor_double( posX );
				int b = MathHelper.floor_double( boundingBox.minY );
				int c = MathHelper.floor_double( posZ );
				float f = getBrightness( 1.0F );

				if ( f > 0.05F || worldObj.canBlockSeeTheSky( a, b, c ) ) {
					witherTime = rand.nextInt( 3 );
				} else if ( witherTime >= 900 ) {
					setWithered( true );
					witherTime = 0;
					return;
				}
			}

			setWithered( false );
		}
	}

	public boolean isAirySpace( int a, int b, int c ) {
		if ( b < 0 || b >= worldObj.getHeight() ) {
			return false;
		}

		Block block = worldObj.getBlock( a, b, c );
		if ( block == null || block == Blocks.air )
			return true;

		Material matt = block.getMaterial();

		if ( matt == null || matt == Material.air || matt == Material.plants || matt == Material.vine
				|| matt == Material.fire || matt == Material.circuits || matt == Material.snow ) {
			return true;
		}

		return false;
	}

	private boolean checkFlyBlocked() {
		int a = MathHelper.floor_double( posX );
		int b = MathHelper.floor_double( boundingBox.minY );
		int c = MathHelper.floor_double( posZ );

		if ( !isAirySpace( a, b + 1, c ) || !isAirySpace( a, b + 2, c ) ) {
			return true;
		}

		return false;
	}

	// Checks to see if a fairy is their comrade.
	private boolean sameTeam( EntityFairy fairy ) {
		if ( tamed() ) {
			return fairy.tamed() && fairy.getFaction() == 0 && fairy.rulerName().equals( this.rulerName() );
		} else if ( getFaction() > 0 ) {
			return fairy.getFaction() == this.getFaction();
		}

		return false;
	}
	
	@Override
	public boolean interact( EntityPlayer player ) {
		if ( !worldObj.isRemote
				&& (ridingEntity == null || ridingEntity == player || ridingEntity instanceof EntityMinecart) ) {
			ItemStack stack = player.inventory.getCurrentItem();

			if ( isRuler(player) ) {
				if ( stack != null && getHealth() < getMaxHealth() && acceptableFoods( stack.getItem() )
						&& stack.stackSize > 0 ) {
					stack.stackSize--;

					if ( stack.stackSize <= 0 ) {
						player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
					}

					setHearts( !hearts() );

					if ( stack.getItem() == Items.sugar ) {
						heal( 5 );
					} else {
						heal( 99 );

						if ( stack.getItem() == Items.speckled_melon ) {
							setWithered( false );
							witherTime = 0;
						}
					}

					return true;
				} else if ( stack != null && haircutItem( stack.getItem() ) && stack.stackSize > 0 && !rogue() ) {
					setHairType( !hairType() );
					return true;
				} else if ( stack != null && ridingEntity == null && !isSitting() && vileSubstance( stack.getItem() )
						&& stack.stackSize > 0 ) {
					dropItem( stack.getItem(), 1 );
					stack.stackSize--;

					if ( stack.stackSize <= 0 ) {
						player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
					}

					disband();
					return true;
				} else if ( onGround && stack != null && namingItem( stack.getItem() ) && stack.stackSize > 0 ) {
					stack.stackSize--;

					if ( stack.stackSize <= 0 ) {
						player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
					}

					setSitting( true );
					setNameEnabled( true );
					isJumping = false;
					setPathToEntity( (PathEntity) null );
					setTarget( (Entity) null );
					entityFear = null;
				} else {
					if ( isSitting() ) {
						if ( stack != null && realFreshOysterBars( stack.getItem() ) && stack.stackSize > 0 ) {
							hydraFairy();
						} else {
							setSitting( false );
						}

						return true;
					} else if ( player.isSneaking() ) {
						if ( flymode() || !onGround ) {
							flyTime = 0;
						} else {
							setSitting( true );
							isJumping = false;
							setPathToEntity( (PathEntity) null );
							setTarget( (Entity) null );
							entityFear = null;
						}
					} else if ( stack == null || !snowballItem( stack.getItem() ) ) {
						mountEntity( player );
						setFlymode( true );
						flyTime = 200;
						setCanFlap( true );
						return true;
					}
				}
			} else {
				if ( (getFaction() == 0 || worldObj.difficultySetting == EnumDifficulty.PEACEFUL) && !((queen() || posted()) && tamed())
						&& !crying() && !angry() && stack != null && acceptableFoods( stack.getItem() )
						&& stack.stackSize > 0 ) {
					stack.stackSize--;

					if ( stack.stackSize <= 0 ) {
						player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
					}

					if ( stack.getItem() != Items.sugar || rand.nextInt( 4 ) == 0 ) {
						if ( stack.getItem() == Items.sugar ) {
							heal( 5 );
						} else {
							heal( 99 );
						}

						tameMe( player );
						return true;
					} else {
						setHearts( !hearts() );
						return true;
					}
				} else if ( !tamed() ) {
					setHearts( !hearts() );
				}

				tameFailMessage( player );
				return true;
			}
		}

		return super.interact( player );
	}

	// Foods that can be used for taming
	public boolean acceptableFoods( Item i ) {
		if ( i == Items.speckled_melon ) {
			return true;
		} else if ( tamed() || !queen() ) {
			return i == Items.apple || i == Items.melon || i == Items.sugar
					|| i == Items.cake || i == Items.cookie;
		}

		return false;
	}
	// Things used for disbanding
	public boolean vileSubstance( Item i ) {
		return i == Items.slime_ball || i == Items.rotten_flesh || i == Items.spider_eye
				|| i == Items.fermented_spider_eye;
	}

	// The quickest way to Daphne
	public boolean realFreshOysterBars( Item i ) {
		return i == Items.magma_cream;
	}

	// Item used to rename a fairy, paper
	public boolean namingItem( Item i ) {
		return i == Items.paper;
	}

	// Is the item a snowball or not.
	public boolean snowballItem( Item i ) {
		return i == Items.snowball;
	}

	// Can the item give a haircut.
	public boolean haircutItem( Item i ) {
		return i == Items.shears;
	}

	private void disband() {
		setRulerName( "" );
		setFaction( 0 );
		setHearts( !didHearts );
		cryTime = 200;
		setTamed( false );
		setCustomName( "" );
		abandonPost();
		snowballin = 0;

		if ( ruler != null ) {
			PathEntity doug = roam( ruler, this, (float) Math.PI );

			if ( doug != null ) {
				setPathToEntity( doug );
			}

			if ( ruler instanceof EntityPlayer ) {
				//
				String s = getActualName( getNamePrefix(), getNameSuffix() ) + " ";

				if ( queen() ) {
					s = "Queen " + s;
				}

				int i = rand.nextInt( 6 );

				if ( queen() && i < 3 ) {
					s += "was greatly offended by your offering.";
				} else if ( queen() ) {
					s += "called you a 'dirty peasant' and stormed out.";
				} else if ( i == 0 ) {
					s += "threw it on the ground and had a fit.";
				} else if ( i == 1 ) {
					s += "called you a 'poopy-head' and ran away.";
				} else if ( i == 2 ) {
					s += "would rather die than eat that gross thing.";
				} else if ( i == 3 ) {
					s += "decided not to be your friend anymore.";
				} else if ( i == 4 ) {
					s += "gave you a dirty look and headed off.";
				} else {
					s += "says that's the grossest thing she's ever seen.";
				}

				FairyFactions.proxy.sendChat( (EntityPlayerMP)ruler, "* �9" + s );
			}
		}

		ruler = null;
	}
	
	public void tameMe( EntityPlayer player ) {
		if ( player == null ) {
			return;
		}

		setFaction( 0 );
		setTamed( true );
		setRulerName( player.getGameProfile().getName() );
		setHearts( !hearts() );
		abandonPost();
		snowballin = 0;
		ruler = player;

		if ( scout() ) {
			cower = false;
		}

		String f = getActualName( getNamePrefix(), getNameSuffix() ) + " ";

		if ( queen() ) {
			f = "Queen " + f;
		}

		String s = f;
		int i = rand.nextInt( 6 );

		if ( queen() && i < 2 ) {
			s += "reluctantly joined your party.";
		} else if ( queen() && i > 3 ) {
			s += "sighed and became your follower.";
		} else if ( queen() ) {
			s += "really enjoys eating glistering melons.";
		} else if ( i == 0 ) {
			s += "was kind of lonely without a leader.";
		} else if ( i == 1 ) {
			s += "shrugged and decided to follow you.";
		} else if ( i == 2 ) {
			s += "put the past behind her and joined you.";
		} else if ( i == 3 ) {
			s += "was easily persuaded by that yummy snack.";
		} else if ( i == 4 ) {
			s += "introduced herself properly to you.";
		} else {
			s += "ate that snack like there was no tomorrow.";
		}

		if( player instanceof EntityPlayerMP ) {
			FairyFactions.proxy.sendChat( (EntityPlayerMP)player, "* �9" + s );
		}
		
		FairyFactions.LOGGER.info( "tameMe: "+rulerName()+": "+this );
	}
	
	// Don't let that spider bite you, spider bite hurt.
	public void hydraFairy() 
	{
		double a = (boundingBox.minX + boundingBox.maxX) / 2D;
		double b = (boundingBox.minY + (double) yOffset) - (double) ySize;
		double c = (boundingBox.minZ + boundingBox.maxZ) / 2D;
		motionX = 0D;
		motionY = -0.1D;
		motionZ = 0D;
		// Anthony stopped to tie his shoe, and they all went marching on.
		isJumping = false; 
		moveForward = 0F;
		moveStrafing = 0F;
		setPathToEntity( (PathEntity) null );
		setSitting( true );
		onGround = true;
		List list = worldObj.getEntitiesWithinAABB( EntityFairy.class, boundingBox.expand( 80D, 80D, 80D ) );

		for ( int j = 0; j < list.size(); j++ ) {
			EntityFairy fairy = (EntityFairy) list.get( j );

			if ( fairy != this && fairy.getHealth() > 0 && sameTeam( fairy ) && fairy.ridingEntity == null
					&& fairy.riddenByEntity == null ) {
				fairy.setTarget( (Entity) null );
				fairy.cryTime = 0;
				fairy.entityFear = null;
				// I'll pay top dollar for that Gidrovlicheskiy in the window.
				fairy.setPosition( a, b, c ); 
				fairy.motionX = 0D;
				fairy.motionY = -0.1D;
				fairy.motionZ = 0D;
				fairy.isJumping = false;
				fairy.moveForward = 0F;
				fairy.moveStrafing = 0F;
				fairy.setPathToEntity( (PathEntity) null );
				fairy.setSitting( true );
				fairy.onGround = true;
				// It feels like I'm floating but I'm not
				fairy.setFlymode( false ); 
			}
		}
	}
	
	public void tameFailMessage( EntityPlayer player ) {
		String s = "You can't ";

		if ( angry() ) {
			s += "tame this fairy because she's angry right now.";
		} else if ( crying() ) {
			s += "tame this fairy because she's upset right now.";
		} else if ( getFaction() > 0 ) {
			if ( queen() ) {
				s += "tame a fairy queen until you defeat her minions.";
			} else {
				s += "tame this fairy until you defeat her queen.";
			}
		} else if ( tamed() && queen() ) {
			s += "steal a fairy queen owned by someone else.";
		} else if ( posted() ) {
			s += "steal a fairy who's assigned to a post";
		} else {
			ItemStack stack = (ItemStack) null;

			if ( player.inventory != null ) {
				stack = player.inventory.getCurrentItem();
			}

			if ( stack != null && stack.stackSize > 0 && stack.getItem() == Items.glowstone_dust ) {
				s += "seriously be trying to feed a fairy something that resembles its own guts.";
			} else if ( queen() ) {
				s += "tame a fairy queen without a slice of speckled melon.";
			} else {
				s += "tame a fairy without a sweet-tasting snack.";
			}
		}

		if( player instanceof EntityPlayerMP ) {
			FairyFactions.proxy.sendChat( (EntityPlayerMP)player, "* �9" + s );
		}
	}

	/**
	 * TODO: cache results similar to RenderFairy's lookup
	 * @param skin which type of fairy is this?
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(int skin) {
		final String texturePath;
        if (getCustomName().equals("Steve")) {
            texturePath = "textures/entities/notFairy.png";
        } else {
        	final int idx;
	        if (skin < 0) {
	            idx = 1;
	        } else if (skin > 3) {
	            idx = 4;
	        } else {
	        	idx = skin + 1;
	        }
	        texturePath = "textures/entities/fairy" + (queen() ? "q" : "") + idx + ".png";
        }
		return new ResourceLocation(Version.ASSET_PREFIX, texturePath);
	}

	public Entity getFishEntity() {
		return fishEntity;
	}

	public void setFishEntity(FairyEntityFishHook fishEntity) {
		this.fishEntity = fishEntity;
	}

	public Entity getEntityFear() {
		return entityFear;
	}

	public void setEntityFear( Entity entityFear ) {
		this.entityFear = entityFear;
	}

	public int getCryTime() {
		return cryTime;
	}

	public void setCryTime( int cryTime ) {
		this.cryTime = cryTime;
	}

	public int getFlyTime() {
		return flyTime;
	}

	public void setFlyTime( int flyTime ) {
		this.flyTime = flyTime;
	}

	public boolean willCower() {
		return cower;
	}

	public void setCower(boolean cower) {
		this.cower = cower;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable p_90011_1_) {
		// No fairy breeding.
		return null;
	}

}

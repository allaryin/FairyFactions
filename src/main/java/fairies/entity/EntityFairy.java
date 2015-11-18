package fairies.entity;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;

public class EntityFairy extends EntityAnimal {
		
	public EntityFairy(World world) {
		super(world);
		this.setHealth(15);
		
		// fairy-specific init
		setSkin(rand.nextInt(4));
		setJob(rand.nextInt(4));
		setSpecialJob(false);
		setFaction(0);
		setFairyName(rand.nextInt(16), rand.nextInt(16));
	}
	
	// DataWatcher objects
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
	
	// ---------- flag 1 -----------
	
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

    public int getFairyName1() {
        return (byte)dataWatcher.getWatchableObjectByte(B_NAME_ORIG) & 0x0f;
    }
    public int getFairyName2() {
        return (byte)(dataWatcher.getWatchableObjectByte(B_NAME_ORIG) >> 4) & 0x0f;
    }

    private static final String name1[] = {
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

    private static final String name2[] = {
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
    
    public String getActualName(int prefix, int suffix) {
    	final String custom = "";//getCustomName();
    	if( custom != null && !custom.isEmpty() )
    		return custom;
    	
    	if( prefix < 0 || prefix > MAX_NAMEIDX || suffix < 0 || suffix > MAX_NAMEIDX ) {
    		return "Error-name";
    	} else {
    		return name1[prefix] + "-" + name2[suffix];
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

	// no baby fairies for now
	@Override
	public EntityAgeable createChild(EntityAgeable parent) {
		return null;
	}

}

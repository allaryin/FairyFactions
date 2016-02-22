package fairies;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class FairyConfig extends Configuration {
	
    public static int		SPAWN_FACTION_MIN_SIZE	= 8;
    public static int		SPAWN_FACTION_MAX_SIZE	= 10;
    
	public static double	DEF_BASE_HEALTH			= 15.0D;
	public static float		DEF_BASE_SPEED			= 0.9F;
	public static float		DEF_SCOUT_SPEED			= 1.05F;
	public static float		DEF_WITHER_MULT			= 0.75F;

	// fall speed
	public static double	DEF_FLOAT_RATE			= -0.2D;
	// fly speed
	public static double	DEF_FLAP_RATE			= 0.15D;
	// bonus to flight while unburdened
	public static double	DEF_SOLO_FLAP_MULT		= 1.25D;
	// bonus to flight when launching
	public static double	DEF_LIFTOFF_MULT		= 2.0D;

	public static int		DEF_MAX_PARTICLES		= 5;

	// how far will we path to something?
	public static float		DEF_PATH_RANGE			= 16F;
	// how far will we chase something?
	public static float		DEF_PURSUE_RANGE		= DEF_PATH_RANGE;
	// how close will guards protect the queen from?
	public static float		DEF_DEFEND_RANGE		= DEF_PURSUE_RANGE / 2;
	// how far will we flee from something?
	public static float		DEF_FEAR_RANGE			= 12F;

	// how long will tame fairies stay mad? 3x for wild
	public static final int	DEF_AGGRO_TIMER			= 15;					

	public FairyConfig(File file) {
		super(file);
		
		load();
		init();
		save();
	}

	private void init() {
		/**
		 * Spawning behaviors
		 */
        SPAWN_FACTION_MAX_SIZE = getInt("max", "spawning", SPAWN_FACTION_MAX_SIZE,
                0, 30, "maximum fairy spawn group size");
        SPAWN_FACTION_MIN_SIZE = getInt("min", "spawning", SPAWN_FACTION_MIN_SIZE,
                0, 30, "minimum fairy spawn group size");
        // TODO: validate that min is <= max
        
        /**
         * General fairy stats
         */
        //GENERAL_HEALTH_BASE = getFloat("health.base", "general", )
	}

}

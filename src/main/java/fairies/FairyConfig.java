package fairies;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class FairyConfig extends Configuration {
	
    public static int SPAWN_FACTION_MIN_SIZE = 8;
    public static int SPAWN_FACTION_MAX_SIZE = 10;

	public FairyConfig(File file) {
		super(file);
		
		load();
		init();
		save();
	}

	private void init() {
        SPAWN_FACTION_MAX_SIZE = getInt("max", "spawning", SPAWN_FACTION_MAX_SIZE,
                0, 30, "maximum fairy spawn group size");
        SPAWN_FACTION_MIN_SIZE = getInt("min", "spawning", SPAWN_FACTION_MIN_SIZE,
                0, 30, "minimum fairy spawn group size");
	}

}

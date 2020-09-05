package org.mcupdater.fairies;

import net.minecraftforge.common.ForgeConfigSpec;

public class FairyConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	public static final General GENERAL = new General(BUILDER);
	public static final Spawn SPAWN = new Spawn(BUILDER);
	public static final Behavior BEHAVIOR = new Behavior(BUILDER);

	public static class General {
		General(ForgeConfigSpec.Builder builder) {
			builder.push("general");
			builder.pop();
		}
	}

	public static class Spawn {
		Spawn(ForgeConfigSpec.Builder builder) {
			builder.push("spawn");
			builder.pop();
		}
	}

	public static class Behavior {
		Behavior(ForgeConfigSpec.Builder builder) {
			builder.push("behavior");
			builder.pop();
		}
	}
}

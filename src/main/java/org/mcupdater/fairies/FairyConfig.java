package org.mcupdater.fairies;

import net.minecraftforge.common.ForgeConfigSpec;

public class FairyConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	public static final General GENERAL = new General(BUILDER);
	public static final Spawn SPAWN = new Spawn(BUILDER);
	public static final Behavior BEHAVIOR = new Behavior(BUILDER);

	public static class General {
		public final ForgeConfigSpec.DoubleValue healthBase;
		public final ForgeConfigSpec.DoubleValue speedBase;
		public final ForgeConfigSpec.DoubleValue speedScout;
		public final ForgeConfigSpec.DoubleValue speedWitherMult;

		General(ForgeConfigSpec.Builder builder) {
			builder.push("general");
			healthBase = builder
					.comment("Base fairy maximum health")
					.translation("fairyfactions.config.healthBase")
					.defineInRange("healthBase",15.0,1.0,40.0);
			speedBase = builder
					.comment("Base fairy move speed")
					.translation("fairyfactions.config.speedBase")
					.defineInRange("speedBase",0.9,0.1,2.0);
			speedScout = builder
					.comment("Scout fairy move speed")
					.translation("fairyfactions.config.speedScout")
					.defineInRange("speedScout",1.05,0.1,2.0);
			speedWitherMult = builder
					.comment("Wither debuff speed multiplier (lower is slower)")
					.translation("fairyfactions.config.speedWitherMult")
					.defineInRange("speedWitherMult",0.75,0.05,0.95);
			builder.pop();
		}
	}

	public static class Spawn {
		public final ForgeConfigSpec.IntValue factionMinSize;
		public final ForgeConfigSpec.IntValue factionMaxSize;

		Spawn(ForgeConfigSpec.Builder builder) {
			builder.push("spawn");
			factionMinSize = builder
					.comment("Minimum number of fairies that spawn together in a single faction")
					.translation("fairyfactions.config.factionMinSize")
					.defineInRange("factionMinSize", 8, 1, 16);
			factionMaxSize = builder
					.comment("Maximum number of fairies that spawn together in a single faction")
					.translation("fairyfactions.config.factionMaxSize")
					.defineInRange("factionMaxSize",10,1,32);
			builder.pop();
		}
	}

	public static class Behavior {
		public final ForgeConfigSpec.DoubleValue pathRange;
		public final ForgeConfigSpec.DoubleValue pursueRangeMult;
		public final ForgeConfigSpec.DoubleValue defendRangeMult;
		public final ForgeConfigSpec.DoubleValue fearRange;

		public final ForgeConfigSpec.IntValue aggroTimer;
		public final ForgeConfigSpec.DoubleValue aggroMultWild;

		Behavior(ForgeConfigSpec.Builder builder) {
			builder.push("behavior");
			pathRange = builder
					.comment("How far will fairies path to something?")
					.translation("fairyfactions.config.pathRange")
					.defineInRange("pathRange",16.0,4.0,32.0);
			pursueRangeMult = builder
					.comment("How much farther will we chase something than our normal pathing?")
					.translation("fairyfactions.config.pursueRangeMult")
					.defineInRange("pursueRangeMult",1.0,0.25,2.0);
			defendRangeMult = builder
					.comment("How close will guards stay to their queen?")
					.translation("fairyfactions.config.defendRangeMult")
					.defineInRange("defendRangeMult",0.5,0.25,2.0);
			fearRange = builder
					.comment("How far will fairies will run away when afraid?")
					.translation("fairyfactions.config.fearRange")
					.defineInRange("fearRange",12.0,4.0,32.0);
			aggroTimer = builder
					.comment("How long will tame fairies stay angry?")
					.translation("fairyfactions.config.aggroTimer")
					.defineInRange("aggroTimer",15,1,100);
			aggroMultWild = builder
					.comment("How much longer will wild fairies stay angry?")
					.translation("fairyfactions.config.aggroMultWild")
					.defineInRange("aggroMultWild",3.0,0.5,10.0);
			builder.pop();
		}
	}
}

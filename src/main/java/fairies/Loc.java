package fairies;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Constants for various message localizations as well as shortcut methods
 * to look them up for use.
 */
public enum Loc {
	QUEEN("text.queen.prefix"),
	
	FACTION_1("text.faction.1.name"),
	FACTION_2("text.faction.2.name"),
	FACTION_3("text.faction.3.name"),
	FACTION_4("text.faction.4.name"),
	FACTION_5("text.faction.5.name"),
	FACTION_6("text.faction.6.name"),
	FACTION_7("text.faction.7.name"),
	FACTION_8("text.faction.8.name"),
	FACTION_9("text.faction.9.name"),
	FACTION_10("text.faction.10.name"),
	FACTION_11("text.faction.11.name"),
	FACTION_12("text.faction.12.name"),
	FACTION_13("text.faction.13.name"),
	FACTION_14("text.faction.14.name"),
	FACTION_15("text.faction.15.name");
	
	public final String key;
	private Loc(String key) {
		this.key = key;
	}

	private static final LanguageRegistry REG = LanguageRegistry.instance();
	
	public String get() {
		return getLoc(this.key);
	}
	
	public static String getLoc(String key) {
		final String res = REG.getStringLocalization(key);
		if( res.isEmpty() )
			return "#"+key+"#";
		else
			return res;
	}
}

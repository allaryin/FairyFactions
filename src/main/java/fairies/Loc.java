package fairies;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Constants for various message localizations as well as shortcut methods
 * to look them up for use.
 */
public enum Loc {
	QUEEN("text.queen.prefix");
	
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

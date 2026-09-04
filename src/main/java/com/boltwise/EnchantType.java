package com.boltwise;

import java.util.EnumMap;
import java.util.Map;
import net.runelite.api.gameval.ItemID;

enum EnchantType
{
	OPAL("Opal bolts", ItemID.OPAL_BOLT, ItemID.XBOWS_CROSSBOW_BOLTS_BRONZE_TIPPED_OPAL_ENCHANTED, 4, 9, runes(RuneCost.COSMIC, 1, RuneCost.AIR, 2)),
	SAPPHIRE("Sapphire bolts", ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_SAPPHIRE, ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_SAPPHIRE_ENCHANTED, 7, 17, runes(RuneCost.COSMIC, 1, RuneCost.WATER, 1, RuneCost.MIND, 1)),
	JADE("Jade bolts", ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_TIPPED_JADE, ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_TIPPED_JADE_ENCHANTED, 14, 19, runes(RuneCost.COSMIC, 1, RuneCost.EARTH, 2)),
	PEARL("Pearl bolts", ItemID.PEARL_BOLT, ItemID.XBOWS_CROSSBOW_BOLTS_IRON_TIPPED_PEARL_ENCHANTED, 24, 29, runes(RuneCost.COSMIC, 1, RuneCost.WATER, 2)),
	EMERALD("Emerald bolts", ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_EMERALD, ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_EMERALD_ENCHANTED, 27, 37, runes(RuneCost.COSMIC, 1, RuneCost.AIR, 3, RuneCost.NATURE, 1)),
	TOPAZ("Topaz bolts", ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_TIPPED_REDTOPAZ, ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_TIPPED_REDTOPAZ_ENCHANTED, 29, 33, runes(RuneCost.COSMIC, 1, RuneCost.FIRE, 2)),
	RUBY("Ruby bolts", ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_RUBY, ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_RUBY_ENCHANTED, 49, 59, runes(RuneCost.COSMIC, 1, RuneCost.FIRE, 5, RuneCost.BLOOD, 1)),
	DIAMOND("Diamond bolts", ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_DIAMOND, ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_DIAMOND_ENCHANTED, 57, 67, runes(RuneCost.COSMIC, 1, RuneCost.EARTH, 10, RuneCost.LAW, 2)),
	DRAGONSTONE("Dragonstone bolts", ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_DRAGONSTONE, ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_DRAGONSTONE_ENCHANTED, 68, 78, runes(RuneCost.COSMIC, 1, RuneCost.EARTH, 15, RuneCost.SOUL, 1)),
	ONYX("Onyx bolts", ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_ONYX, ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_ONYX_ENCHANTED, 87, 97, runes(RuneCost.COSMIC, 1, RuneCost.FIRE, 20, RuneCost.DEATH, 1)),
	OPAL_DRAGON("Opal dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_OPAL, ItemID.DRAGON_BOLTS_ENCHANTED_OPAL, 4, 9, runes(RuneCost.COSMIC, 1, RuneCost.AIR, 2)),
	SAPPHIRE_DRAGON("Sapphire dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_SAPPHIRE, ItemID.DRAGON_BOLTS_ENCHANTED_SAPPHIRE, 7, 17, runes(RuneCost.COSMIC, 1, RuneCost.WATER, 1, RuneCost.MIND, 1)),
	JADE_DRAGON("Jade dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_JADE, ItemID.DRAGON_BOLTS_ENCHANTED_JADE, 14, 19, runes(RuneCost.COSMIC, 1, RuneCost.EARTH, 2)),
	PEARL_DRAGON("Pearl dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_PEARL, ItemID.DRAGON_BOLTS_ENCHANTED_PEARL, 24, 29, runes(RuneCost.COSMIC, 1, RuneCost.WATER, 2)),
	EMERALD_DRAGON("Emerald dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_EMERALD, ItemID.DRAGON_BOLTS_ENCHANTED_EMERALD, 27, 37, runes(RuneCost.COSMIC, 1, RuneCost.AIR, 3, RuneCost.NATURE, 1)),
	TOPAZ_DRAGON("Topaz dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_TOPAZ, ItemID.DRAGON_BOLTS_ENCHANTED_TOPAZ, 29, 33, runes(RuneCost.COSMIC, 1, RuneCost.FIRE, 2)),
	RUBY_DRAGON("Ruby dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, ItemID.DRAGON_BOLTS_ENCHANTED_RUBY, 49, 59, runes(RuneCost.COSMIC, 1, RuneCost.FIRE, 5, RuneCost.BLOOD, 1)),
	DIAMOND_DRAGON("Diamond dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_DIAMOND, ItemID.DRAGON_BOLTS_ENCHANTED_DIAMOND, 57, 67, runes(RuneCost.COSMIC, 1, RuneCost.EARTH, 10, RuneCost.LAW, 2)),
	DRAGONSTONE_DRAGON("Dragonstone dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_DRAGONSTONE, ItemID.DRAGON_BOLTS_ENCHANTED_DRAGONSTONE, 68, 78, runes(RuneCost.COSMIC, 1, RuneCost.EARTH, 15, RuneCost.SOUL, 1)),
	ONYX_DRAGON("Onyx dragon bolts", ItemID.DRAGON_BOLTS_UNENCHANTED_ONYX, ItemID.DRAGON_BOLTS_ENCHANTED_ONYX, 87, 97, runes(RuneCost.COSMIC, 1, RuneCost.FIRE, 20, RuneCost.DEATH, 1));

	private final String displayName;
	private final int unenchantedId;
	private final int enchantedId;
	private final int magicLevel;
	private final int xpPerCast;
	private final Map<RuneCost, Integer> runeCosts;

	EnchantType(String displayName, int unenchantedId, int enchantedId, int magicLevel, int xpPerCast, Map<RuneCost, Integer> runeCosts)
	{
		this.displayName = displayName;
		this.unenchantedId = unenchantedId;
		this.enchantedId = enchantedId;
		this.magicLevel = magicLevel;
		this.xpPerCast = xpPerCast;
		this.runeCosts = runeCosts;
	}

	String getDisplayName() { return displayName; }
	int getUnenchantedId() { return unenchantedId; }
	int getEnchantedId() { return enchantedId; }
	int getMagicLevel() { return magicLevel; }
	int getXpPerCast() { return xpPerCast; }
	Map<RuneCost, Integer> getRuneCosts() { return runeCosts; }

	private static Map<RuneCost, Integer> runes(Object... values)
	{
		Map<RuneCost, Integer> result = new EnumMap<>(RuneCost.class);
		for (int i = 0; i < values.length; i += 2)
		{
			result.put((RuneCost) values[i], (Integer) values[i + 1]);
		}
		return result;
	}
}

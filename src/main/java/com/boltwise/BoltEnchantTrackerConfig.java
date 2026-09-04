package com.boltwise;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(BoltEnchantTrackerConfig.GROUP)
public interface BoltEnchantTrackerConfig extends Config
{
	String GROUP = "bolt-enchant-tracker";

	@Range(min = 2, max = 99)
	@ConfigItem(keyName = "targetMagicLevel", name = "Target Magic level", description = "Level used for remaining bolts, time, and cost projections", position = 0)
	default int targetMagicLevel() { return 85; }

	@ConfigItem(keyName = "inputPriceOverride", name = "Bolt buy price override", description = "Actual price per unenchanted bolt; set to 0 for the live guide price", position = 1)
	default int inputPriceOverride() { return 0; }

	@ConfigItem(keyName = "outputPriceOverride", name = "Bolt sell price override", description = "Expected price per enchanted bolt; set to 0 for the live guide price", position = 2)
	default int outputPriceOverride() { return 0; }

	@ConfigItem(keyName = "includeGeTax", name = "Include GE tax", description = "Deduct Grand Exchange tax from enchanted-bolt revenue", position = 3)
	default boolean includeGeTax() { return true; }

	@ConfigItem(keyName = "freeAirRunes", name = "Free air runes", description = "Enable when your equipment supplies unlimited air runes", position = 4)
	default boolean freeAirRunes() { return false; }

	@ConfigItem(keyName = "freeWaterRunes", name = "Free water runes", description = "Enable when your equipment supplies unlimited water runes", position = 5)
	default boolean freeWaterRunes() { return false; }

	@ConfigItem(keyName = "freeEarthRunes", name = "Free earth runes", description = "Enable when your equipment supplies unlimited earth runes", position = 6)
	default boolean freeEarthRunes() { return false; }

	@ConfigItem(keyName = "freeFireRunes", name = "Free fire runes", description = "Enable when a staff, Tome of Fire, or other equipment supplies unlimited fire runes", position = 7)
	default boolean freeFireRunes() { return false; }
}

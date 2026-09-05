package com.boltwise;

import java.util.Map;

final class SessionSnapshot
{
	private final long elapsedMillis;
	private final boolean paused;
	private final EnchantType latestType;
	private final Map<EnchantType, Long> enchantedCounts;
	private final Map<EnchantType, Long> castCounts;

	SessionSnapshot(long elapsedMillis, boolean paused, EnchantType latestType,
		Map<EnchantType, Long> enchantedCounts, Map<EnchantType, Long> castCounts)
	{
		this.elapsedMillis = elapsedMillis;
		this.paused = paused;
		this.latestType = latestType;
		this.enchantedCounts = enchantedCounts;
		this.castCounts = castCounts;
	}

	long getElapsedMillis() { return elapsedMillis; }
	boolean isPaused() { return paused; }
	EnchantType getLatestType() { return latestType; }
	Map<EnchantType, Long> getEnchantedCounts() { return enchantedCounts; }
	Map<EnchantType, Long> getCastCounts() { return castCounts; }
	long getTotalBolts() { return enchantedCounts.values().stream().mapToLong(Long::longValue).sum(); }
	long getTotalCasts() { return castCounts.values().stream().mapToLong(Long::longValue).sum(); }
	long getMagicXp()
	{
		return castCounts.entrySet().stream()
			.mapToLong(entry -> entry.getValue() * entry.getKey().getXpPerCast()).sum();
	}
}

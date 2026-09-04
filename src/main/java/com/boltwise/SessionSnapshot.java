package com.boltwise;

import java.util.Map;

final class SessionSnapshot
{
	private final long startedAtMillis;
	private final long nowMillis;
	private final EnchantType latestType;
	private final Map<EnchantType, Long> enchantedCounts;
	private final Map<EnchantType, Long> castCounts;

	SessionSnapshot(long startedAtMillis, long nowMillis, EnchantType latestType,
		Map<EnchantType, Long> enchantedCounts, Map<EnchantType, Long> castCounts)
	{
		this.startedAtMillis = startedAtMillis;
		this.nowMillis = nowMillis;
		this.latestType = latestType;
		this.enchantedCounts = enchantedCounts;
		this.castCounts = castCounts;
	}

	long getElapsedMillis() { return startedAtMillis == 0 ? 0 : Math.max(1, nowMillis - startedAtMillis); }
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

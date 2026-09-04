package com.boltwise;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Item;

final class SessionTracker
{
	private final Map<Integer, Integer> previousQuantities = new HashMap<>();
	private final Map<EnchantType, Long> enchantedCounts = new EnumMap<>(EnchantType.class);
	private final Map<EnchantType, Long> castCounts = new EnumMap<>(EnchantType.class);
	private final Map<EnchantType, Integer> pendingInputLosses = new EnumMap<>(EnchantType.class);
	private final Map<EnchantType, Long> pendingLossTimes = new EnumMap<>(EnchantType.class);
	private long startedAtMillis;
	private EnchantType latestType;
	private boolean initialized;

	synchronized long acceptInventory(Item[] items, long nowMillis)
	{
		Map<Integer, Integer> current = countItems(items);
		if (!initialized)
		{
			previousQuantities.putAll(current);
			initialized = true;
			return 0;
		}
		long detectedBolts = 0;

		for (EnchantType type : EnchantType.values())
		{
			long pendingTime = pendingLossTimes.getOrDefault(type, 0L);
			if (pendingTime > 0 && nowMillis - pendingTime > 1_500L)
			{
				pendingInputLosses.remove(type);
				pendingLossTimes.remove(type);
			}
			int inputLoss = previousQuantities.getOrDefault(type.getUnenchantedId(), 0)
				- current.getOrDefault(type.getUnenchantedId(), 0);
			int outputGain = current.getOrDefault(type.getEnchantedId(), 0)
				- previousQuantities.getOrDefault(type.getEnchantedId(), 0);
			if (inputLoss > 0)
			{
				pendingInputLosses.merge(type, inputLoss, Integer::sum);
				pendingLossTimes.put(type, nowMillis);
			}
			int pendingLoss = pendingInputLosses.getOrDefault(type, 0);
			if (outputGain > 0 && pendingLoss >= outputGain)
			{
				if (startedAtMillis == 0)
				{
					startedAtMillis = nowMillis;
				}
				enchantedCounts.merge(type, (long) outputGain, Long::sum);
				castCounts.merge(type, (long) ((outputGain + 9) / 10), Long::sum);
				latestType = type;
				detectedBolts += outputGain;
				int remainingLoss = pendingLoss - outputGain;
				if (remainingLoss == 0)
				{
					pendingInputLosses.remove(type);
					pendingLossTimes.remove(type);
				}
				else
				{
					pendingInputLosses.put(type, remainingLoss);
				}
			}
		}

		previousQuantities.clear();
		previousQuantities.putAll(current);
		return detectedBolts;
	}

	synchronized void reset(Item[] currentInventory)
	{
		enchantedCounts.clear();
		castCounts.clear();
		startedAtMillis = 0;
		latestType = null;
		rebaseline(currentInventory);
	}

	/**
	 * Replaces the inventory baseline without clearing the current session.
	 * Used across world hops so the inventory being reloaded cannot look like
	 * newly enchanted bolts.
	 */
	synchronized void rebaseline(Item[] currentInventory)
	{
		pendingInputLosses.clear();
		pendingLossTimes.clear();
		previousQuantities.clear();
		previousQuantities.putAll(countItems(currentInventory));
		initialized = true;
	}

	synchronized SessionSnapshot snapshot(long nowMillis)
	{
		return new SessionSnapshot(startedAtMillis, nowMillis, latestType,
			new EnumMap<>(enchantedCounts), new EnumMap<>(castCounts));
	}

	private static Map<Integer, Integer> countItems(Item[] items)
	{
		if (items == null)
		{
			return Collections.emptyMap();
		}
		Map<Integer, Integer> counts = new HashMap<>();
		for (Item item : items)
		{
			if (item != null && item.getId() > 0 && item.getQuantity() > 0)
			{
				counts.merge(item.getId(), item.getQuantity(), Integer::sum);
			}
		}
		return counts;
	}
}

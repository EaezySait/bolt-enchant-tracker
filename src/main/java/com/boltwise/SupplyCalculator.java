package com.boltwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class SupplyCalculator
{
	private static final int BOLTS_PER_CAST = 10;

	private SupplyCalculator() { }

	static SupplySummary calculate(EnchantType type, long boltQuantity,
		Map<Integer, Long> itemQuantities, Predicate<RuneCost> isFree)
	{
		List<SupplyAmount> supplies = new ArrayList<>();
		List<Limit> limits = new ArrayList<>();
		long safeBoltQuantity = Math.max(0L, boltQuantity);
		supplies.add(new SupplyAmount(type.getUnenchantedId(), "Bolts", safeBoltQuantity, false));
		limits.add(new Limit("Bolts", divideRoundingUp(safeBoltQuantity, BOLTS_PER_CAST)));

		for (Map.Entry<RuneCost, Integer> entry : type.getRuneCosts().entrySet())
		{
			RuneCost rune = entry.getKey();
			boolean unlimited = isFree.test(rune);
			long quantity = usableRuneQuantity(rune, itemQuantities);
			supplies.add(new SupplyAmount(rune.getItemId(), rune.getDisplayName(), quantity, unlimited));
			if (!unlimited)
			{
				limits.add(new Limit(rune.getDisplayName(), quantity / entry.getValue()));
			}
		}

		long castsLeft = limits.stream().mapToLong(limit -> limit.casts).min().orElse(0L);
		StringBuilder limitingSupply = new StringBuilder();
		for (Limit limit : limits)
		{
			if (limit.casts == castsLeft)
			{
				if (limitingSupply.length() > 0)
				{
					limitingSupply.append(" + ");
				}
				limitingSupply.append(limit.name);
			}
		}

		return new SupplySummary(supplies, castsLeft, limitingSupply.toString());
	}

	private static long usableRuneQuantity(RuneCost rune, Map<Integer, Long> itemQuantities)
	{
		long quantity = 0;
		for (int itemId : rune.getUsableItemIds())
		{
			quantity += itemQuantities.getOrDefault(itemId, 0L);
		}
		return quantity;
	}

	private static long divideRoundingUp(long value, long divisor)
	{
		return value <= 0 ? 0 : (value + divisor - 1) / divisor;
	}

	private static final class Limit
	{
		private final String name;
		private final long casts;

		private Limit(String name, long casts)
		{
			this.name = name;
			this.casts = casts;
		}
	}
}

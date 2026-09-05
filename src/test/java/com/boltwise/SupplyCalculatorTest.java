package com.boltwise;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SupplyCalculatorTest
{
	@Test
	public void calculatesRubyDragonCastsFromLimitingRune()
	{
		Map<Integer, Long> quantities = new HashMap<>();
		quantities.put(ItemID.COSMICRUNE, 1_000L);
		quantities.put(ItemID.FIRERUNE, 10_000L);
		quantities.put(ItemID.BLOODRUNE, 700L);

		SupplySummary summary = SupplyCalculator.calculate(
			EnchantType.RUBY_DRAGON, 25_000L, quantities, rune -> false);

		assertEquals(700L, summary.castsLeft);
		assertEquals("Blood", summary.limitingSupply);
	}

	@Test
	public void freeElementalRuneDoesNotLimitCasts()
	{
		Map<Integer, Long> quantities = new HashMap<>();
		quantities.put(ItemID.COSMICRUNE, 1_000L);
		quantities.put(ItemID.BLOODRUNE, 1_000L);

		SupplySummary summary = SupplyCalculator.calculate(
			EnchantType.RUBY_DRAGON, 25_000L, quantities, rune -> rune == RuneCost.FIRE);

		assertEquals(1_000L, summary.castsLeft);
		assertEquals("Cosmic + Blood", summary.limitingSupply);
	}

	@Test
	public void combinationRunesCountAsElementalRunes()
	{
		Map<Integer, Long> quantities = new HashMap<>();
		quantities.put(ItemID.COSMICRUNE, 100L);
		quantities.put(ItemID.SMOKERUNE, 500L);
		quantities.put(ItemID.BLOODRUNE, 100L);

		SupplySummary summary = SupplyCalculator.calculate(
			EnchantType.RUBY_DRAGON, 1_000L, quantities, rune -> false);

		assertEquals(100L, summary.castsLeft);
		assertEquals("Bolts + Fire + Cosmic + Blood", summary.limitingSupply);
	}
}

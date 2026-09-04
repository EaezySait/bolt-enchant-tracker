package com.boltwise;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProfitCalculatorTest
{
	@Test
	public void appliesTwoPercentGeTaxRoundedDown()
	{
		assertEquals(2_931L, ProfitCalculator.afterTaxUnitPrice(2_990L, true));
		assertEquals(49L, ProfitCalculator.afterTaxUnitPrice(49L, true));
		assertEquals(2_990L, ProfitCalculator.afterTaxUnitPrice(2_990L, false));
	}

	@Test
	public void calculatesBoltAndRuneCosts()
	{
		assertEquals(57L, ProfitCalculator.batchProfit(10, 1, 2_864, 2_990, 613, true));
	}
}

package com.boltwise;

final class ProfitCalculator
{
	private static final long GE_TAX_CAP = 5_000_000L;

	private ProfitCalculator() { }

	static long afterTaxUnitPrice(long unitPrice, boolean includeTax)
	{
		if (!includeTax || unitPrice <= 0)
		{
			return Math.max(0, unitPrice);
		}
		long tax = Math.min(unitPrice / 50L, GE_TAX_CAP);
		return unitPrice - tax;
	}

	static long batchProfit(long bolts, long casts, long inputUnitPrice,
		long outputUnitPrice, long runeCostPerCast, boolean includeTax)
	{
		return bolts * (afterTaxUnitPrice(outputUnitPrice, includeTax) - inputUnitPrice)
			- casts * runeCostPerCast;
	}
}

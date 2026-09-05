package com.boltwise;

import java.util.Collections;
import java.util.List;

final class DashboardSnapshot
{
	final String boltName;
	final long bolts;
	final long casts;
	final long magicXp;
	final long boltsPerHour;
	final long xpPerHour;
	final long inputPrice;
	final long outputPrice;
	final long runeCostPerCast;
	final long sessionProfit;
	final long profitPerHour;
	final long profitPer11000;
	final int targetLevel;
	final long xpRemaining;
	final long boltsRemaining;
	final long millisRemaining;
	final long projectedProfit;
	final boolean autoPaused;
	final List<SupplyDisplay> supplies;
	final long castsAvailable;
	final String limitingSupply;

	DashboardSnapshot(String boltName, long bolts, long casts, long magicXp,
		long boltsPerHour, long xpPerHour, long inputPrice, long outputPrice,
		long runeCostPerCast, long sessionProfit, long profitPerHour, long profitPer11000,
		int targetLevel, long xpRemaining, long boltsRemaining,
		long millisRemaining, long projectedProfit, boolean autoPaused,
		List<SupplyDisplay> supplies, long castsAvailable, String limitingSupply)
	{
		this.boltName = boltName;
		this.bolts = bolts;
		this.casts = casts;
		this.magicXp = magicXp;
		this.boltsPerHour = boltsPerHour;
		this.xpPerHour = xpPerHour;
		this.inputPrice = inputPrice;
		this.outputPrice = outputPrice;
		this.runeCostPerCast = runeCostPerCast;
		this.sessionProfit = sessionProfit;
		this.profitPerHour = profitPerHour;
		this.profitPer11000 = profitPer11000;
		this.targetLevel = targetLevel;
		this.xpRemaining = xpRemaining;
		this.boltsRemaining = boltsRemaining;
		this.millisRemaining = millisRemaining;
		this.projectedProfit = projectedProfit;
		this.autoPaused = autoPaused;
		this.supplies = Collections.unmodifiableList(supplies);
		this.castsAvailable = castsAvailable;
		this.limitingSupply = limitingSupply;
	}

	static DashboardSnapshot empty(int targetLevel)
	{
		return new DashboardSnapshot("Waiting for enchantment…", 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, targetLevel, 0, 0, 0, 0, false,
			Collections.emptyList(), 0, "—");
	}
}

package com.boltwise;

import java.util.Collections;
import java.util.List;

final class SupplySummary
{
	final List<SupplyAmount> supplies;
	final long castsLeft;
	final String limitingSupply;

	SupplySummary(List<SupplyAmount> supplies, long castsLeft, String limitingSupply)
	{
		this.supplies = Collections.unmodifiableList(supplies);
		this.castsLeft = castsLeft;
		this.limitingSupply = limitingSupply;
	}
}

package com.boltwise;

final class SupplyAmount
{
	final int itemId;
	final String name;
	final long quantity;
	final boolean unlimited;

	SupplyAmount(int itemId, String name, long quantity, boolean unlimited)
	{
		this.itemId = itemId;
		this.name = name;
		this.quantity = quantity;
		this.unlimited = unlimited;
	}
}

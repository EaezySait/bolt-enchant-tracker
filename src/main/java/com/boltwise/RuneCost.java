package com.boltwise;

import net.runelite.api.gameval.ItemID;

enum RuneCost
{
	AIR(ItemID.AIRRUNE), WATER(ItemID.WATERRUNE), EARTH(ItemID.EARTHRUNE), FIRE(ItemID.FIRERUNE),
	MIND(ItemID.MINDRUNE), NATURE(ItemID.NATURERUNE), LAW(ItemID.LAWRUNE), COSMIC(ItemID.COSMICRUNE),
	BLOOD(ItemID.BLOODRUNE), SOUL(ItemID.SOULRUNE), DEATH(ItemID.DEATHRUNE);

	private final int itemId;

	RuneCost(int itemId)
	{
		this.itemId = itemId;
	}

	int getItemId()
	{
		return itemId;
	}
}

package com.boltwise;

import net.runelite.api.gameval.ItemID;

enum RuneCost
{
	AIR("Air", ItemID.AIRRUNE, ItemID.MISTRUNE, ItemID.DUSTRUNE, ItemID.SMOKERUNE),
	WATER("Water", ItemID.WATERRUNE, ItemID.MISTRUNE, ItemID.MUDRUNE, ItemID.STEAMRUNE),
	EARTH("Earth", ItemID.EARTHRUNE, ItemID.DUSTRUNE, ItemID.MUDRUNE, ItemID.LAVARUNE),
	FIRE("Fire", ItemID.FIRERUNE, ItemID.SMOKERUNE, ItemID.STEAMRUNE, ItemID.LAVARUNE),
	MIND("Mind", ItemID.MINDRUNE),
	NATURE("Nature", ItemID.NATURERUNE),
	LAW("Law", ItemID.LAWRUNE),
	COSMIC("Cosmic", ItemID.COSMICRUNE),
	BLOOD("Blood", ItemID.BLOODRUNE),
	SOUL("Soul", ItemID.SOULRUNE),
	DEATH("Death", ItemID.DEATHRUNE);

	private final String displayName;
	private final int itemId;
	private final int[] usableItemIds;

	RuneCost(String displayName, int itemId, int... additionalItemIds)
	{
		this.displayName = displayName;
		this.itemId = itemId;
		this.usableItemIds = new int[additionalItemIds.length + 1];
		this.usableItemIds[0] = itemId;
		System.arraycopy(additionalItemIds, 0, usableItemIds, 1, additionalItemIds.length);
	}

	String getDisplayName()
	{
		return displayName;
	}

	int getItemId()
	{
		return itemId;
	}

	int[] getUsableItemIds()
	{
		return usableItemIds.clone();
	}
}

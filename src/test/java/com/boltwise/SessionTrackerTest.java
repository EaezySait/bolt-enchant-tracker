package com.boltwise;

import net.runelite.api.Item;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SessionTrackerTest
{
	@Test
	public void detectsRubyDragonBoltEnchanting()
	{
		SessionTracker tracker = new SessionTracker();
		tracker.acceptInventory(new Item[]{new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 100)}, 1_000L);
		tracker.acceptInventory(new Item[]{
			new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 90),
			new Item(ItemID.DRAGON_BOLTS_ENCHANTED_RUBY, 10)
		}, 2_000L);

		SessionSnapshot snapshot = tracker.snapshot(3_000L);
		assertEquals(EnchantType.RUBY_DRAGON, snapshot.getLatestType());
		assertEquals(10L, snapshot.getTotalBolts());
		assertEquals(1L, snapshot.getTotalCasts());
		assertEquals(59L, snapshot.getMagicXp());
	}

	@Test
	public void ignoresOrdinaryBankWithdrawal()
	{
		SessionTracker tracker = new SessionTracker();
		tracker.acceptInventory(new Item[0], 1_000L);
		tracker.acceptInventory(new Item[]{new Item(ItemID.DRAGON_BOLTS_ENCHANTED_RUBY, 1_000)}, 2_000L);

		SessionSnapshot snapshot = tracker.snapshot(3_000L);
		assertNull(snapshot.getLatestType());
		assertEquals(0L, snapshot.getTotalBolts());
	}

	@Test
	public void detectsWhenInputAndOutputChangesArriveSeparately()
	{
		SessionTracker tracker = new SessionTracker();
		tracker.acceptInventory(new Item[]{new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 100)}, 1_000L);
		tracker.acceptInventory(new Item[]{new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 90)}, 1_100L);
		tracker.acceptInventory(new Item[]{
			new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 90),
			new Item(ItemID.DRAGON_BOLTS_ENCHANTED_RUBY, 10)
		}, 1_200L);

		assertEquals(10L, tracker.snapshot(2_000L).getTotalBolts());
	}

	@Test
	public void resetClearsSessionAndKeepsInventoryBaseline()
	{
		SessionTracker tracker = new SessionTracker();
		Item[] before = {new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_ONYX, 20)};
		Item[] after = {
			new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_ONYX, 10),
			new Item(ItemID.DRAGON_BOLTS_ENCHANTED_ONYX, 10)
		};
		tracker.acceptInventory(before, 1_000L);
		tracker.acceptInventory(after, 2_000L);
		tracker.reset(after);

		SessionSnapshot snapshot = tracker.snapshot(3_000L);
		assertNull(snapshot.getLatestType());
		assertEquals(0L, snapshot.getTotalBolts());
	}

	@Test
	public void rebaselinePreservesSessionWithoutCountingReloadedInventory()
	{
		SessionTracker tracker = new SessionTracker();
		Item[] before = {new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 100)};
		Item[] afterCast = {
			new Item(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY, 90),
			new Item(ItemID.DRAGON_BOLTS_ENCHANTED_RUBY, 10)
		};
		tracker.acceptInventory(before, 1_000L);
		tracker.acceptInventory(afterCast, 2_000L);

		tracker.rebaseline(null);
		tracker.rebaseline(afterCast);

		SessionSnapshot snapshot = tracker.snapshot(3_000L);
		assertEquals(EnchantType.RUBY_DRAGON, snapshot.getLatestType());
		assertEquals(10L, snapshot.getTotalBolts());
		assertEquals(1L, snapshot.getTotalCasts());
	}
}

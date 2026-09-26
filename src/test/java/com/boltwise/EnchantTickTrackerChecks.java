package com.boltwise;

/** Dependency-free checks, also invoked by the JUnit test suite. */
public final class EnchantTickTrackerChecks
{
	public static void main(String[] args)
	{
		EnchantTickTracker tracker = new EnchantTickTracker();
		tracker.record(10);
		tracker.tick(600_000_000L);
		check(tracker.getStreak() == 1, "first cast");
		tracker.record(10);
		tracker.record(0);
		tracker.tick(1_200_000_000L);
		check(tracker.getStreak() == 2 && tracker.getBest() == 2, "consecutive casts");
		check(tracker.progress(1_500_000_000L) == 0.5, "half-tick progress");
		check(tracker.progress(2_000_000_000L) == 1, "bar clamps during delay");
		tracker.tick(1_800_000_000L);
		check(tracker.getStreak() == 0 && tracker.getBest() == 2, "missed tick keeps best");
		tracker.record(4);
		tracker.record(6);
		tracker.tick(2_400_000_000L);
		check(tracker.getStreak() == 1, "split updates counted once");
		tracker.record(20);
		tracker.tick(3_000_000_000L);
		check(tracker.getStreak() == 0, "batched casts cannot invent streak");
		tracker.record(10);
		tracker.tick(3_600_000_000L);
		tracker.record(10);
		tracker.tick(5_000_000_000L);
		check(tracker.getStreak() == 1, "long delay breaks continuity");
		tracker.record(10);
		tracker.interrupt();
		tracker.tick(5_600_000_000L);
		check(tracker.getStreak() == 0 && tracker.getBest() == 2, "hop discards pending but keeps best");
		tracker.reset();
		check(tracker.getBest() == 0 && tracker.progress(6_000_000_000L) == 0, "session reset");
		System.out.println("Enchant tick tracker: all checks passed.");
	}

	private static void check(boolean condition, String message)
	{
		if (!condition) { throw new AssertionError(message); }
	}
}

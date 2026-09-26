package com.boltwise;

/** Tracks confirmed inventory conversions grouped by the received server tick. */
final class EnchantTickTracker
{
	private long pendingBolts;
	private long streak;
	private long best;
	private long lastTickNanos;
	private boolean synchronizedTick;

	synchronized void record(long bolts)
	{
		if (bolts > 0)
		{
			pendingBolts += bolts;
		}
	}

	synchronized void tick(long nowNanos)
	{
		// A delayed/batched update cannot establish how many individual ticks
		// contained casts. Break the streak rather than inventing successes.
		boolean timely = synchronizedTick && nowNanos - lastTickNanos > 0
			&& nowNanos - lastTickNanos < 1_200_000_000L;
		if (pendingBolts > 0 && pendingBolts <= 10)
		{
			streak = timely ? streak + 1 : 1;
			best = Math.max(best, streak);
		}
		else
		{
			streak = 0;
		}
		pendingBolts = 0;
		lastTickNanos = nowNanos;
		synchronizedTick = true;
	}

	synchronized void interrupt()
	{
		pendingBolts = 0;
		streak = 0;
		synchronizedTick = false;
	}

	synchronized void reset()
	{
		interrupt();
		best = 0;
	}

	synchronized long getStreak() { return streak; }
	synchronized long getBest() { return best; }

	synchronized double progress(long nowNanos)
	{
		if (!synchronizedTick)
		{
			return 0;
		}
		return Math.max(0, Math.min(1, (nowNanos - lastTickNanos) / 600_000_000.0));
	}
}

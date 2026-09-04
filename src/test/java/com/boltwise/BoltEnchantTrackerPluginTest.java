package com.boltwise;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BoltEnchantTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BoltEnchantTrackerPlugin.class);
		RuneLite.main(args);
	}
}

package com.boltwise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import javax.inject.Inject;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

final class BoltWiseOverlay extends OverlayPanel
{
	private static final Color GOLD = new Color(255, 184, 45);
	private static final Color GREEN = new Color(73, 203, 115);
	private static final Color RED = new Color(235, 87, 87);
	private static final NumberFormat NUMBERS = NumberFormat.getIntegerInstance();

	private final BoltEnchantTrackerPlugin plugin;
	private final BoltEnchantTrackerConfig config;

	@Inject
	BoltWiseOverlay(BoltEnchantTrackerPlugin plugin, BoltEnchantTrackerConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setPreferredSize(new Dimension(210, 0));
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "BoltWise overlay");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		DashboardSnapshot snapshot = plugin.getDashboardSnapshot();
		if (!config.showOverlay() || snapshot == null || snapshot.bolts == 0)
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("BoltWise")
			.color(GOLD)
			.build());
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(snapshot.autoPaused ? "II AUTO-PAUSED" : "ACTIVE")
			.color(snapshot.autoPaused ? GOLD : GREEN)
			.build());

		panelComponent.getChildren().add(new SupplyIconsComponent(snapshot.supplies));
		panelComponent.getChildren().add(line("Casts left", number(snapshot.castsAvailable), Color.WHITE));
		panelComponent.getChildren().add(line("Bolts/hour", number(snapshot.boltsPerHour), Color.WHITE));
		panelComponent.getChildren().add(line("Profit/hour", compactSignedGp(snapshot.profitPerHour),
			snapshot.profitPerHour > 0 ? GREEN : snapshot.profitPerHour < 0 ? RED : Color.WHITE));

		return super.render(graphics);
	}

	private static LineComponent line(String left, String right, Color rightColor)
	{
		return LineComponent.builder()
			.left(left)
			.right(right)
			.rightColor(rightColor)
			.build();
	}

	private static String compactSignedGp(long value)
	{
		long absolute = Math.abs(value);
		String amount;
		if (absolute >= 1_000_000L)
		{
			amount = decimal(absolute, 1_000_000L) + "m";
		}
		else if (absolute >= 1_000L)
		{
			amount = decimal(absolute, 1_000L) + "k";
		}
		else
		{
			amount = number(absolute);
		}
		return (value > 0 ? "+" : value < 0 ? "-" : "") + amount + " gp";
	}

	private static String decimal(long value, long divisor)
	{
		long whole = value / divisor;
		long tenth = value % divisor * 10L / divisor;
		return tenth == 0 ? Long.toString(whole) : whole + "." + tenth;
	}

	private static String number(long value)
	{
		return NUMBERS.format(value);
	}
}

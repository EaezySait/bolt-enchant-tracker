package com.boltwise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Slf4j
final class BoltEnchantTrackerPanel extends PluginPanel
{
	private static final NumberFormat NUMBERS = NumberFormat.getIntegerInstance();
	private static final Color GOLD = new Color(255, 184, 45);
	private static final Color GREEN = new Color(73, 203, 115);
	private static final Color RED = new Color(235, 87, 87);

	private final Supplier<DashboardSnapshot> snapshotSupplier;
	private final Map<String, JLabel> values = new LinkedHashMap<>();
	private final JLabel boltName = new JLabel("Waiting for enchantment…", SwingConstants.CENTER);
	private final JLabel status = new JLabel("", SwingConstants.CENTER);
	private final JPanel supplyIcons = new JPanel(new GridLayout(1, 0, 4, 0));
	private final Timer timer;

	BoltEnchantTrackerPanel(Supplier<DashboardSnapshot> snapshotSupplier, Runnable resetAction)
	{
		this.snapshotSupplier = snapshotSupplier;
		setBorder(new EmptyBorder(8, 8, 8, 8));

		JLabel title = new JLabel("BoltWise", SwingConstants.CENTER);
		title.setForeground(GOLD);
		title.setFont(FontManager.getRunescapeBoldFont().deriveFont(22f));
		add(title);

		boltName.setForeground(Color.WHITE);
		boltName.setFont(FontManager.getRunescapeBoldFont());
		boltName.setBorder(new EmptyBorder(4, 0, 8, 0));
		add(boltName);

		status.setFont(FontManager.getRunescapeSmallFont());
		status.setBorder(new EmptyBorder(0, 0, 6, 0));
		add(status);

		add(section("SESSION", new String[][]{
			{"bolts", "Bolts enchanted"}, {"casts", "Spell casts"}, {"xp", "Magic XP"},
			{"boltsHour", "Bolts/hour"}, {"xpHour", "XP/hour"}
		}));

		add(section("ECONOMICS", new String[][]{
			{"buy", "Bolt buy price"}, {"sell", "Bolt sell price"}, {"runes", "Runes/cast"},
			{"profit", "Session profit"}, {"profitHour", "Profit/hour"}, {"profit11k", "Profit per 11,000"}
		}));

		add(section("TARGET", new String[][]{
			{"target", "Magic target"}, {"xpLeft", "XP remaining"}, {"boltsLeft", "Bolts remaining"},
			{"timeLeft", "Estimated time"}, {"projected", "Projected profit"}
		}));

		add(suppliesSection());

		JButton reset = new JButton("Reset session");
		reset.setFocusable(false);
		reset.addActionListener(event -> resetAction.run());
		add(reset);

		timer = new Timer(1_000, event -> refreshSafely());
		timer.setInitialDelay(0);
	}

	void start()
	{
		refreshSafely();
		timer.start();
	}

	void refreshNow()
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			refreshSafely();
		}
		else
		{
			SwingUtilities.invokeLater(this::refreshSafely);
		}
	}

	void stop()
	{
		timer.stop();
	}

	private JPanel section(String heading, String[][] rows)
	{
		JPanel outer = new JPanel(new BorderLayout(0, 5));
		outer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		outer.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(7, 7, 7, 7)));

		JLabel headingLabel = new JLabel(heading);
		headingLabel.setForeground(GOLD);
		headingLabel.setFont(FontManager.getRunescapeSmallFont());
		outer.add(headingLabel, BorderLayout.NORTH);

		JPanel grid = new JPanel(new GridLayout(0, 1, 0, 3));
		grid.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		for (String[] row : rows)
		{
			JPanel line = new JPanel(new BorderLayout());
			line.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			JLabel label = new JLabel(row[1]);
			label.setForeground(Color.LIGHT_GRAY);
			JLabel value = new JLabel("—", SwingConstants.RIGHT);
			value.setForeground(Color.WHITE);
			values.put(row[0], value);
			line.add(label, BorderLayout.WEST);
			line.add(value, BorderLayout.EAST);
			grid.add(line);
		}
		outer.add(grid, BorderLayout.CENTER);
		outer.setMaximumSize(new Dimension(PANEL_WIDTH, Integer.MAX_VALUE));
		return outer;
	}

	private JPanel suppliesSection()
	{
		JPanel outer = new JPanel(new BorderLayout(0, 5));
		outer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		outer.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(7, 7, 7, 7)));

		JLabel headingLabel = new JLabel("SUPPLIES");
		headingLabel.setForeground(GOLD);
		headingLabel.setFont(FontManager.getRunescapeSmallFont());
		outer.add(headingLabel, BorderLayout.NORTH);

		supplyIcons.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		outer.add(supplyIcons, BorderLayout.CENTER);

		JPanel rows = new JPanel(new GridLayout(0, 1, 0, 3));
		rows.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		rows.add(supplyLine("castsAvailable", "Casts left"));
		rows.add(supplyLine("limitingSupply", "Limited by"));
		outer.add(rows, BorderLayout.SOUTH);
		outer.setMaximumSize(new Dimension(PANEL_WIDTH, Integer.MAX_VALUE));
		return outer;
	}

	private JPanel supplyLine(String key, String text)
	{
		JPanel line = new JPanel(new BorderLayout());
		line.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JLabel label = new JLabel(text);
		label.setForeground(Color.LIGHT_GRAY);
		JLabel value = new JLabel("—", SwingConstants.RIGHT);
		value.setForeground(Color.WHITE);
		values.put(key, value);
		line.add(label, BorderLayout.WEST);
		line.add(value, BorderLayout.EAST);
		return line;
	}

	private void refresh()
	{
		DashboardSnapshot snapshot = snapshotSupplier.get();
		boltName.setText(snapshot.boltName);
		status.setText(snapshot.bolts == 0 ? "" : snapshot.autoPaused ? "Ⅱ AUTO-PAUSED" : "● ACTIVE");
		status.setForeground(snapshot.autoPaused ? GOLD : GREEN);
		set("bolts", number(snapshot.bolts));
		set("casts", number(snapshot.casts));
		set("xp", number(snapshot.magicXp));
		set("boltsHour", number(snapshot.boltsPerHour));
		set("xpHour", number(snapshot.xpPerHour));
		set("buy", gp(snapshot.inputPrice));
		set("sell", gp(snapshot.outputPrice));
		set("runes", gp(snapshot.runeCostPerCast));
		setProfit("profit", snapshot.sessionProfit);
		setProfit("profitHour", snapshot.profitPerHour);
		setProfit("profit11k", snapshot.profitPer11000);
		set("target", snapshot.targetLevel + " Magic");
		set("xpLeft", number(snapshot.xpRemaining));
		set("boltsLeft", number(snapshot.boltsRemaining));
		set("timeLeft", duration(snapshot.millisRemaining));
		setProfit("projected", snapshot.projectedProfit);
		set("castsAvailable", number(snapshot.castsAvailable));
		set("limitingSupply", snapshot.limitingSupply);
		refreshSupplies(snapshot);
	}

	private void refreshSupplies(DashboardSnapshot snapshot)
	{
		supplyIcons.removeAll();
		for (SupplyDisplay supply : snapshot.supplies)
		{
			JPanel item = new JPanel();
			item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
			item.setBackground(ColorScheme.DARKER_GRAY_COLOR);

			JLabel icon = new JLabel(toIcon(supply.icon));
			icon.setAlignmentX(CENTER_ALIGNMENT);
			item.add(icon);

			JLabel amount = new JLabel(supply.unlimited ? "∞" : number(supply.quantity), SwingConstants.CENTER);
			amount.setForeground(Color.WHITE);
			amount.setFont(FontManager.getRunescapeSmallFont());
			amount.setAlignmentX(CENTER_ALIGNMENT);
			item.add(amount);

			JLabel name = new JLabel(supply.name, SwingConstants.CENTER);
			name.setForeground(Color.LIGHT_GRAY);
			name.setFont(FontManager.getRunescapeSmallFont());
			name.setAlignmentX(CENTER_ALIGNMENT);
			item.add(name);
			supplyIcons.add(item);
		}
		supplyIcons.revalidate();
		supplyIcons.repaint();
	}

	private static ImageIcon toIcon(BufferedImage image)
	{
		return image == null ? null : new ImageIcon(image);
	}

	private void refreshSafely()
	{
		try
		{
			refresh();
		}
		catch (Throwable ex)
		{
			log.warn("Unable to refresh BoltWise dashboard", ex);
		}
	}

	private void set(String key, String text)
	{
		values.get(key).setText(text);
	}

	private void setProfit(String key, long amount)
	{
		JLabel label = values.get(key);
		label.setText((amount > 0 ? "+" : "") + gp(amount));
		label.setForeground(amount > 0 ? GREEN : amount < 0 ? RED : Color.WHITE);
	}

	private static String number(long value)
	{
		return NUMBERS.format(value);
	}

	private static String gp(long value)
	{
		return number(value) + " gp";
	}

	private static String duration(long millis)
	{
		if (millis <= 0)
		{
			return "—";
		}
		long totalMinutes = Math.max(1, millis / 60_000L);
		long hours = totalMinutes / 60;
		long minutes = totalMinutes % 60;
		return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
	}
}

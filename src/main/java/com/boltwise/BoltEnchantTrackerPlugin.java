package com.boltwise;

import com.google.inject.Provides;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.Experience;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Bolt Enchant Tracker",
	description = "Tracks bolt enchanting speed, Magic XP, costs, profit, and target-level progress",
	tags = {"bolts", "enchant", "magic", "xp", "profit", "tracker", "ge"}
)
public class BoltEnchantTrackerPlugin extends Plugin
{
	private static final int[] RUNE_POUCH_QUANTITY_VARBITS = {
		VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2,
		VarbitID.RUNE_POUCH_QUANTITY_3, VarbitID.RUNE_POUCH_QUANTITY_4,
		VarbitID.RUNE_POUCH_QUANTITY_5, VarbitID.RUNE_POUCH_QUANTITY_6
	};
	private static final int[] RUNE_POUCH_TYPE_VARBITS = {
		VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2,
		VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4,
		VarbitID.RUNE_POUCH_TYPE_5, VarbitID.RUNE_POUCH_TYPE_6
	};

	private final SessionTracker tracker = new SessionTracker();

	@Inject private Client client;
	@Inject private ClientThread clientThread;
	@Inject private ClientToolbar clientToolbar;
	@Inject private ItemManager itemManager;
	@Inject private BoltEnchantTrackerConfig config;
	@Inject private OverlayManager overlayManager;
	@Inject private BoltWiseOverlay overlay;

	private BoltEnchantTrackerPanel panel;
	private NavigationButton navigationButton;
	private volatile int currentMagicXp;
	private volatile DashboardSnapshot dashboardSnapshot;

	@Override
	protected void startUp()
	{
		dashboardSnapshot = DashboardSnapshot.empty(config.targetMagicLevel());
		panel = new BoltEnchantTrackerPanel(() -> dashboardSnapshot, this::requestReset);
		navigationButton = NavigationButton.builder()
			.tooltip("Bolt Enchant Tracker")
			.icon(createBoltIcon())
			.priority(7)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navigationButton);
		overlayManager.add(overlay);
		SwingUtilities.invokeLater(panel::start);
		clientThread.invoke(() ->
		{
			currentMagicXp = client.getSkillExperience(Skill.MAGIC);
			tracker.reset(getInventoryItems());
			refreshDashboard();
		});
		log.debug("Bolt Enchant Tracker started");
	}

	@Override
	protected void shutDown()
	{
		if (panel != null)
		{
			SwingUtilities.invokeLater(panel::stop);
		}
		if (navigationButton != null)
		{
			clientToolbar.removeNavigation(navigationButton);
		}
		overlayManager.remove(overlay);
		panel = null;
		navigationButton = null;
		log.debug("Bolt Enchant Tracker stopped");
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (event.getContainerId() == InventoryID.INV || event.getItemContainer() == inventory)
		{
			recordInventory(event.getItemContainer().getItems(), "container event");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Some game scripts update stack quantities without producing the expected
		// inventory container event. Scanning 28 slots once per game tick is a cheap,
		// reliable fallback, and SessionTracker prevents duplicate detections.
		recordInventory(getInventoryItems(), "game-tick fallback");
		if (tracker.snapshot(System.currentTimeMillis(), config.autoPause()).getLatestType() != null)
		{
			refreshDashboard();
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() == Skill.MAGIC)
		{
			currentMagicXp = event.getXp();
			if (tracker.snapshot(System.currentTimeMillis(), config.autoPause()).getLatestType() != null)
			{
				refreshDashboard();
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGED_IN:
				currentMagicXp = client.getSkillExperience(Skill.MAGIC);
				tracker.rebaseline(getInventoryItems());
				refreshDashboard();
				break;
			case HOPPING:
				// Preserve session totals, but discard the old inventory baseline so
				// items reappearing after the hop cannot be counted as enchantments.
				tracker.rebaseline(null);
				break;
			case LOGIN_SCREEN:
				tracker.reset(null);
				refreshDashboard();
				break;
			default:
				break;
		}
	}

	@Provides
	BoltEnchantTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BoltEnchantTrackerConfig.class);
	}

	private void requestReset()
	{
		clientThread.invoke(() ->
		{
			tracker.reset(getInventoryItems());
			refreshDashboard();
		});
	}

	private Item[] getInventoryItems()
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		return inventory == null ? null : inventory.getItems();
	}

	private void recordInventory(Item[] items, String source)
	{
		long detected = tracker.acceptInventory(items, System.currentTimeMillis());
		if (detected > 0)
		{
			log.debug("Detected {} enchanted bolts via {}", detected, source);
			refreshDashboard();
			SessionSnapshot session = tracker.snapshot(System.currentTimeMillis(), config.autoPause());
			log.debug("BoltWise session state: type={}, bolts={}, casts={}",
				session.getLatestType(), session.getTotalBolts(), session.getTotalCasts());
		}
	}

	private void refreshDashboard()
	{
		try
		{
			dashboardSnapshot = createDashboardSnapshot();
		}
		catch (Throwable ex)
		{
			// Price/config lookups should never prevent the core tracking totals from
			// reaching the sidebar. Keep a statistics-only snapshot as a safe fallback.
			dashboardSnapshot = createTrackingSnapshot();
			log.warn("Unable to calculate BoltWise economics; showing tracking totals only", ex);
		}

		BoltEnchantTrackerPanel currentPanel = panel;
		if (currentPanel != null)
		{
			currentPanel.refreshNow();
		}
	}

	private DashboardSnapshot createTrackingSnapshot()
	{
		SessionSnapshot session = tracker.snapshot(System.currentTimeMillis(), config.autoPause());
		EnchantType type = session.getLatestType();
		int targetLevel = config.targetMagicLevel();
		if (type == null)
		{
			return DashboardSnapshot.empty(targetLevel);
		}

		long elapsed = session.getElapsedMillis();
		long bolts = session.getTotalBolts();
		long casts = session.getTotalCasts();
		long magicXp = session.getMagicXp();
		long xpPerHour = perHour(magicXp, elapsed);
		long targetXp = Experience.getXpForLevel(targetLevel);
		long xpRemaining = Math.max(0L, targetXp - currentMagicXp);
		long castsRemaining = divideRoundingUp(xpRemaining, type.getXpPerCast());
		SupplySummary supplySummary = createSupplySummary(type);

		return new DashboardSnapshot(type.getDisplayName(), bolts, casts, magicXp,
			perHour(bolts, elapsed), xpPerHour, 0, 0, 0,
			0, 0, 0, targetLevel, xpRemaining, castsRemaining * 10L,
			xpPerHour <= 0 ? 0 : xpRemaining * 3_600_000L / xpPerHour, 0,
			session.isPaused(), createSupplyDisplays(supplySummary, false),
			supplySummary.castsLeft, supplySummary.limitingSupply);
	}

	private DashboardSnapshot createDashboardSnapshot()
	{
		SessionSnapshot session = tracker.snapshot(System.currentTimeMillis(), config.autoPause());
		EnchantType type = session.getLatestType();
		int targetLevel = config.targetMagicLevel();
		if (type == null)
		{
			return DashboardSnapshot.empty(targetLevel);
		}

		long elapsed = session.getElapsedMillis();
		long bolts = session.getTotalBolts();
		long casts = session.getTotalCasts();
		long magicXp = session.getMagicXp();
		long boltsPerHour = perHour(bolts, elapsed);
		long xpPerHour = perHour(magicXp, elapsed);
		long inputPrice = config.inputPriceOverride() > 0
			? config.inputPriceOverride() : itemManager.getItemPrice(type.getUnenchantedId());
		long outputPrice = config.outputPriceOverride() > 0
			? config.outputPriceOverride() : itemManager.getItemPrice(type.getEnchantedId());
		long runeCostPerCast = runeCost(type);

		long sessionProfit = 0;
		for (Map.Entry<EnchantType, Long> entry : session.getEnchantedCounts().entrySet())
		{
			EnchantType entryType = entry.getKey();
			long entryInput = config.inputPriceOverride() > 0
				? config.inputPriceOverride() : itemManager.getItemPrice(entryType.getUnenchantedId());
			long entryOutput = config.outputPriceOverride() > 0
				? config.outputPriceOverride() : itemManager.getItemPrice(entryType.getEnchantedId());
			sessionProfit += ProfitCalculator.batchProfit(entry.getValue(),
				session.getCastCounts().getOrDefault(entryType, 0L), entryInput, entryOutput,
				runeCost(entryType), config.includeGeTax());
		}

		long profitPerHour = perHour(sessionProfit, elapsed);
		long profitPer11000 = ProfitCalculator.batchProfit(11_000, 1_100,
			inputPrice, outputPrice, runeCostPerCast, config.includeGeTax());
		long targetXp = Experience.getXpForLevel(targetLevel);
		long xpRemaining = Math.max(0L, targetXp - currentMagicXp);
		long castsRemaining = divideRoundingUp(xpRemaining, type.getXpPerCast());
		long boltsRemaining = castsRemaining * 10L;
		long millisRemaining = xpPerHour <= 0 ? 0 : xpRemaining * 3_600_000L / xpPerHour;
		long projectedProfit = ProfitCalculator.batchProfit(boltsRemaining, castsRemaining,
			inputPrice, outputPrice, runeCostPerCast, config.includeGeTax());
		SupplySummary supplySummary = createSupplySummary(type);

		return new DashboardSnapshot(type.getDisplayName(), bolts, casts, magicXp,
			boltsPerHour, xpPerHour, inputPrice, outputPrice, runeCostPerCast,
			sessionProfit, profitPerHour, profitPer11000, targetLevel, xpRemaining, boltsRemaining,
			millisRemaining, projectedProfit, session.isPaused(),
			createSupplyDisplays(supplySummary, true), supplySummary.castsLeft,
			supplySummary.limitingSupply);
	}

	DashboardSnapshot getDashboardSnapshot()
	{
		return dashboardSnapshot;
	}

	private SupplySummary createSupplySummary(EnchantType type)
	{
		Map<Integer, Long> quantities = inventoryAndRunePouchQuantities();
		return SupplyCalculator.calculate(type,
			quantities.getOrDefault(type.getUnenchantedId(), 0L), quantities, this::isFree);
	}

	private List<SupplyDisplay> createSupplyDisplays(SupplySummary summary, boolean includeImages)
	{
		List<SupplyDisplay> displays = new ArrayList<>();
		for (SupplyAmount supply : summary.supplies)
		{
			BufferedImage icon = null;
			BufferedImage overlayIcon = null;
			if (includeImages)
			{
				icon = itemManager.getImage(supply.itemId);
				overlayIcon = itemManager.getImage(supply.itemId);
			}
			displays.add(new SupplyDisplay(icon, overlayIcon, supply.name, supply.quantity, supply.unlimited));
		}
		return displays;
	}

	private Map<Integer, Long> inventoryAndRunePouchQuantities()
	{
		Map<Integer, Long> quantities = new HashMap<>();
		Item[] inventoryItems = getInventoryItems();
		if (inventoryItems != null)
		{
			for (Item item : inventoryItems)
			{
				if (item != null && item.getId() > 0 && item.getQuantity() > 0)
				{
					quantities.merge(item.getId(), (long) item.getQuantity(), Long::sum);
				}
			}
		}

		EnumComposition runePouchRunes = client.getEnum(EnumID.RUNEPOUCH_RUNE);
		for (int i = 0; i < RUNE_POUCH_TYPE_VARBITS.length; i++)
		{
			int runeType = client.getVarbitValue(RUNE_POUCH_TYPE_VARBITS[i]);
			int quantity = client.getVarbitValue(RUNE_POUCH_QUANTITY_VARBITS[i]);
			if (runeType != 0 && quantity > 0)
			{
				int itemId = runePouchRunes.getIntValue(runeType);
				quantities.merge(itemId, (long) quantity, Long::sum);
			}
		}
		return quantities;
	}

	private long runeCost(EnchantType type)
	{
		long total = 0;
		for (Map.Entry<RuneCost, Integer> entry : type.getRuneCosts().entrySet())
		{
			if (!isFree(entry.getKey()))
			{
				total += (long) itemManager.getItemPrice(entry.getKey().getItemId()) * entry.getValue();
			}
		}
		return total;
	}

	private boolean isFree(RuneCost rune)
	{
		switch (rune)
		{
			case AIR: return config.freeAirRunes();
			case WATER: return config.freeWaterRunes();
			case EARTH: return config.freeEarthRunes();
			case FIRE: return config.freeFireRunes();
			default: return false;
		}
	}

	private static long perHour(long amount, long elapsedMillis)
	{
		return elapsedMillis <= 0 ? 0 : amount * 3_600_000L / elapsedMillis;
	}

	private static long divideRoundingUp(long value, long divisor)
	{
		return value <= 0 ? 0 : (value + divisor - 1) / divisor;
	}

	private static BufferedImage createBoltIcon()
	{
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(new Color(37, 33, 26));
		graphics.fillOval(1, 1, 22, 22);
		graphics.setColor(new Color(255, 184, 45));
		graphics.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawLine(6, 18, 18, 6);
		graphics.drawLine(14, 6, 18, 6);
		graphics.drawLine(18, 6, 18, 10);
		graphics.drawLine(6, 18, 9, 18);
		graphics.drawLine(6, 18, 6, 15);
		graphics.dispose();
		return image;
	}
}

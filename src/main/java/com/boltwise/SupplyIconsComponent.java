package com.boltwise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.List;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

final class SupplyIconsComponent implements LayoutableRenderableEntity
{
	private static final NumberFormat NUMBERS = NumberFormat.getIntegerInstance();
	private static final int GAP = 3;

	private final List<SupplyDisplay> supplies;
	private final Rectangle bounds = new Rectangle();
	private Point preferredLocation = new Point();
	private Dimension preferredSize = new Dimension(190, 0);

	SupplyIconsComponent(List<SupplyDisplay> supplies)
	{
		this.supplies = supplies;
	}

	@Override
	public Rectangle getBounds()
	{
		return bounds;
	}

	@Override
	public void setPreferredLocation(Point preferredLocation)
	{
		this.preferredLocation = preferredLocation;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize)
	{
		this.preferredSize = preferredSize;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (supplies.isEmpty())
		{
			return new Dimension(0, 0);
		}

		int width = preferredSize.width;
		int cellWidth = width / supplies.size();
		int imageHeight = 0;
		for (SupplyDisplay supply : supplies)
		{
			if (supply.overlayIcon != null)
			{
				imageHeight = Math.max(imageHeight, supply.overlayIcon.getHeight());
			}
		}

		FontMetrics metrics = graphics.getFontMetrics();
		int textBaseline = preferredLocation.y + imageHeight + GAP + metrics.getAscent();
		for (int i = 0; i < supplies.size(); i++)
		{
			SupplyDisplay supply = supplies.get(i);
			int centerX = preferredLocation.x + i * cellWidth + cellWidth / 2;
			BufferedImage image = supply.overlayIcon;
			if (image != null)
			{
				graphics.drawImage(image, centerX - image.getWidth() / 2, preferredLocation.y, null);
			}

			String quantity = supply.unlimited ? "∞" : compact(supply.quantity);
			int textX = centerX - metrics.stringWidth(quantity) / 2;
			graphics.setColor(Color.BLACK);
			graphics.drawString(quantity, textX + 1, textBaseline + 1);
			graphics.setColor(Color.WHITE);
			graphics.drawString(quantity, textX, textBaseline);
		}

		Dimension dimension = new Dimension(width, imageHeight + GAP + metrics.getHeight());
		bounds.setLocation(preferredLocation);
		bounds.setSize(dimension);
		return dimension;
	}

	private static String compact(long value)
	{
		if (value >= 1_000_000L)
		{
			return value / 1_000_000L + "m";
		}
		if (value >= 1_000L)
		{
			return value / 1_000L + "k";
		}
		return NUMBERS.format(value);
	}
}

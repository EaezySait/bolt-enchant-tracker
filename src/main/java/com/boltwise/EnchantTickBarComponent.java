package com.boltwise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

final class EnchantTickBarComponent implements LayoutableRenderableEntity
{
	private final double progress;
	private final Rectangle bounds = new Rectangle();
	private Point location = new Point();
	private Dimension size = new Dimension(190, 12);

	EnchantTickBarComponent(double progress) { this.progress = progress; }
	@Override public Rectangle getBounds() { return bounds; }
	@Override public void setPreferredLocation(Point location) { this.location = location; }
	@Override public void setPreferredSize(Dimension size) { this.size = size; }

	@Override
	public Dimension render(Graphics2D graphics)
	{
		int width = Math.max(4, size.width);
		Graphics2D g = (Graphics2D) graphics.create();
		try
		{
			g.setColor(Color.BLACK);
			g.fillRect(location.x, location.y, width, 12);
			g.setColor(new Color(45, 43, 38));
			g.fillRect(location.x + 1, location.y + 1, width - 2, 10);
			int fill = (int) Math.round((width - 2) * progress);
			g.setColor(new Color(73, 203, 115));
			g.fillRect(location.x + 1, location.y + 1, fill, 10);
			g.setColor(Color.WHITE);
			int marker = location.x + 1 + Math.min(width - 3, fill);
			g.drawLine(marker, location.y + 1, marker, location.y + 10);
		}
		finally
		{
			g.dispose();
		}
		bounds.setBounds(location.x, location.y, width, 12);
		return new Dimension(width, 12);
	}
}

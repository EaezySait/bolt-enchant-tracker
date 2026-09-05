package com.boltwise;

import java.awt.image.BufferedImage;

final class SupplyDisplay
{
	final BufferedImage icon;
	final BufferedImage overlayIcon;
	final String name;
	final long quantity;
	final boolean unlimited;

	SupplyDisplay(BufferedImage icon, BufferedImage overlayIcon, String name, long quantity, boolean unlimited)
	{
		this.icon = icon;
		this.overlayIcon = overlayIcon;
		this.name = name;
		this.quantity = quantity;
		this.unlimited = unlimited;
	}
}

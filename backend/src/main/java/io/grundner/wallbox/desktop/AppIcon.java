package io.grundner.wallbox.desktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Zeichnet das App-Symbol (grüne Kachel mit Blitz, docs/ui/theme.md) in beliebiger
 * Größe – für das Symbol im Infobereich und als Vorlage der Windows-Icon-Datei.
 */
public final class AppIcon {

	private static final Color BRAND = new Color(0x8c, 0xff, 0x70);

	private AppIcon() {
	}

	public static BufferedImage render(int size) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			double s = size;
			g.setColor(BRAND);
			g.fill(new RoundRectangle2D.Double(0, 0, s, s, s * 0.45, s * 0.45));

			// Blitz aus Icons.tsx (24er-Raster: M13 2 4 14h7l-1 8 9-12h-7l1-8z)
			double u = s / 24.0;
			Path2D bolt = new Path2D.Double();
			bolt.moveTo(13 * u, 2.5 * u);
			bolt.lineTo(4.5 * u, 13.5 * u);
			bolt.lineTo(11.5 * u, 13.5 * u);
			bolt.lineTo(10.5 * u, 21.5 * u);
			bolt.lineTo(19.5 * u, 10.5 * u);
			bolt.lineTo(12.5 * u, 10.5 * u);
			bolt.closePath();
			g.setColor(Color.BLACK);
			g.fill(bolt);
			g.setStroke(new BasicStroke((float) Math.max(1, u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(bolt);
		}
		finally {
			g.dispose();
		}
		return image;
	}
}

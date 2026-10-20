package io.grundner.wallbox.desktop;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Desktop-Modus gemäß ADR 0004. */
class DesktopLauncherTest {

	@Test
	void listensOnlyOnLoopback() { // OQ-2 für den Desktop-Modus
		assertEquals("127.0.0.1", DesktopLauncher.properties().get("server.address"));
		assertEquals(DesktopLauncher.PORT, DesktopLauncher.properties().get("server.port"));
		assertEquals("http://127.0.0.1:47123/", DesktopLauncher.APP_URI.toString());
	}

	@Test
	void rendersIconInRequestedSize() {
		BufferedImage icon = AppIcon.render(32);
		assertEquals(32, icon.getWidth());
		assertEquals(32, icon.getHeight());
	}
}

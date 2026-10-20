package io.grundner.wallbox.desktop;

import io.grundner.wallbox.WallboxReportApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Map;

/**
 * Start als Windows-Desktop-App (ADR 0004, docs/operations/windows-app.md):
 * nur auf 127.0.0.1, fester Port, Browser öffnen, Symbol im Infobereich.
 */
public final class DesktopLauncher {

	public static final String ARGUMENT = "--desktop";
	static final int PORT = 47123;
	static final URI APP_URI = URI.create("http://127.0.0.1:" + PORT + "/");

	private DesktopLauncher() {
	}

	public static void main(String[] args) {
		if (!isPortFree()) {
			if (isOurAppRunning()) {
				openBrowser(); // zweiter Start: nur Browser öffnen
				return;
			}
			showError("Port " + PORT + " ist belegt. Bitte das Programm, das ihn nutzt, beenden "
					+ "oder den Rechner neu starten.");
			System.exit(1);
		}

		ConfigurableApplicationContext context = new SpringApplicationBuilder(WallboxReportApplication.class)
				.headless(false)
				.properties(properties())
				.listeners(event -> {
					if (event instanceof ApplicationReadyEvent ready) {
						installTrayIcon(ready.getApplicationContext());
						openBrowser();
					}
				})
				.run(args);
		context.registerShutdownHook();
	}

	/** Desktop-Konfiguration: nur lokal erreichbar (OQ-2), Protokoll in ~/.w5x/logs. */
	static Map<String, Object> properties() {
		return Map.of(
				"server.address", "127.0.0.1",
				"server.port", PORT,
				"logging.file.name", "${user.home}/.w5x/logs/ladesitzungsbericht.log",
				"spring.main.banner-mode", "off");
	}

	static boolean isPortFree() {
		try (ServerSocket socket = new ServerSocket(PORT, 1, InetAddress.getLoopbackAddress())) {
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	private static boolean isOurAppRunning() {
		try {
			HttpURLConnection connection = (HttpURLConnection) APP_URI.resolve("api/settings").toURL().openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			return connection.getResponseCode() == 200;
		}
		catch (IOException e) {
			return false;
		}
	}

	private static void installTrayIcon(ConfigurableApplicationContext context) {
		if (GraphicsEnvironment.isHeadless() || !SystemTray.isSupported()) {
			return;
		}
		PopupMenu menu = new PopupMenu();
		MenuItem open = new MenuItem("Öffnen");
		open.addActionListener(e -> openBrowser());
		MenuItem quit = new MenuItem("Beenden");
		quit.addActionListener(e -> System.exit(org.springframework.boot.SpringApplication.exit(context)));
		menu.add(open);
		menu.addSeparator();
		menu.add(quit);

		TrayIcon icon = new TrayIcon(AppIcon.render(64), "Ladesitzungsbericht", menu);
		icon.setImageAutoSize(true);
		icon.addActionListener(e -> openBrowser()); // Doppelklick
		try {
			SystemTray.getSystemTray().add(icon);
		}
		catch (Exception e) {
			// ohne Symbol weiterlaufen; Bedienung über den Browser bleibt möglich
		}
	}

	private static void openBrowser() {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(APP_URI);
			}
		}
		catch (IOException e) {
			// Browser nicht startbar: Adresse steht in der Betriebsdoku
		}
	}

	private static void showError(String message) {
		if (!GraphicsEnvironment.isHeadless()) {
			JOptionPane.showMessageDialog(null, message, "Ladesitzungsbericht", JOptionPane.ERROR_MESSAGE);
		}
		System.err.println(message);
	}
}

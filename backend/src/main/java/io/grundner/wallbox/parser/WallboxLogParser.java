package io.grundner.wallbox.parser;

import io.grundner.wallbox.domain.ChargingSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parst den Inhalt einer wallbox.log in Ladesitzungen.
 * Spezifikation: docs/domain/log-format.md, Regeln CS-1..CS-3.
 *
 * Felder werden musterbasiert (nicht positionsbasiert) erkannt. Zahlen nutzen den
 * Punkt als Dezimaltrennzeichen. Zeitstempel werden als lokale Zeit interpretiert.
 */
@Component
public class WallboxLogParser {

	private static final Pattern STOP_PATTERN =
			Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}):Fahrzeug getrennt");
	private static final Pattern ENERGY_PATTERN =
			Pattern.compile("Geladene Energie:(\\d+(?:\\.\\d+)?)\\s*kWh");
	private static final Pattern DURATION_PATTERN =
			Pattern.compile("Dauer:(\\d+)\\s*s");
	private static final Pattern USER_PATTERN =
			Pattern.compile("user:([^;]+)");
	private static final Pattern COST_PATTERN =
			Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*€");

	private static final DateTimeFormatter STOP_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Parst den gesamten Log-Text. Jede Zeile mit "Fahrzeug getrennt" ergibt eine
	 * Ladesitzung; Zeilen ohne gültigen Stop-Zeitpunkt werden übersprungen (CS-1).
	 */
	public List<ChargingSession> parse(String logText) {
		List<ChargingSession> sessions = new ArrayList<>();
		if (logText == null || logText.isBlank()) {
			return sessions;
		}
		for (String line : logText.split("\\r?\\n")) {
			parseLine(line).ifPresent(sessions::add);
		}
		return sessions;
	}

	private java.util.Optional<ChargingSession> parseLine(String line) {
		Matcher stopMatcher = STOP_PATTERN.matcher(line);
		if (!stopMatcher.find()) {
			return java.util.Optional.empty(); // CS-1: keine Sitzung ohne Stop-Zeit
		}
		LocalDateTime stop = LocalDateTime.parse(stopMatcher.group(1), STOP_FORMAT);

		long duration = matchLong(DURATION_PATTERN, line);   // CS-3: fehlend -> 0
		double energy = matchDouble(ENERGY_PATTERN, line);
		double cost = matchDouble(COST_PATTERN, line);
		String user = matchString(USER_PATTERN, line);

		LocalDateTime start = stop.minusSeconds(duration);   // CS-2

		return java.util.Optional.of(
				new ChargingSession(start, stop, duration, energy, cost, user, line));
	}

	private static long matchLong(Pattern pattern, String line) {
		Matcher m = pattern.matcher(line);
		return m.find() ? Long.parseLong(m.group(1)) : 0L;
	}

	private static double matchDouble(Pattern pattern, String line) {
		Matcher m = pattern.matcher(line);
		return m.find() ? Double.parseDouble(m.group(1)) : 0.0d;
	}

	private static String matchString(Pattern pattern, String line) {
		Matcher m = pattern.matcher(line);
		return m.find() ? m.group(1).trim() : "";
	}
}

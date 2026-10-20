package io.grundner.wallbox.report;

import io.grundner.wallbox.domain.ChargingSession;
import io.grundner.wallbox.domain.MonthlyReport;
import io.grundner.wallbox.loxone.LoxoneClient;
import io.grundner.wallbox.parser.WallboxLogParser;
import io.grundner.wallbox.settings.Settings;
import io.grundner.wallbox.settings.SettingsStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Erzeugt Monatsberichte (stateless, ADR 0001).
 * Regeln MR-1..MR-4 aus docs/domain/charging-session.md, ST-2 aus docs/features/settings.md.
 */
@Service
public class ReportService {

	private final SettingsStore settingsStore;
	private final LoxoneClient loxoneClient;
	private final WallboxLogParser parser;

	public ReportService(SettingsStore settingsStore, LoxoneClient loxoneClient, WallboxLogParser parser) {
		this.settingsStore = settingsStore;
		this.loxoneClient = loxoneClient;
		this.parser = parser;
	}

	public MonthlyReport buildReport(int year, int month) {
		Settings settings = settingsStore.load();
		if (!settings.complete()) {
			throw new SettingsIncompleteException(); // ST-2: kein Abruf ohne vollständige Einstellungen
		}
		String log = loxoneClient.fetchLog(
				LoxoneClient.logUrl(settings.serialNumber()), settings.username(), settings.password());
		return buildReport(settings, parser.parse(log), year, month);
	}

	/** Aggregiert bereits geparste Sitzungen zum Monatsbericht. */
	public MonthlyReport buildReport(Settings settings, List<ChargingSession> all, int year, int month) {
		// MR-1: nur Sitzungen dieses Kalendermonats; MR-2: aufsteigend nach start
		List<ChargingSession> sessions = all.stream()
				.filter(s -> s.stop().getYear() == year && s.stop().getMonthValue() == month)
				.sorted(Comparator.comparing(ChargingSession::start))
				.toList();

		// MR-3: Summen nur über die enthaltenen Sitzungen
		double totalEnergy = sessions.stream().mapToDouble(ChargingSession::energyKwh).sum();
		long totalDuration = sessions.stream().mapToLong(ChargingSession::durationSeconds).sum();
		double totalCost = sessions.stream().mapToDouble(ChargingSession::costEur).sum();

		return new MonthlyReport(
				settings.locationLabel(),
				settings.wallboxId(),
				year,
				month,
				LocalDateTime.now(),
				sessions,
				totalEnergy,
				totalDuration,
				totalCost);
	}

	/** Einstellungen unvollständig (führt zu HTTP 409, ST-2). */
	public static class SettingsIncompleteException extends RuntimeException {
		public SettingsIncompleteException() {
			super("Einstellungen unvollständig: Seriennummer, Benutzer und Kennwort müssen gesetzt sein");
		}
	}
}

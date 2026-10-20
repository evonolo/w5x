package io.grundner.wallbox.report;

import io.grundner.wallbox.domain.ChargingSession;
import io.grundner.wallbox.domain.MonthlyReport;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Präsentationsmodell des Berichts mit bereits formatierten Feldern.
 * Formatierung gemäß docs/features/monthly-report.md (deutsche Lokalisierung).
 * Wird sowohl von der JSON-API als auch von der Thymeleaf-Vorlage genutzt.
 */
public record ReportView(
		String location,
		String wallbox,
		int year,
		int month,
		String generatedDate,
		String generatedTime,
		String totalEnergyKwh,
		String totalDurationFormatted,
		int sessionCount,
		List<SessionView> sessions) {

	public record SessionView(
			String energyKwh,
			String durationFormatted,
			String start,
			String stop,
			String user) {
	}

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

	public static ReportView of(MonthlyReport report) {
		List<SessionView> sessionViews = report.sessions().stream()
				.map(ReportView::toSessionView)
				.toList();

		return new ReportView(
				report.locationLabel(),
				report.wallboxId(),
				report.year(),
				report.month(),
				report.generatedAt().format(DATE),
				report.generatedAt().format(TIME),
				formatEnergy(report.totalEnergyKwh()),
				formatDuration(report.totalDurationSeconds()),
				report.sessionCount(),
				sessionViews);
	}

	private static SessionView toSessionView(ChargingSession s) {
		return new SessionView(
				formatEnergy(s.energyKwh()),
				formatDuration(s.durationSeconds()),
				s.start().format(DATETIME),
				s.stop().format(DATETIME),
				s.user());
	}

	static String formatEnergy(double kwh) {
		return String.format(Locale.ROOT, "%.2f", kwh);
	}

	/**
	 * "HH Stunden, mm Minuten, ss Sekunden" – Stunden nicht auf 24 begrenzt,
	 * zweistellig aufgefüllt (docs/features/monthly-report.md).
	 */
	static String formatDuration(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format(Locale.ROOT, "%02d Stunden, %02d Minuten, %02d Sekunden",
				hours, minutes, seconds);
	}
}

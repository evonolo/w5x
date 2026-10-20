package io.grundner.wallbox.report;

import io.grundner.wallbox.domain.ChargingSession;
import io.grundner.wallbox.domain.MonthlyReport;

import java.time.LocalDateTime;
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

	/**
	 * Eine Sitzungszeile. Neben den exakten Werten enthält sie die Kurzformen der
	 * App-Darstellung (docs/features/monthly-report.md, „Sitzungszeilen in der App“).
	 */
	public record SessionView(
			String energyKwh,
			String durationFormatted,
			String start,
			String stop,
			String user,
			String startWeekday,
			String startDay,
			String startTime,
			String stopTime,
			String stopDayLabel,
			String durationShort,
			double energyShare) {
	}

	private static final String[] WEEKDAYS = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd.MM.");

	public static ReportView of(MonthlyReport report) {
		// SR-2: Bezug ist die energiereichste Sitzung dieses Berichts
		double maxEnergy = report.sessions().stream()
				.mapToDouble(ChargingSession::energyKwh)
				.max()
				.orElse(0);
		List<SessionView> sessionViews = report.sessions().stream()
				.map(s -> toSessionView(s, maxEnergy))
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

	private static SessionView toSessionView(ChargingSession s, double maxEnergy) {
		return new SessionView(
				formatEnergy(s.energyKwh()),
				formatDuration(s.durationSeconds()),
				s.start().format(DATETIME),
				s.stop().format(DATETIME),
				s.user(),
				weekday(s.start()),
				s.start().format(DAY),
				s.start().format(TIME),
				s.stop().format(TIME),
				stopDayLabel(s.start(), s.stop()),
				formatDurationShort(s.durationSeconds()),
				maxEnergy > 0 ? s.energyKwh() / maxEnergy : 0);
	}

	static String weekday(LocalDateTime dateTime) {
		return WEEKDAYS[dateTime.getDayOfWeek().getValue() - 1];
	}

	static String stopDayLabel(LocalDateTime start, LocalDateTime stop) {
		return start.toLocalDate().equals(stop.toLocalDate())
				? "gleicher Tag"
				: weekday(stop) + " " + stop.format(DAY);
	}

	/** "H h mm min", Sekunden abgeschnitten (SR-1). */
	static String formatDurationShort(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		return String.format(Locale.ROOT, "%d h %02d min", hours, minutes);
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

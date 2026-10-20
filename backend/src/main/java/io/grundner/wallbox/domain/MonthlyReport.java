package io.grundner.wallbox.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Aggregat aller Ladesitzungen eines Standorts in einem Kalendermonat.
 * Siehe docs/domain/charging-session.md (Regeln MR-1..MR-4).
 */
public record MonthlyReport(
		String locationLabel,
		String wallboxId,
		int year,
		int month,
		LocalDateTime generatedAt,
		List<ChargingSession> sessions,
		double totalEnergyKwh,
		long totalDurationSeconds,
		double totalCostEur) {

	public int sessionCount() {
		return sessions.size();
	}
}

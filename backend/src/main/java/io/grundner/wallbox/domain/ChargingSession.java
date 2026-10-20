package io.grundner.wallbox.domain;

import java.time.LocalDateTime;

/**
 * Eine abgeschlossene Ladesitzung, abgeleitet aus einer Log-Zeile.
 * Siehe docs/domain/charging-session.md (Regeln CS-1..CS-3).
 *
 * @param start           abgeleitet: {@code stop - durationSeconds} (CS-2)
 * @param stop            Zeitpunkt des Ereignisses "Fahrzeug getrennt"
 * @param durationSeconds Ladedauer in Sekunden
 * @param energyKwh       geladene Energie in kWh
 * @param costEur         Kosten in Euro (geparst, aber nicht ausgewiesen – MR-4)
 * @param user            Nutzerkennung aus dem Log
 * @param rawLine         ursprüngliche Log-Zeile (Diagnose)
 */
public record ChargingSession(
		LocalDateTime start,
		LocalDateTime stop,
		long durationSeconds,
		double energyKwh,
		double costEur,
		String user,
		String rawLine) {
}

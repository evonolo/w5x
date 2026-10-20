package io.grundner.wallbox.report;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportViewTest {

	@Test
	void formatsDurationWithoutCappingHours() { // AC-3
		assertEquals("05 Stunden, 00 Minuten, 00 Sekunden", ReportView.formatDuration(18000));
		assertEquals("08 Stunden, 03 Minuten, 20 Sekunden", ReportView.formatDuration(29000));
		// Gesamtdauer > 24 h wird nicht begrenzt
		assertEquals("27 Stunden, 46 Minuten, 40 Sekunden", ReportView.formatDuration(100000));
	}

	@Test
	void formatsEnergyWithTwoDecimalsAndDot() { // AC-2
		assertEquals("90.31", ReportView.formatEnergy(90.31));
		assertEquals("45.50", ReportView.formatEnergy(45.5));
	}

	@Test
	void shortDurationCutsSeconds() { // SR-1
		assertEquals("11 h 17 min", ReportView.formatDurationShort(11 * 3600 + 17 * 60 + 59));
		assertEquals("0 h 50 min", ReportView.formatDurationShort(50 * 60 + 1));
		assertEquals("108 h 39 min", ReportView.formatDurationShort(108 * 3600 + 39 * 60));
	}

	@Test
	void weekdayAndStopDayLabel() {
		LocalDateTime start = LocalDateTime.of(2026, 9, 30, 19, 6, 43);   // Mittwoch
		LocalDateTime stop = LocalDateTime.of(2026, 10, 1, 6, 24, 4);     // Donnerstag
		assertEquals("Mi", ReportView.weekday(start));
		assertEquals("Do 01.10.", ReportView.stopDayLabel(start, stop));
		assertEquals("gleicher Tag", ReportView.stopDayLabel(start, start.plusMinutes(50)));
	}
}

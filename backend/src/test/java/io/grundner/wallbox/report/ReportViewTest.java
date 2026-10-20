package io.grundner.wallbox.report;

import org.junit.jupiter.api.Test;

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
}

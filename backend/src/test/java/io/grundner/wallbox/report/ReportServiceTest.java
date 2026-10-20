package io.grundner.wallbox.report;

import io.grundner.wallbox.TestLogs;
import io.grundner.wallbox.domain.ChargingSession;
import io.grundner.wallbox.domain.MonthlyReport;
import io.grundner.wallbox.loxone.LoxoneClient;
import io.grundner.wallbox.parser.WallboxLogParser;
import io.grundner.wallbox.settings.Settings;
import io.grundner.wallbox.settings.SettingsProperties;
import io.grundner.wallbox.settings.SettingsStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {

	private static final Settings SETTINGS =
			new Settings("504F94A22F32", "user", "secret", "Teststandort", "A73B4D3");

	private final List<ChargingSession> sample = new WallboxLogParser().parse(TestLogs.sample());

	@TempDir
	Path tempDir;

	private ReportService service(LoxoneClient client) {
		SettingsStore store = new SettingsStore(
				new SettingsProperties(tempDir.resolve("settings.json")), JsonMapper.builder().build());
		return new ReportService(store, client, new WallboxLogParser());
	}

	@Test
	void filtersToRequestedMonthAndAggregates() { // MR-1, MR-3, AC-1, AC-2, AC-3, HP-2
		MonthlyReport report = service(new LoxoneClient()).buildReport(SETTINGS, sample, 2024, 2);

		assertEquals(2, report.sessionCount());
		assertEquals(90.31, report.totalEnergyKwh(), 1e-9);   // 66.99 + 23.32
		assertEquals(29000, report.totalDurationSeconds());    // 18000 + 11000
	}

	@Test
	void sortsSessionsByStartAscending() { // MR-2
		MonthlyReport report = service(new LoxoneClient()).buildReport(SETTINGS, sample, 2024, 2);
		var sessions = report.sessions();
		assertTrue(sessions.get(0).start().isBefore(sessions.get(1).start()));
	}

	@Test
	void headerComesFromSettings() {
		MonthlyReport report = service(new LoxoneClient()).buildReport(SETTINGS, sample, 2024, 2);
		assertEquals("Teststandort", report.locationLabel());
		assertEquals("A73B4D3", report.wallboxId());
	}

	@Test
	void incompleteSettingsFailWithoutFetching() { // ST-2, AC-7, AC-S5
		LoxoneClient mustNotBeCalled = new LoxoneClient() {
			@Override
			public String fetchLog(URI url, String username, String password) {
				throw new AssertionError("Kein Abruf bei unvollständigen Einstellungen erwartet");
			}
		};
		assertThrows(ReportService.SettingsIncompleteException.class,
				() -> service(mustNotBeCalled).buildReport(2024, 2));
	}
}

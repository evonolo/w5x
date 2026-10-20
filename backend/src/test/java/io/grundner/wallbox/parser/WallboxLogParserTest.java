package io.grundner.wallbox.parser;

import io.grundner.wallbox.domain.ChargingSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WallboxLogParserTest {

	private final WallboxLogParser parser = new WallboxLogParser();

	private static final String LOG = """
			1970-01-01 00:00:00;Wallbox Log;System gestartet
			1970-01-01 00:00:00;Wallbox Log;2024-01-31 23:55:55:Fahrzeug getrennt;user:-;Geladene Energie:45.54kWh;Dauer:12000 s;54.45€
			1970-01-01 00:00:00;Wallbox Log;2024-02-01 05:20:53:Fahrzeug getrennt;user:-;Geladene Energie:66.99kWh;Dauer:18000 s;99.66€
			""";

	@Test
	void skipsLinesWithoutDisconnectEvent() { // CS-1 / AC-4
		List<ChargingSession> sessions = parser.parse(LOG);
		assertEquals(2, sessions.size());
	}

	@Test
	void parsesFieldsByPattern() {
		ChargingSession s = parser.parse(LOG).get(0);
		assertEquals(LocalDateTime.of(2024, 1, 31, 23, 55, 55), s.stop());
		assertEquals(12000, s.durationSeconds());
		assertEquals(45.54, s.energyKwh(), 1e-9);
		assertEquals(54.45, s.costEur(), 1e-9);
		assertEquals("-", s.user());
	}

	@Test
	void derivesStartFromStopMinusDuration() { // CS-2
		ChargingSession s = parser.parse(LOG).get(1); // 18000 s = 5 h
		assertEquals(LocalDateTime.of(2024, 2, 1, 0, 20, 53), s.start());
	}

	@Test
	void emptyInputYieldsNoSessions() {
		assertTrue(parser.parse("").isEmpty());
		assertTrue(parser.parse(null).isEmpty());
	}
}

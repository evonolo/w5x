package io.grundner.wallbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/** Zugriff auf die Beispieldatei der Testdaten (docs/domain/log-format.md, „Beispiel-Daten“). */
public final class TestLogs {

	private TestLogs() {
	}

	public static String sample() {
		try (InputStream in = TestLogs.class.getResourceAsStream("/samples/wallbox-sample.log")) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

package io.grundner.wallbox.loxone;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abruf der Log-Datei per HTTP (docs/domain/log-format.md, „Abruf der Log-Datei“; AC-6).
 * Ein lokaler Server bildet die Loxone-Cloud-DNS-Weiterleitung nach.
 */
class LoxoneClientTest {

	private static final String LOG =
			"1970-01-01 00:00:00;Wallbox Log;2026-10-05 07:00:00:Fahrzeug getrennt;user:-;"
					+ "Geladene Energie:12.50kWh;Dauer:3600 s;3.75€\n";
	private static final String EXPECTED_AUTH = "Basic "
			+ Base64.getEncoder().encodeToString("user:secret".getBytes(StandardCharsets.UTF_8));

	private final LoxoneClient client = new LoxoneClient();
	private final List<String> authHeadersAtLog = Collections.synchronizedList(new ArrayList<>());
	private HttpServer server;
	private String base;

	@BeforeEach
	void startServer() throws IOException {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/cloud/wallbox.log", ex -> {
			ex.getResponseHeaders().add("Location", base + "/miniserver/wallbox.log");
			respond(ex, 307, "Temporary Redirect. Redirecting to " + base + "/miniserver/wallbox.log");
		});
		server.createContext("/miniserver/wallbox.log", ex -> {
			String auth = ex.getRequestHeaders().getFirst("Authorization");
			authHeadersAtLog.add(auth == null ? "<none>" : auth);
			if (!EXPECTED_AUTH.equals(auth)) {
				respond(ex, 401, "Unauthorized");
				return;
			}
			ex.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
			respond(ex, 200, LOG);
		});
		server.createContext("/loop", ex -> {
			ex.getResponseHeaders().add("Location", "/loop");
			respond(ex, 302, "");
		});
		server.createContext("/no-target", ex -> respond(ex, 307, "Temporary Redirect"));
		server.start();
		base = "http://localhost:" + server.getAddress().getPort();
	}

	@AfterEach
	void stopServer() {
		server.stop(0);
	}

	@Test
	void followsRedirectAndSendsCredentialsToTarget() {
		String log = fetch("/cloud/wallbox.log", "user", "secret");

		assertEquals(LOG, log);
		assertEquals(List.of(EXPECTED_AUTH), authHeadersAtLog);
	}

	@Test
	void rejectedCredentialsAfterRedirectAreAFetchError() { // AC-6
		var e = assertThrows(LoxoneClient.LogFetchException.class,
				() -> fetch("/cloud/wallbox.log", "user", "wrong"));
		assertTrue(e.getMessage().contains("401"), e.getMessage());
	}

	@Test
	void redirectWithoutTargetIsAFetchError() { // AC-6: nie als leerer Bericht
		assertThrows(LoxoneClient.LogFetchException.class,
				() -> fetch("/no-target", "user", "secret"));
	}

	@Test
	void tooManyRedirectsIsAFetchError() {
		var e = assertThrows(LoxoneClient.LogFetchException.class,
				() -> fetch("/loop", "user", "secret"));
		assertTrue(e.getMessage().contains("Zu viele Weiterleitungen"), e.getMessage());
	}

	@Test
	void unreachableHostIsAFetchError() {
		server.stop(0);
		assertThrows(LoxoneClient.LogFetchException.class,
				() -> fetch("/cloud/wallbox.log", "user", "secret"));
	}

	@Test
	void redirectFromHttpsToHttpIsRejected() {
		var e = assertThrows(LoxoneClient.LogFetchException.class,
				() -> LoxoneClient.redirectTarget(URI.create("https://dns.example/log"),
						URI.create("http://miniserver.example/log")));
		assertTrue(e.getMessage().contains("abgelehnt"), e.getMessage());
	}

	@Test
	void relativeRedirectIsResolvedAgainstCurrentUri() {
		URI target = LoxoneClient.redirectTarget(URI.create("https://dns.example/a/log"),
				URI.create("/b/log"));
		assertEquals(URI.create("https://dns.example/b/log"), target);
	}

	private String fetch(String path, String username, String password) {
		return client.fetchLog(URI.create(base + path), username, password);
	}

	@Test
	void logUrlIsBuiltFromSerialNumber() {
		assertEquals(URI.create("https://dns.loxonecloud.com/504F94A22F32/dev/fsget/log/wallbox.log"),
				LoxoneClient.logUrl("504F94A22F32"));
	}

	private static void respond(com.sun.net.httpserver.HttpExchange ex, int status, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
		if (bytes.length > 0) {
			ex.getResponseBody().write(bytes);
		}
		ex.close();
	}
}

package io.grundner.wallbox.loxone;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Holt den Inhalt der wallbox.log vom Miniserver.
 *
 * Stateless (ADR 0001): bei jedem Aufruf frischer Abruf per HTTP GET mit Basic Auth
 * (docs/domain/log-format.md, „Abruf der Log-Datei“). Weiterleitungen werden selbst
 * verfolgt, damit die Zugangsdaten auch beim Ziel-Host ankommen; jede Antwort außer
 * 2xx ist ein Abruffehler (AC-6).
 */
@Component
public class LoxoneClient {

	static final int MAX_REDIRECTS = 5;

	private final RestClient restClient = RestClient.builder()
			.requestFactory(new JdkClientHttpRequestFactory(HttpClient.newBuilder()
					.followRedirects(HttpClient.Redirect.NEVER)
					.build()))
			.build();

	/** Adresse der wallbox.log über Loxone-Cloud-DNS (docs/domain/log-format.md). */
	public static URI logUrl(String serialNumber) {
		return URI.create("https://dns.loxonecloud.com/" + serialNumber + "/dev/fsget/log/wallbox.log");
	}

	public String fetchLog(URI url, String username, String password) {
		String authorization = basicAuth(username, password);
		try {
			URI uri = url;
			for (int redirects = 0; redirects <= MAX_REDIRECTS; redirects++) {
				FetchResult result = fetchOnce(uri, authorization);
				if (result.redirectTo() == null) {
					return result.body();
				}
				uri = redirectTarget(uri, result.redirectTo());
			}
			throw new LogFetchException("Zu viele Weiterleitungen beim Abruf der wallbox.log");
		}
		catch (LogFetchException e) {
			throw e;
		}
		catch (Exception e) {
			throw new LogFetchException("Abruf der wallbox.log fehlgeschlagen", e);
		}
	}

	private FetchResult fetchOnce(URI uri, String authorization) {
		return restClient.get().uri(uri)
				.header(HttpHeaders.AUTHORIZATION, authorization)
				.exchange((request, response) -> {
					HttpStatusCode status = response.getStatusCode();
					if (status.is3xxRedirection()) {
						URI target = response.getHeaders().getLocation();
						if (target == null) {
							throw new LogFetchException("Weiterleitung ohne Ziel (HTTP " + status.value() + ")");
						}
						return new FetchResult(null, target);
					}
					if (!status.is2xxSuccessful()) {
						throw new LogFetchException("Abruf der wallbox.log fehlgeschlagen: HTTP " + status.value());
					}
					Charset charset = charsetOf(response.getHeaders().getContentType());
					return new FetchResult(new String(response.getBody().readAllBytes(), charset), null);
				});
	}

	static URI redirectTarget(URI current, URI location) {
		URI target = current.resolve(location);
		// Zugangsdaten nie unverschlüsselt weitergeben
		if ("https".equalsIgnoreCase(current.getScheme()) && !"https".equalsIgnoreCase(target.getScheme())) {
			throw new LogFetchException("Weiterleitung von HTTPS auf " + target.getScheme() + " abgelehnt");
		}
		return target;
	}

	private static Charset charsetOf(MediaType contentType) {
		return contentType != null && contentType.getCharset() != null
				? contentType.getCharset()
				: StandardCharsets.UTF_8;
	}

	private static String basicAuth(String username, String password) {
		String raw = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
	}

	/** Ergebnis eines einzelnen Requests: entweder Log-Inhalt oder Weiterleitungsziel. */
	private record FetchResult(String body, URI redirectTo) {
	}

	/** Fehler beim Beschaffen der Log-Datei (führt zu HTTP 502, AC-6). */
	public static class LogFetchException extends RuntimeException {
		public LogFetchException(String message, Throwable cause) {
			super(message, cause);
		}

		public LogFetchException(String message) {
			super(message);
		}
	}
}

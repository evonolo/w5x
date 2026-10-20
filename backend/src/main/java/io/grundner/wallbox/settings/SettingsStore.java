package io.grundner.wallbox.settings;

import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.regex.Pattern;

/**
 * Liest und schreibt die Einstellungen als JSON-Datei (ADR 0003, ST-1..ST-7).
 * Verzeichnis und Datei werden – wo unterstützt – nur für den Eigentümer zugänglich
 * angelegt, da die Datei das Kennwort im Klartext enthält.
 */
@Component
public class SettingsStore {

	private static final Pattern SERIAL_NUMBER = Pattern.compile("[A-Za-z0-9]*");

	private final Path file;
	private final JsonMapper jsonMapper;

	public SettingsStore(SettingsProperties properties, JsonMapper jsonMapper) {
		this.file = properties.settingsFile();
		this.jsonMapper = jsonMapper;
	}

	/** Gespeicherte Einstellungen; {@link Settings#EMPTY}, solange noch nichts gespeichert wurde. */
	public synchronized Settings load() {
		if (!Files.exists(file)) {
			return Settings.EMPTY;
		}
		try {
			return jsonMapper.readValue(Files.readString(file), Settings.class);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Einstellungsdatei nicht lesbar: " + file, e);
		}
		catch (JacksonException e) {
			throw new IllegalStateException("Einstellungsdatei ungültig: " + file, e);
		}
	}

	/**
	 * Übernimmt die Einstellungen. Ein leeres Kennwort behält das gespeicherte bei (ST-5).
	 *
	 * @throws IllegalArgumentException bei ungültiger Seriennummer (ST-3)
	 */
	public synchronized Settings update(Settings incoming) {
		if (!SERIAL_NUMBER.matcher(incoming.serialNumber()).matches()) {
			throw new IllegalArgumentException("Die Seriennummer darf nur Buchstaben und Ziffern enthalten");
		}
		Settings settings = incoming.password().isEmpty()
				? incoming.withPassword(load().password())
				: incoming;
		write(settings);
		return settings;
	}

	private void write(Settings settings) {
		try {
			Path dir = file.toAbsolutePath().getParent();
			Files.createDirectories(dir);
			restrict(dir, "rwx------");
			// erst Rechte setzen, dann Inhalt schreiben, dann atomar ersetzen
			Path tmp = Files.createTempFile(dir, "settings", ".tmp");
			restrict(tmp, "rw-------");
			Files.writeString(tmp, jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings));
			Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Einstellungsdatei nicht schreibbar: " + file, e);
		}
	}

	private static void restrict(Path path, String permissions) throws IOException {
		try {
			Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(permissions));
		}
		catch (UnsupportedOperationException e) {
			// Dateisystem ohne POSIX-Rechte (z. B. Windows): Standardrechte bleiben
		}
	}
}

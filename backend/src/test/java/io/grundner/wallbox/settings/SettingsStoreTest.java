package io.grundner.wallbox.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Regeln ST-1..ST-7 aus docs/features/settings.md. */
class SettingsStoreTest {

	@TempDir
	Path tempDir;

	private SettingsStore store(Path file) {
		return new SettingsStore(new SettingsProperties(file), JsonMapper.builder().build());
	}

	private Path file() {
		return tempDir.resolve("w5x/settings.json");
	}

	@Test
	void emptyWhenNothingSaved() {
		Settings settings = store(file()).load();
		assertEquals(Settings.EMPTY, settings);
		assertFalse(settings.complete());
	}

	@Test
	void survivesNewStoreInstance() { // ST-7, AC-S1
		Settings saved = new Settings("504F94A22F32", "admin", "geheim", "Neuhausen 22", "50:4F");
		store(file()).update(saved);

		assertEquals(saved, store(file()).load());
	}

	@Test
	void emptyPasswordKeepsStoredPassword() { // ST-5, AC-S3
		SettingsStore store = store(file());
		store.update(new Settings("504F94A22F32", "admin", "geheim", "", ""));
		store.update(new Settings("504F94A22F32", "admin2", "", "Neu", ""));

		Settings loaded = store.load();
		assertEquals("geheim", loaded.password());
		assertEquals("admin2", loaded.username());
		assertEquals("Neu", loaded.locationLabel());
	}

	@Test
	void invalidSerialNumberIsRejectedAndNotSaved() { // ST-3, AC-S4
		SettingsStore store = store(file());
		assertThrows(IllegalArgumentException.class,
				() -> store.update(new Settings("504F/../x", "admin", "geheim", "", "")));
		assertFalse(Files.exists(file()));
	}

	@Test
	void incompleteSettingsCanBeSaved() { // ST-6, ST-1
		SettingsStore store = store(file());
		store.update(new Settings("504F94A22F32", "", "", "", ""));
		assertEquals("504F94A22F32", store.load().serialNumber());
		assertFalse(store.load().complete());
	}

	@Test
	void trimsAllFieldsExceptPassword() { // ST-3
		Settings settings = new Settings(" 504F94A22F32 ", " admin ", " geheim ", " Ort ", " W ");
		assertEquals("504F94A22F32", settings.serialNumber());
		assertEquals("admin", settings.username());
		assertEquals(" geheim ", settings.password());
		assertTrue(settings.complete());
	}

	@Test
	void ignoresUnknownFieldsFromOlderVersions() throws Exception { // z. B. entferntes Feld "person"
		Files.createDirectories(file().getParent());
		Files.writeString(file(), """
				{"serialNumber":"504F94A22F32","username":"admin","password":"geheim",
				 "locationLabel":"Ort","person":"Alt","wallboxId":"W"}""");

		assertEquals(new Settings("504F94A22F32", "admin", "geheim", "Ort", "W"), store(file()).load());
	}

	@Test
	void fileIsOnlyAccessibleByOwner() throws Exception { // ADR 0003
		// Unter Windows gibt es keine POSIX-Rechte; dort schützt das Benutzerprofil (ADR 0004)
		assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
		store(file()).update(new Settings("504F94A22F32", "admin", "geheim", "", ""));
		assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file())));
		assertEquals("rwx------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file().getParent())));
	}
}

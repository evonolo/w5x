package io.grundner.wallbox.web;

import io.grundner.wallbox.settings.Settings;
import io.grundner.wallbox.settings.SettingsStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Einstellungs-API – Contract: docs/api/report-api.md, Regeln: docs/features/settings.md.
 */
@RestController
@RequestMapping("/api/settings")
public class SettingsController {

	private final SettingsStore settingsStore;

	public SettingsController(SettingsStore settingsStore) {
		this.settingsStore = settingsStore;
	}

	@GetMapping
	public SettingsView get() {
		return SettingsView.of(settingsStore.load());
	}

	@PutMapping
	public SettingsView put(@RequestBody Settings settings) {
		return SettingsView.of(settingsStore.update(settings));
	}

	/** Einstellungen ohne Kennwort (ST-4). */
	public record SettingsView(
			String serialNumber,
			String username,
			boolean passwordSet,
			String locationLabel,
			String wallboxId,
			boolean complete) {

		static SettingsView of(Settings s) {
			return new SettingsView(s.serialNumber(), s.username(), !s.password().isEmpty(),
					s.locationLabel(), s.wallboxId(), s.complete());
		}
	}
}

package io.grundner.wallbox.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * Ablageort der Einstellungsdatei (ADR 0003), Standard: ~/.w5x/settings.json.
 */
@ConfigurationProperties(prefix = "w5x")
public record SettingsProperties(Path settingsFile) {
}

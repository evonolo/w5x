package io.grundner.wallbox.settings;

/**
 * Einstellungen der einen Wallbox-Installation – docs/features/settings.md.
 * Nicht gesetzte Felder sind leer (nie {@code null}); alle Felder außer dem Kennwort
 * werden getrimmt (ST-3).
 */
public record Settings(
		String serialNumber,
		String username,
		String password,
		String locationLabel,
		String wallboxId) {

	public static final Settings EMPTY = new Settings("", "", "", "", "");

	public Settings {
		serialNumber = trim(serialNumber);
		username = trim(username);
		password = password == null ? "" : password;
		locationLabel = trim(locationLabel);
		wallboxId = trim(wallboxId);
	}

	/** ST-1: Seriennummer, Benutzer und Kennwort sind gesetzt. */
	public boolean complete() {
		return !serialNumber.isEmpty() && !username.isEmpty() && !password.isEmpty();
	}

	public Settings withPassword(String newPassword) {
		return new Settings(serialNumber, username, newPassword, locationLabel, wallboxId);
	}

	private static String trim(String value) {
		return value == null ? "" : value.strip();
	}
}

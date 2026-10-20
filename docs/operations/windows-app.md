# Betrieb: Windows-Desktop-App

Auslieferung gemäß [ADR 0004](../decisions/0004-windows-desktop-app.md).

## Installation

- Bezug: GitHub-Release der jeweiligen Version (Repository `evonolo/w5x`, Abschnitt
  „Releases“).
- Datei `Ladesitzungsbericht-<Version>-x64-setup.exe` (Windows auf Intel/AMD) bzw.
  `…-arm64-setup.exe` (Windows auf ARM, z. B. Snapdragon) ausführen.
- Beim ersten Start meldet Windows ggf. „Der Computer wurde durch Windows
  geschützt": **Weitere Informationen → Trotzdem ausführen**.
- Installation pro Benutzer nach `%LOCALAPPDATA%\Programs\Ladesitzungsbericht`,
  ohne Administratorrechte. Startmenü-Eintrag; Desktop-Verknüpfung optional.

## Bedienung

- Start über Startmenü oder Desktop-Verknüpfung. Der Browser öffnet sich mit der
  App unter `http://127.0.0.1:47123/`.
- Im Infobereich (neben der Uhr) erscheint ein grünes Symbol:
  - Doppelklick oder **Öffnen**: App im Browser öffnen.
  - **Beenden**: App vollständig beenden.
- Das Schließen des Browsers beendet die App nicht.
- Ein zweiter Start, während die App läuft, öffnet nur den Browser.

## Daten

| Was | Wo |
|-----|----|
| Einstellungen (inkl. Kennwort) | `%USERPROFILE%\.w5x\settings.json` |
| Protokoll | `%USERPROFILE%\.w5x\logs\ladesitzungsbericht.log` |

## Update und Deinstallation

- **Update:** neuen Installer ausführen; eine laufende App wird dabei beendet.
  Einstellungen bleiben erhalten.
- **Deinstallation:** Windows-Einstellungen → Apps. Der Ordner `.w5x` mit den
  Einstellungen bleibt bestehen und kann bei Bedarf von Hand gelöscht werden.

## Fehlerbilder

| Symptom | Ursache / Abhilfe |
|---------|-------------------|
| Meldung „Port 47123 ist belegt" | Ein anderes Programm nutzt den Port. Programm beenden oder Rechner neu starten. |
| Kein Symbol im Infobereich | Ausgeblendete Symbole über den Pfeil „^" neben der Uhr anzeigen. |
| Bericht meldet „HTTP 401" | Benutzer/Kennwort in den Einstellungen prüfen. |

## Bauen

Siehe `.claude/project/build-and-test.md`, Abschnitt „Windows-Installer".

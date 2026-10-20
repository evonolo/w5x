# ADR 0004: Auslieferung als Windows-Desktop-App

- Status: akzeptiert
- Datum: 2026-10-20

## Kontext

Der Nutzer der App (Geri) betreibt sie auf seinem eigenen Windows-Notebook
(Surface, x64) und kann dort Programme (EXE) installieren und ausführen. Er soll
weder Java noch Node noch eine Kommandozeile benötigen. Bisher lief die App nur
als Entwicklungsaufbau (Vite-Dev-Server + Spring Boot).

Betrachtete Optionen:

1. **Spring-Boot-App mit eingebettetem Frontend + mitgelieferter Java-Laufzeit
   (`jlink`) + Windows-Launcher (`jpackage`) + Installer.**
2. Electron/Tauri-Hülle um Frontend und Backend.
3. Native Kompilierung mit GraalVM.

## Entscheidung

Option 1:

- Das gebaute React-Frontend wird als statische Ressource in die Spring-Boot-App
  aufgenommen; App und API laufen in **einem** Prozess auf **einem** Port.
- `jlink` erzeugt eine schlanke Java-21-Laufzeit, `jpackage` daraus ein
  App-Image mit `Ladesitzungsbericht.exe`.
- **Inno Setup** verpackt das App-Image zu einem **Installer pro Benutzer**
  (keine Administratorrechte, Startmenü, optional Desktop-Verknüpfung,
  Deinstallation).
- Im Desktop-Modus lauscht das Backend **nur auf `127.0.0.1`** (fester Port
  47123), öffnet beim Start den Standardbrowser und zeigt ein Symbol im
  Infobereich mit „Öffnen" und „Beenden".
- Gebaut wird über **GitHub Actions** auf Windows-Runnern, da `jpackage` nicht
  plattformübergreifend baut: **x64** immer, **ARM64** zusätzlich als
  Best-Effort (eigener Runner, Fehlschlag blockiert x64 nicht).
- Die EXE wird **nicht signiert**.

## Begründung

- Die App bleibt im Kern unverändert; nur Verpackung und Startlogik kommen hinzu.
- Keine zweite Laufzeit (gegen Option 2), kein Risiko mit Reflection/AWT der
  PDF-Bibliothek in einem nativen Image (gegen Option 3).
- Inno Setup statt WiX: Installer pro Benutzer ohne Adminrechte, deutsche
  Oberfläche, auf GitHub-Windows-Runnern verfügbar.
- Signatur kostet laufend Geld; für einen einzelnen Nutzer genügt einmaliges
  Bestätigen der SmartScreen-Warnung.

## Konsequenzen

- Für den Desktop-Modus ist [OQ-2](../open-questions.md#oq-2-erreichbarkeit-des-backends-im-netzwerk)
  beantwortet: nur lokal erreichbar.
- Unter Windows greift die Beschränkung der Dateirechte aus
  [ADR 0003](0003-local-settings-file.md) nicht; die Einstellungsdatei ist durch
  das Benutzerprofil geschützt.
- Beim ersten Start erscheint die SmartScreen-Warnung („Weitere Informationen →
  Trotzdem ausführen").
- Updates: neuen Installer über die bestehende Installation ausführen;
  Einstellungen bleiben erhalten.
- Ein Windows-Installer entsteht nur über die CI-Pipeline, nicht auf macOS/Linux.
  Lokal lässt sich das App-Image der jeweiligen Plattform zum Testen bauen.
- Auf Windows-ARM läuft notfalls auch der x64-Build (Emulation).

# Build & Test

## Backend

Ausführung aus `backend/`:

```bash
./gradlew build          # kompilieren + Tests
./gradlew test           # nur Tests
./gradlew bootRun        # nur Backend starten (Port 8080)
./gradlew dev            # Frontend (Port 3000) + Backend (Port 8080) gemeinsam
```

`dev` führt bei Bedarf `npm install` aus, startet den Vite-Dev-Server im
Hintergrund (Ausgabe mit Präfix `[vite]`) und danach `bootRun`. Ctrl+C beendet
beide; der Vite-Prozess hängt an einem Gradle-Build-Service und wird am
Build-Ende beendet.

- Java-21-Toolchain. Ist lokal nur ein anderes JDK vorhanden, lädt Gradle die
  Toolchain über den foojay-Resolver (Plugin in `settings.gradle`) automatisch.
- Erster Lauf lädt Gradle-Distribution und Abhängigkeiten (Netzwerk nötig).

## Frontend

Ausführung aus `frontend/`:

```bash
npm install
npm run dev              # Vite Dev-Server auf http://localhost:3000
npm run build            # Produktionsbuild nach dist/
```

Der Dev-Server erwartet das Backend unter `http://localhost:8080` (per Vite-Proxy
`/api`). Der Port 3000 ist fest (`strictPort`): CORS im Backend erlaubt nur
`http://localhost:3000`. Ist der Port belegt, bricht Vite ab, statt auf einen
anderen Port auszuweichen.

## Einstellungen

Seriennummer, Zugangsdaten und Berichtskopf werden in der App gepflegt und vom
Backend in `w5x.settings-file` (Standard `~/.w5x/settings.json`) gespeichert
(ADR 0003). Für lokale Tests mit separater Datei:

```bash
W5X_SETTINGSFILE=/tmp/w5x-test/settings.json ./gradlew dev
```

- Tests, die den Spring-Kontext starten, setzen `w5x.settings-file` auf eine Datei
  unter `build/tmp/`, damit nie die echte Einstellungsdatei gelesen oder
  überschrieben wird.
- Die Beispiel-Logdatei liegt unter `src/test/resources/samples/` und ist nur
  Testgrundlage.
- Zugangsdaten gehören nicht ins Repo.

## Windows-Installer (ADR 0004)

```bash
./gradlew desktopImage                          # App-Image der aktuellen Plattform
./gradlew windowsInstaller -PwinArch=x64        # nur unter Windows, braucht Inno Setup 6
```

- Beide Tasks betten das gebaute Frontend automatisch ein (`buildFrontend` →
  `static/`). Ein normaler `build`/`bootRun` tut das nicht; erzwingen mit
  `-PwithFrontend`.
- `jlinkRuntime` erzeugt die Laufzeit aus der Modulliste in `build.gradle`. Wird
  ein JDK-Modul fehlen (z. B. nach neuer Bibliothek), fällt das erst beim Start des
  App-Images auf → nach Änderungen an Abhängigkeiten `desktopImage` bauen und
  starten (Rauchtest: Oberfläche, Bericht mit echtem Abruf, PDF).
- Lokaler Rauchtest mit separater Einstellungsdatei, z. B. unter macOS:
  `W5X_SETTINGSFILE=/tmp/w5x-test/settings.json build/desktop/image/Ladesitzungsbericht.app/Contents/MacOS/Ladesitzungsbericht`
- Die Versionsnummer (`version` in `build.gradle`) muss numerisch sein und darf
  nicht mit 0 beginnen (jpackage).
- Windows-Installer entstehen über GitHub Actions (`.github/workflows/windows-installer.yml`):
  manuell starten („Run workflow“) oder Tag `v*` pushen. Ergebnis als Artefakt
  `Ladesitzungsbericht-x64-setup` bzw. `-arm64-setup`.
- `installer/windows/app.ico` ist aus `AppIcon` erzeugt; bei Änderung des Symbols
  neu erzeugen.

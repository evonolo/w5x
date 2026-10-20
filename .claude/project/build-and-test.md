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

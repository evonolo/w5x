# w5x – Wallbox Ladesitzungsbericht

Eigenständige Java/React-App, die Log-Dateien einer Loxone-Wallbox liest, die
Ladesitzungen eines Monats aggregiert und als HTML-Vorschau und A4-PDF bereitstellt.
Nachfolger eines Node-RED-Flows.

## Struktur

- `docs/` – autoritatives Projektwissen (Start: [docs/README.md](docs/README.md))
- `.claude/` – Arbeitskonventionen (aktives Profil: `.claude/profiles/wallbox-report.md`)
- `backend/` – Spring Boot (Java 21, Gradle), Parser + Report + PDF + REST-API
- `frontend/` – React + TypeScript + Vite

## Schnellstart

Beides zusammen (Frontend http://localhost:3000, Backend Port 8080):

```bash
cd backend
./gradlew dev
```

Oder einzeln – Backend (Port 8080):

```bash
cd backend
./gradlew bootRun
```

Frontend (Port 3000, proxyt `/api` auf 8080):

```bash
cd frontend
npm install
npm run dev
```

## Einstellungen und Zugangsdaten

Die App wertet genau **einen** Loxone-Miniserver aus. Seriennummer, Benutzer und
Kennwort werden **in der App** gepflegt: Navigation „Einstellungen“ bzw.
http://localhost:3000/#einstellungen.

| Feld | Pflicht | Hinweis |
|------|---------|---------|
| Miniserver-Seriennummer | ja | z. B. `504F94A22F32`; daraus wird `https://dns.loxonecloud.com/<Seriennummer>/dev/fsget/log/wallbox.log` gebildet |
| Benutzer, Kennwort | ja | Loxone-Benutzer mit Zugriff auf die Log-Datei |
| Standortbezeichnung, Wallbox-ID | nein | erscheinen im Berichtskopf |

Solange die Pflichtfelder fehlen, weist die Berichtsansicht auf die Einstellungen
hin. Das Kennwort wird nach dem Speichern nicht mehr angezeigt; bleibt das Feld
beim erneuten Speichern leer, gilt das bisherige Kennwort weiter.

**Ablage:** Das Backend speichert die Einstellungen in `~/.w5x/settings.json`
(nur für den eigenen Benutzer lesbar). Das Kennwort steht dort im Klartext – siehe
[ADR 0003](docs/decisions/0003-local-settings-file.md). Ein anderer Ablageort lässt
sich über die Umgebungsvariable `W5X_SETTINGSFILE` (bzw. Property
`w5x.settings-file`) festlegen:

```bash
W5X_SETTINGSFILE=/pfad/zu/settings.json ./gradlew dev
```

Die App hat kein Login. Wer das Backend über das Netz erreicht, kann die
Einstellungen ändern (siehe [offene Frage OQ-2](docs/open-questions.md)).

## Hilfe

Die App enthält eine Hilfeseite (Navigation „Hilfe“ bzw.
http://localhost:3000/#hilfe), die erklärt, wie die Log-Datei ausgewertet wird und
wie man einen Bericht von Hand nachprüft.

## Endpunkte

- `GET /api/settings`, `PUT /api/settings`
- `GET /api/report?year=&month=`
- `GET /api/report/pdf?year=&month=`

Contract: [docs/api/report-api.md](docs/api/report-api.md).

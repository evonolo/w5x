# REST-API-Contract

Basis-Pfad: `/api`. Alle Antworten UTF-8. Fehler als JSON mit `status`, `error`,
`message`.

## GET /api/settings

Aktuelle [Einstellungen](../features/settings.md) – **ohne Kennwort** (ST-4).

**200**
```json
{
  "serialNumber": "504F94A22F32",
  "username": "admin",
  "passwordSet": true,
  "locationLabel": "Neuhausen 22, 6391 Fieberbrunn",
  "wallboxId": "50:4F:94:FF:FE:C1:0F:A0",
  "complete": true
}
```

Nicht gesetzte Felder sind `""`. `complete` gemäß ST-1.

## PUT /api/settings

Speichert die Einstellungen und liefert sie wie `GET /api/settings` zurück.

**Body**
```json
{
  "serialNumber": "504F94A22F32",
  "username": "admin",
  "password": "geheim",
  "locationLabel": "Neuhausen 22, 6391 Fieberbrunn",
  "wallboxId": "50:4F:94:FF:FE:C1:0F:A0"
}
```

- Alle Felder werden ersetzt; fehlende Felder gelten als `""`.
- Ausnahme `password`: fehlt es oder ist es leer, bleibt das gespeicherte Kennwort
  unverändert (ST-5).
- **400** bei ungültiger Seriennummer (ST-3).

## GET /api/report

Query-Parameter:

| Param | Typ | Pflicht | Bedeutung |
|-------|-----|---------|-----------|
| `year` | int | ja | Berichtsjahr, z. B. `2024` |
| `month` | int (1–12) | ja | Berichtsmonat |

**200** – Bericht als View-Modell (bereits formatierte Felder; `location` und
`wallbox` aus den Einstellungen):
```json
{
  "location": "Neuhausen 22, 6391 Fieberbrunn",
  "wallbox": "50:4F:94:FF:FE:C1:0F:A0",
  "year": 2024,
  "month": 2,
  "generatedDate": "12.08.2026",
  "generatedTime": "18:52",
  "totalEnergyKwh": "90.31",
  "totalDurationFormatted": "08 Stunden, 03 Minuten, 20 Sekunden",
  "sessionCount": 2,
  "sessions": [
    {
      "energyKwh": "66.99",
      "durationFormatted": "05 Stunden, 00 Minuten, 00 Sekunden",
      "start": "01.02.2024 00:20:53",
      "stop": "01.02.2024 05:20:53",
      "user": "-",
      "startWeekday": "Do",
      "startDay": "01.02.",
      "startTime": "00:20",
      "stopTime": "05:20",
      "stopDayLabel": "gleicher Tag",
      "durationShort": "5 h 00 min",
      "energyShare": 1.0
    }
  ]
}
```

## GET /api/report/pdf

Gleiche Query-Parameter wie `/api/report`.

**200** – `Content-Type: application/pdf`,
`Content-Disposition: attachment; filename="ladesitzungsbericht-<year>-<month>.pdf"`.

## Fehler

| Status | Fall |
|--------|------|
| 400 | fehlende/ungültige Parameter (`month` außerhalb 1–12), ungültige Seriennummer |
| 409 | Einstellungen unvollständig, kein Bericht möglich (ST-2) |
| 502 | Log-Quelle (Loxone) nicht erreichbar / Abruf fehlgeschlagen (AC-6) |

## CORS

Für die lokale Entwicklung ist `http://localhost:3000` (Vite Dev-Server) als Origin
zugelassen (Methoden `GET`, `PUT`).

## Änderungshistorie

- 2026-10-20: `GET /api/locations` und Parameter `location` entfernt (nur noch ein
  Miniserver); `GET`/`PUT /api/settings` neu; 404 für unbekannte Standorte entfällt,
  409 für unvollständige Einstellungen neu; PDF-Dateiname ohne Standort.
- 2026-10-20: Sitzungen um Kurzformen für die App ergänzt (`startWeekday`,
  `startDay`, `startTime`, `stopTime`, `stopDayLabel`, `durationShort`,
  `energyShare` 0–1), siehe [Sitzungszeilen](../features/monthly-report.md#sitzungszeilen-in-der-app).
- 2026-10-20: Feld `person` aus Einstellungen und Bericht entfernt (wurde nirgends
  angezeigt). Unbekannte Felder in einer bestehenden Einstellungsdatei werden
  ignoriert.

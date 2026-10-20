# Domäne: Ladesitzung (ChargingSession)

## Begriffe (Glossar)

| Begriff | Bedeutung |
|---------|-----------|
| **Ladesitzung** (ChargingSession) | Ein abgeschlossener Ladevorgang eines Fahrzeugs an der Wallbox. Endet mit dem Ereignis „Fahrzeug getrennt". |
| **Einstellungen** (Settings) | Die eine Wallbox-Installation: Miniserver-Seriennummer, Zugangsdaten sowie Standortbezeichnung und Wallbox-ID für den Berichtskopf. Siehe [features/settings.md](../features/settings.md). |
| **Standortbezeichnung** | Freitext im Berichtskopf (Teil der Einstellungen). |
| **Monatsbericht** (MonthlyReport) | Aggregation aller Ladesitzungen der Wallbox in einem Kalendermonat. |
| **Wallbox-ID** | Technische Kennung der Wallbox (z. B. `50:4F:94:FF:FE:C1:0F:A0`). Nur informativ im Bericht. |

## Entität: ChargingSession

Wird aus einer Log-Zeile abgeleitet (siehe [log-format.md](log-format.md)).

| Attribut | Typ | Herkunft |
|----------|-----|----------|
| `stop` | Zeitpunkt (lokal) | Ereigniszeit „Fahrzeug getrennt" |
| `durationSeconds` | Ganzzahl (s) | Feld `Dauer` |
| `start` | Zeitpunkt (lokal) | abgeleitet: `stop − durationSeconds` |
| `energyKwh` | Dezimal | Feld `Geladene Energie` |
| `costEur` | Dezimal | Feld Kosten (geparst, **nicht** ausgewiesen) |
| `user` | Text | Feld `user` |
| `rawLine` | Text | ursprüngliche Log-Zeile (Diagnose) |

### Invarianten / Regeln

- **CS-1**: Eine Zeile ohne gültigen Stop-Zeitpunkt („Fahrzeug getrennt") erzeugt
  **keine** Ladesitzung und wird übersprungen.
- **CS-2**: `start` ist stets `stop − durationSeconds`. (Verhalten bei
  Zeitumstellung: siehe [OQ-1](../open-questions.md#oq-1-startzeitpunkt-bei-zeitumstellung).)
- **CS-3**: Fehlende optionale Felder (Energie, Dauer, Nutzer, Kosten) führen zu
  neutralen Werten (0 bzw. leer), nicht zum Verwerfen der Sitzung.

## Aggregat: MonthlyReport

| Attribut | Bedeutung |
|----------|-----------|
| `locationLabel`, `wallboxId` | aus den Einstellungen |
| `year`, `month` | gewählter Berichtsmonat (Monat 1–12) |
| `sessions` | gefilterte, sortierte Ladesitzungen des Monats |
| `totalEnergyKwh` | Summe `energyKwh` |
| `totalDurationSeconds` | Summe `durationSeconds` |
| `sessionCount` | Anzahl Sitzungen |
| `generatedAt` | Erstellungszeitpunkt des Berichts |

### Regeln

- **MR-1**: Eine Sitzung gehört zum Bericht, wenn `stop.year == year` und
  `stop.monthValue == month`.
- **MR-2**: Sitzungen werden **aufsteigend nach `start`** sortiert.
  (Korrektur des Node-RED-Bugs `a.start - b.stop`.)
- **MR-3**: Summen berücksichtigen ausschließlich die zum Monat gehörenden
  Sitzungen.
- **MR-4**: `costEur` wird summiert vorgehalten, aber gemäß Vision nicht dargestellt.

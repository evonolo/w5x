# Feature: Monatsbericht

## Intent

Ein Nutzer wählt einen **Monat (Jahr + Monat)** und erhält den Ladesitzungsbericht
seiner Wallbox als Vorschau und optional als PDF. Voraussetzung sind vollständige
[Einstellungen](settings.md) (ST-1).

## Ablauf

1. Nutzer wählt Jahr und Monat (Frontend).
2. Backend holt die `wallbox.log` vom Miniserver aus den Einstellungen (siehe
   [ADR 0001](../decisions/0001-stateless-report.md): stateless, kein Cache/DB).
3. Log wird in [Ladesitzungen](../domain/charging-session.md) geparst.
4. Sitzungen werden auf den Monat gefiltert (Regel MR-1) und sortiert (MR-2).
5. Summen werden gebildet (MR-3).
6. Ergebnis wird als JSON (Vorschau) bzw. als A4-PDF geliefert.

## Berichtsinhalt

**Kopf:**
- Titel „Ladesitzungsbericht"
- Standortbezeichnung (aus den Einstellungen) + Erstellungsdatum/-uhrzeit
- Wallbox-ID
- Gesamte Ladedauer
- Gesamte Energie (kWh)

**Tabelle** (eine Zeile je Sitzung, sortiert nach `start`):
- Energie (kWh)
- Dauer
- Zeitraum (`start` – `stop`)

Kosten werden **nicht** dargestellt (Vision / MR-4).

## Formatierung (lokalisiert Deutsch)

| Wert | Format | Beispiel |
|------|--------|----------|
| Datum/Zeit einer Sitzung | `dd.MM.yyyy HH:mm:ss` | `31.01.2024 23:55:55` |
| Erstellungsdatum | `dd.MM.yyyy` | `12.08.2026` |
| Erstellungsuhrzeit | `HH:mm` | `18:52` |
| Dauer | `HH 'Stunden, 'mm 'Minuten, 'ss 'Sekunden'` | `03 Stunden, 20 Minuten, 00 Sekunden` |
| Energie | zwei Nachkommastellen, Punkt | `45.54` |

- Die Dauer-Stunden werden **nicht** auf 24 h begrenzt (Gesamtdauer kann > 24 h
  sein), zweistellig aufgefüllt.
- Diese Formate gelten für das PDF, die Summen und die exakten Werte. Die
  Sitzungszeilen der App verwenden zusätzlich die Kurzformen unten.

## Sitzungszeilen in der App

Jede Sitzung erscheint als eigene Zeile (Kachel) mit vier Spalten (Entwurf:
Claude Design, Vorschlag 1a „Dunkles Dashboard mit Kacheln“, 2026-10-20):

| Spalte | Inhalt | Beispiel |
|--------|--------|----------|
| Start | Wochentag (zweistellig) über dem Datum `dd.MM.` des Starts | `Mi` / `30.09.` |
| Zeitraum | Start- und Enduhrzeit `HH:mm → HH:mm`, dahinter der Endtag: „gleicher Tag" oder Wochentag + `dd.MM.` | `19:06 → 06:24` `Do 01.10.` |
| Dauer | `H h mm min` | `11 h 17 min` |
| Energie | Balken + Wert (zwei Nachkommastellen, Punkt) | `22.50 kWh` |

- **SR-1**: Die Kurzformen lassen die Sekunden weg (abgeschnitten, nicht gerundet).
  Die exakten Zeitpunkte und die exakte Dauer stehen im Tooltip der Zeile.
- **SR-2**: Die Balkenlänge ist die Energie der Sitzung im Verhältnis zur
  energiereichsten Sitzung **desselben Berichts** (diese = voller Balken).
- **SR-3**: Die Energie wird nicht gekürzt (zwei Nachkommastellen wie Summe und PDF),
  damit Zeilen und Summe nachrechenbar bleiben.
- Wochentage: Mo, Di, Mi, Do, Fr, Sa, So.

## Akzeptanzkriterien

- **AC-1**: Für die Beispiel-Logdatei (Testdaten) und Monat `2024-02` enthält der Bericht genau
  die Sitzungen mit `stop` im Februar 2024, aufsteigend nach `start`.
- **AC-2**: Gesamtenergie = Summe der `energyKwh` der enthaltenen Sitzungen, auf zwei
  Nachkommastellen.
- **AC-3**: Gesamtdauer = Summe der `durationSeconds`, formatiert als
  „HH Stunden, mm Minuten, ss Sekunden".
- **AC-4**: Zeilen ohne „Fahrzeug getrennt" erzeugen keine Berichtszeile (CS-1).
- **AC-5**: Das PDF ist A4 Hochformat und enthält Kopf + Tabelle wie oben; keine
  Kostenspalte.
- **AC-6**: Ist die Log-Quelle nicht erreichbar, liefert die API einen klaren Fehler
  (kein leerer/teilweiser Bericht ohne Hinweis). Als nicht erreichbar gilt jede
  Antwort, die nach dem Folgen von Weiterleitungen nicht erfolgreich (2xx) ist –
  z. B. abgelehnte Zugangsdaten (401) oder eine nicht auflösbare Weiterleitung
  (siehe [Abruf der Log-Datei](../domain/log-format.md#abruf-der-log-datei)).
- **AC-7**: Sind die Einstellungen unvollständig, liefert die API einen klaren Fehler
  (HTTP 409) statt eines Berichts (ST-2).

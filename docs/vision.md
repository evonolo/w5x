# Vision

## Zweck

Betreiber einer Loxone-Wallbox benötigen für einzelne Standorte/Personen einen
nachvollziehbaren **Monatsbericht der Ladesitzungen** (Zeitpunkt, Dauer, geladene
Energie). Die Rohdaten liegen als fortlaufende Log-Datei (`wallbox.log`) auf dem
Loxone-Miniserver und sind für Endnutzer nicht auswertbar.

Die App holt diese Log-Datei, wertet die Ladesitzungen eines gewählten Monats aus
und stellt das Ergebnis als lesbare Vorschau und als PDF (Ladesitzungsbericht)
bereit.

## Ziele

- Automatisierter Abruf der `wallbox.log` vom Miniserver (HTTP + Basic Auth).
- Zuverlässiges Parsen der Ladesitzungen aus dem Loxone-Log-Format.
- Aggregation pro Kalendermonat: Gesamtenergie, Gesamtdauer, Anzahl Sitzungen.
- Bericht als HTML-Vorschau (React) und als A4-PDF (Download).
- Genau **ein** Miniserver bzw. eine Wallbox-Installation; Verbindung (Seriennummer,
  Zugangsdaten) und Berichtskopf pflegt der Nutzer in der App
  ([Einstellungen](features/settings.md)).

## Nicht-Ziele

- **Keine dauerhafte Speicherung** der Sitzungen in einer Datenbank. Der Bericht
  wird bei jedem Abruf frisch aus der Log-Datei erzeugt (siehe
  [ADR 0001](decisions/0001-stateless-report.md)).
- **Keine Kostenausweisung** im Bericht. Kostenwerte im Log werden geparst, aber
  nicht dargestellt (bewusste Übernahme aus dem Node-RED-Flow).
- Keine Nutzerverwaltung, kein Login, keine Mandantenfähigkeit.
- Keine Auswertung mehrerer Miniserver/Standorte in einer Installation.
- Keine Steuerung/Konfiguration der Wallbox selbst; die App ist rein lesend.

## Scope

Eine Java-Backend-Anwendung (Spring Boot) mit REST-API und PDF-Erzeugung sowie ein
React-Frontend zur Pflege der Einstellungen, zur Auswahl des Zeitraums und zur
Berichtsanzeige.
Monorepo mit `backend/` und `frontend/`.

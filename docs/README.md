# Projektwissen – Index

Dies ist der Einstieg in das autoritative Projektwissen (siehe `CLAUDE.md` §29).

Die App **w5x / wallbox-report** liest Log-Dateien einer Loxone-Wallbox, aggregiert
die Ladesitzungen eines Monats und stellt daraus einen Bericht (HTML-Vorschau und
PDF) bereit. Sie ist die eigenständige Java/React-Nachfolge eines bestehenden
Node-RED-Flows.

## Autoritative Dokumente

| Dokument | Inhalt |
|----------|--------|
| [vision.md](vision.md) | Zweck, Ziele, Nicht-Ziele, Scope |
| [domain/charging-session.md](domain/charging-session.md) | Domänenmodell „Ladesitzung" und Begriffe |
| [domain/log-format.md](domain/log-format.md) | Spezifikation des Wallbox-Log-Formats |
| [features/monthly-report.md](features/monthly-report.md) | Feature „Monatsbericht" (Verhalten, Regeln, Akzeptanz) |
| [features/settings.md](features/settings.md) | Feature „Einstellungen" (Miniserver-Verbindung, Berichtskopf, lokale Speicherung) |
| [features/help-page.md](features/help-page.md) | Feature „Hilfeseite" (Erklärung der Log-Auswertung, manuelle Prüfung) |
| [ui/theme.md](ui/theme.md) | UI: Theme und Bedienung (Loxone-Anmutung, TH-*) |
| [api/report-api.md](api/report-api.md) | REST-API-Contract |
| [open-questions.md](open-questions.md) | Offene Fragen (nicht autoritativ) |
| [decisions/0001-stateless-report.md](decisions/0001-stateless-report.md) | ADR: Stateless-Report, kein Persistenzspeicher |
| [decisions/0002-pdf-openhtmltopdf.md](decisions/0002-pdf-openhtmltopdf.md) | ADR: PDF-Erzeugung mit openhtmltopdf |
| [decisions/0003-local-settings-file.md](decisions/0003-local-settings-file.md) | ADR: Einstellungen in lokaler Datei des Backends |

## Herkunft

Das Verhalten wurde aus dem Node-RED-Flow „Wallbox" (Tab `cb8767695833d1c9`,
Subflow „wallbox report" `f0be2d336b923566`) abgeleitet. Der Flow bleibt als
Referenz erhalten, ist aber nicht mehr autoritativ. Bekannte Fehler des Flows
(Zeitzonen-Mischung, Sortier-Bug `a.start - b.stop`, `getDay()` statt Monatstag)
werden in dieser App bewusst korrigiert – siehe die jeweiligen Dokumente.

# ADR 0003: Verbindungseinstellungen in lokaler Datei des Backends

- Status: akzeptiert
- Datum: 2026-10-20

## Kontext

Bisher waren Standorte (inkl. Loxone-URL und Zugangsdaten) in `application.yaml`
bzw. Umgebungsvariablen konfiguriert. Neu soll der Nutzer die Verbindung zum
Miniserver (Seriennummer, Benutzer, Kennwort) sowie die Angaben für den
Berichtskopf **in der App selbst pflegen**. Es gibt nur noch **einen** Miniserver.
Die Einstellungen müssen einen Neustart überdauern.

Optionen:

1. **Datei des Backends** im Benutzerverzeichnis.
2. **Browser-Speicher** (`localStorage`); die Zugangsdaten werden bei jedem
   Berichtsabruf an das Backend mitgeschickt.
3. Datenbank.

## Entscheidung

Das Backend speichert die Einstellungen als JSON-Datei, standardmäßig
`~/.w5x/settings.json` (konfigurierbar über `w5x.settings-file`).

- Die Datei wird – wo das Dateisystem es unterstützt – nur für den Eigentümer
  lesbar/schreibbar angelegt (Verzeichnis `700`, Datei `600`).
- Das Kennwort wird im Klartext gespeichert (Basic Auth erfordert den Klartext;
  eine lokale Verschlüsselung bräuchte einen ebenfalls lokal abgelegten Schlüssel
  und brächte keinen echten Schutz).
- Das Kennwort verlässt das Backend nach dem Speichern nie wieder: Die API liefert
  nur, **ob** ein Kennwort gesetzt ist.

## Begründung

- Das Backend ruft die Log-Datei ab; es braucht die Zugangsdaten ohnehin.
- Kein Kennwort im Browser-Speicher und nicht bei jedem Request über das Netz.
- Einstellungen gelten unabhängig vom verwendeten Browser.
- Eine Datei ist für einen einzelnen Datensatz ausreichend; keine Datenbank nötig.

## Konsequenzen

- [ADR 0001](0001-stateless-report.md) bleibt gültig: Ladesitzungen werden weiterhin
  nicht gespeichert. Persistiert werden ausschließlich die Einstellungen.
- Wer Lesezugriff auf das Benutzerverzeichnis des Backend-Prozesses hat, kann das
  Kennwort lesen.
- Die Einstellungs-API ist – wie die gesamte App (Vision: kein Login) – nicht
  authentifiziert. Wer das Backend über das Netz erreicht, kann die Einstellungen
  ändern (nicht aber das Kennwort auslesen). Siehe
  [OQ-2](../open-questions.md#oq-2-erreichbarkeit-des-backends-im-netzwerk).
- Konfiguration über `application.yaml`/Umgebungsvariablen für Standorte entfällt.

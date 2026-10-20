# ADR 0001: Stateless-Report ohne Persistenzspeicher

- Status: akzeptiert
- Datum: 2026-08-12

## Kontext

Der Node-RED-Flow erzeugt den Bericht bei jedem Lauf frisch aus der jeweils
abgerufenen `wallbox.log`; die Roh-Logs werden lediglich als Backup-Dateien
abgelegt. Für die Neuentwicklung stand die Wahl zwischen einer Datenbank mit
deduplizierten Ladesitzungen (echte Historie, Mehrfach-Wallbox-Auswertung) und
einem zustandslosen Ansatz.

## Entscheidung

Die App bleibt **stateless**: Jeder Berichtsabruf holt die Log-Datei erneut, parst
und aggregiert sie in-memory. Es gibt **keine Datenbank** und keinen serverseitigen
Cache der Sitzungen.

## Begründung

- Hält die App nah am bewährten Node-RED-Verhalten.
- Minimaler Betriebsaufwand (kein Schema, keine Migrationen, keine Deduplizierung).
- Die Loxone-Log-Datei ist die einzige Quelle der Wahrheit.

## Konsequenzen

- Auswertungen sind auf den Umfang der jeweils vorliegenden Log-Datei begrenzt.
- Historie über den Log-Umfang hinaus ist nicht möglich (bewusstes Nicht-Ziel).
- Wiederholte Abrufe erzeugen wiederholte HTTP-Requests an den Miniserver; das ist
  für den erwarteten, gering-frequenten Gebrauch akzeptabel.
- Ein späterer Wechsel zu einer Persistenz würde diese Entscheidung ablösen (neuer
  ADR erforderlich).

# Feature: Einstellungen

## Intent

Der Nutzer pflegt die Verbindung zu **seinem** Loxone-Miniserver und die Angaben
für den Berichtskopf direkt in der App. Es gibt genau **eine** Wallbox-Installation;
eine Auswahl zwischen mehreren Standorten entfällt.

## Inhalt der Einstellungen

| Feld | Pflicht | Bedeutung |
|------|---------|-----------|
| Miniserver-Seriennummer | ja | Seriennummer des Miniservers, z. B. `504F94A22F32`. Daraus wird die Adresse der Log-Datei gebildet (siehe [log-format.md](../domain/log-format.md#abruf-der-log-datei)). |
| Benutzer | ja | Loxone-Benutzer für den Abruf (Basic Auth). |
| Kennwort | ja | Kennwort dieses Benutzers. |
| Standortbezeichnung | nein | Im Berichtskopf angezeigt, z. B. `Neuhausen 22, 6391 Fieberbrunn`. |
| Wallbox-ID | nein | Im Berichtskopf angezeigt, rein informativ. |

## Regeln

- **ST-1**: Die Einstellungen sind **vollständig**, wenn Seriennummer, Benutzer und
  Kennwort gesetzt sind. Die übrigen Felder sind optional und erscheinen leer im
  Bericht, wenn sie fehlen.
- **ST-2**: Solange die Einstellungen unvollständig sind, wird kein Bericht erzeugt
  und die Log-Datei nicht abgerufen; die API antwortet mit einem klaren Fehler
  (HTTP 409), die App weist auf die Einstellungen hin.
- **ST-3**: Die Seriennummer besteht nur aus Buchstaben und Ziffern (sie wird Teil
  der Adresse). Führende/folgende Leerzeichen werden bei allen Feldern außer dem
  Kennwort entfernt; das Kennwort wird unverändert übernommen.
- **ST-4**: Das Kennwort wird nach dem Speichern **nie** wieder angezeigt oder von
  der API ausgeliefert. Die App zeigt nur an, dass ein Kennwort gespeichert ist.
- **ST-5**: Wird beim Speichern kein neues Kennwort eingegeben, bleibt das
  gespeicherte Kennwort unverändert.
- **ST-6**: Unvollständige Einstellungen dürfen gespeichert werden (z. B. erst die
  Seriennummer, später die Zugangsdaten).
- **ST-7**: Die Einstellungen werden lokal auf dem Rechner des Backends gespeichert
  und überdauern einen Neustart ([ADR 0003](../decisions/0003-local-settings-file.md)).

## Oberfläche

- Eigene Ansicht „Einstellungen", erreichbar über die Navigation im Kopf und unter
  der Adresse `#einstellungen`.
- Formular mit den Feldern oben; das Kennwortfeld ist leer, mit dem Hinweis
  „gespeichert – leer lassen, um es beizubehalten", falls bereits ein Kennwort
  gesetzt ist.
- „Speichern" übernimmt die Werte; Erfolg und Fehler (z. B. ungültige Seriennummer)
  werden direkt am Formular angezeigt.
- Sind die Einstellungen unvollständig, zeigt die Berichtsansicht statt der
  Bericht-Buttons einen Hinweis mit Link zu den Einstellungen.

## Akzeptanzkriterien

- **AC-S1**: Nach dem Speichern und einem Neustart des Backends sind die Werte
  unverändert vorhanden.
- **AC-S2**: `GET /api/settings` enthält das Kennwort in keiner Form.
- **AC-S3**: Speichern ohne Kennwort ändert ein gespeichertes Kennwort nicht (ST-5).
- **AC-S4**: Eine Seriennummer mit anderen Zeichen als Buchstaben/Ziffern wird
  abgelehnt (HTTP 400) und nicht gespeichert.
- **AC-S5**: Berichtsabruf bei unvollständigen Einstellungen liefert HTTP 409 (ST-2).

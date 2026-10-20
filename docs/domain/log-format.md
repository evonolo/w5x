# Domäne: Wallbox-Log-Format

Autoritative Spezifikation des von der Loxone-Wallbox erzeugten Log-Formats, wie es
über `dev/fsget/log/wallbox.log` bereitgestellt wird.

## Abruf der Log-Datei

Die Datei wird per HTTP GET mit Basic Auth vom Miniserver abgerufen.

- Die Adresse wird aus der Miniserver-Seriennummer der
  [Einstellungen](../features/settings.md) gebildet:
  `https://dns.loxonecloud.com/<Seriennummer>/dev/fsget/log/wallbox.log`
  (**Loxone-Cloud-DNS**). Diese liefert die Datei nicht selbst, sondern antwortet
  mit einer Weiterleitung (HTTP 307) auf die aktuelle Adresse des Miniservers.
- Weiterleitungen werden gefolgt (höchstens 5); die Zugangsdaten werden dabei an das
  Weiterleitungsziel mitgesendet. Eine Weiterleitung von HTTPS auf HTTP wird
  abgelehnt, damit Zugangsdaten nicht unverschlüsselt übertragen werden.
- Nur eine erfolgreiche Antwort (2xx) gilt als Log-Inhalt. Jede andere Antwort ist
  ein Abruffehler (AC-6), nie ein leerer Bericht.
- Ohne vollständige Einstellungen findet kein Abruf statt (ST-2).
- Der Inhalt wird im Zeichensatz der Antwort gelesen, ohne Angabe als UTF-8.

## Aufbau

Die Datei ist zeilenweise. **Jede Zeile mit dem Ereignis „Fahrzeug getrennt"
entspricht genau einer abgeschlossenen [Ladesitzung](charging-session.md).** Zeilen
ohne dieses Ereignis (z. B. reine Kopf-/Statuszeilen) werden ignoriert.

Eine Zeile besteht aus semikolon-getrennten Feldern. Beispiel:

```
1970-01-01 00:00:00;Wallbox Log;2024-01-31 23:55:55:Fahrzeug getrennt;user:-;Geladene Energie:45.54kWh;Dauer:12000 s;54.45€
```

## Relevante Felder

Die Felder werden **anhand von Mustern erkannt, nicht anhand ihrer Position**
(robust gegen zusätzliche/fehlende Felder):

| Bedeutung | Muster | Beispiel | Ziel |
|-----------|--------|----------|------|
| Stop-Zeitpunkt | `<yyyy-MM-dd HH:mm:ss>:Fahrzeug getrennt` | `2024-01-31 23:55:55:Fahrzeug getrennt` | `stop` |
| Geladene Energie | `Geladene Energie:<zahl>kWh` | `Geladene Energie:45.54kWh` | `energyKwh` |
| Dauer | `Dauer:<ganzzahl> s` | `Dauer:12000 s` | `durationSeconds` |
| Nutzer | `user:<text>` | `user:-` | `user` |
| Kosten | `<zahl>€` | `54.45€` | `costEur` (geparst, nicht ausgewiesen) |

- Zahlen verwenden **Punkt** als Dezimaltrennzeichen. Der Nachkommateil kann fehlen
  (z. B. `0€`).
- Das führende Datum (`1970-01-01 00:00:00`) und das Feld `Wallbox Log` tragen keine
  fachliche Bedeutung und werden nicht ausgewertet.

## Abgeleitete Werte

- `stop` = geparster Zeitpunkt des Ereignisses „Fahrzeug getrennt".
- `start` = `stop` − `durationSeconds`.

## Zeit und Zeitzone

Die Zeitstempel sind **lokale Zeit** der Anlage ohne Zeitzonenangabe. Sie werden als
`LocalDateTime` interpretiert. Die Monatszuordnung erfolgt direkt über Jahr und
Monat des `stop`-Zeitpunkts (`year`, `monthValue`).

> Korrektur gegenüber dem Node-RED-Flow: Dort wurde beim Parsen lokale Zeit, beim
> Filtern aber `Date.UTC` verwendet, was an Monatsgrenzen zu Fehlzuordnungen führen
> konnte. Diese App vermeidet die Umrechnung vollständig.

## Beispiel-Daten

Eine synthetische Beispieldatei liegt unter
`backend/src/test/resources/samples/wallbox-sample.log` und dient als Testgrundlage
(AC-1 bis AC-3) sowie als Rechenbeispiel der Hilfeseite (HP-2). Sie stammt aus den
Test-/Synthetik-Pfaden des ursprünglichen Flows.

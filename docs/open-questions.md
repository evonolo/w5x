# Offene Fragen

Ungeklärte fachliche oder technische Fragen. Einträge hier sind **nicht**
autoritativ und treiben keine Implementierung, bis sie entschieden und in das
zuständige Dokument übernommen sind.

## OQ-1: Startzeitpunkt bei Zeitumstellung

- Status: offen
- Seit: 2026-10-20
- Betrifft: [CS-2](domain/charging-session.md), [log-format.md – Zeit und Zeitzone](domain/log-format.md#zeit-und-zeitzone)

**Beobachtung:** Zeitstempel im Log sind lokale Zeit ohne Zeitzonenangabe. `start`
wird als `stop − durationSeconds` auf der lokalen Uhrzeit berechnet (CS-2), ohne
Kenntnis der Zeitzone. Liegt eine Ladesitzung über einer Sommer-/Winterzeit-
Umstellung, weicht der berechnete Start um eine Stunde von der tatsächlich auf der
Uhr abgelesenen Startzeit ab.

Beispiel (Umstellung auf Winterzeit, 25.10.2026, 03:00 → 02:00): Eine Sitzung mit
Stop 25.10.2026 06:00:00 und Dauer 28800 s (8 h) begann tatsächlich um 23:00 Uhr
des Vortags (Sommerzeit). Berechnet wird 22:00 Uhr.

**Nicht betroffen:** Dauer, Energie, Summen und die Monatszuordnung (über `stop`).

**Offen:**
- Ist die Abweichung für den Bericht relevant oder akzeptabel?
- Falls relevant: Soll die Zeitzone der Anlage konfigurierbar werden (z. B.
  `Europe/Vienna`), damit `start` zeitzonenbewusst berechnet wird?
- Welche Zeitzone schreibt der Miniserver bei Zeitumstellung tatsächlich ins Log
  (Prüfung an realen Log-Zeilen aus einer Umstellungsnacht)?

## OQ-2: Erreichbarkeit des Backends im Netzwerk

- Status: offen
- Seit: 2026-10-20
- Betrifft: [ADR 0003](decisions/0003-local-settings-file.md), [Einstellungen](features/settings.md)

**Beobachtung:** Die App hat kein Login (Vision). Mit der Einstellungs-API kann
jeder, der das Backend über das Netz erreicht, Seriennummer und Zugangsdaten
überschreiben (das Kennwort auslesen kann er nicht). Das Backend lauscht derzeit
auf allen Netzwerkschnittstellen.

**Offen:**
- Wie wird die App im Betrieb ausgeführt (nur lokal auf einem Rechner, im
  Heimnetz, öffentlich erreichbar)?
- Soll das Backend standardmäßig nur auf `localhost` lauschen?

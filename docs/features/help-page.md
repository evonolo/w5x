# Feature: Hilfeseite

## Intent

Nutzer sollen nachvollziehen können, **wie der Bericht aus der Log-Datei entsteht**,
und ein Ergebnis bei Bedarf **selbst von Hand nachprüfen** können.

## Zugang

- In der Navigation im Kopf der App gibt es einen Eintrag „Hilfe".
- Die Hilfeseite ist unter der Adresse `#hilfe` direkt aufrufbar (verlinkbar,
  Browser-Zurück funktioniert).
- Von der Hilfeseite führt ein Link „Zurück zum Bericht" auf die Berichtsansicht;
  die gewählten Werte (Monat, Jahr) und ein bereits geladener Bericht bleiben dabei
  erhalten.

## Inhalt

Die Hilfeseite erklärt in Nutzersprache:

1. **Woher die Daten kommen** – Abruf der `wallbox.log` vom Miniserver bei jedem
   Bericht, keine Speicherung.
2. **Aufbau einer Log-Zeile** – annotierte Beispielzeile, Bedeutung jedes Feldes,
   welche Felder ignoriert werden.
3. **Wie daraus eine Ladesitzung wird** – nur Zeilen mit „Fahrzeug getrennt",
   Berechnung von Start aus Stop und Dauer, Verhalten bei fehlenden Feldern.
4. **Wie der Monatsbericht entsteht** – Monatszuordnung nach Stop-Zeitpunkt,
   Sortierung, Summenbildung, Formatierung.
5. **Rechenbeispiel** – vollständig durchgerechnet mit der Beispieldatei
   (Februar 2024).
6. **Ergebnis selbst prüfen** – Schritt-für-Schritt-Anleitung zum manuellen
   Nachrechnen.
7. **Häufige Stolperfallen und Fehlermeldungen** – was „0 Sitzungen" bedeuten kann
   und was die Fehlermeldungen der App aussagen.

## Regeln

- **HP-1**: Die Hilfeseite **erklärt** bestehende Regeln, sie definiert keine.
  Autoritativ bleiben [log-format.md](../domain/log-format.md),
  [charging-session.md](../domain/charging-session.md) (CS-*, MR-*) und
  [monthly-report.md](monthly-report.md). Ändert sich eine dieser Regeln, wird die
  Hilfeseite im selben Zug angepasst.
- **HP-2**: Das Rechenbeispiel verwendet die Beispieldatei aus den Testdaten und
  muss mit dem Ergebnis übereinstimmen, das die Tests für Februar 2024 prüfen
  (AC-1 bis AC-3).
- **HP-3**: Kosten werden auch auf der Hilfeseite nicht ausgewiesen; es wird nur
  erwähnt, dass das Feld ignoriert wird (MR-4).

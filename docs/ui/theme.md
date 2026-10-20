# UI: Theme und Bedienung

## Intent

Die App soll sich optisch und in der Bedienung wie eine **Loxone-App** anfühlen:
dunkel, ruhig, mit kräftigem Grün als einziger Akzentfarbe, großen Kacheln und
deutlicher Rückmeldung bei jeder Berührung. Vorbild sind die Design-Tokens der
Loxone-Website (loxone.com, Stand 2026-10) und die Loxone-App.

## Grundsätze

- **TH-1 Dunkel als einziges Schema.** Die App hat kein helles Theme; Hintergrund
  ist tiefes Anthrazit, Inhalte liegen auf etwas helleren Kacheln.
- **TH-2 Grün ist Status und Aktion.** Grün markiert ausschließlich: primäre
  Aktionen, die aktive Navigation, Kennzahlen des Berichts und Erfolgsmeldungen.
  Sonst ist die Oberfläche neutral (Grau-/Weißtöne).
- **TH-3 Kacheln.** Inhalte sind in abgerundeten Kacheln gruppiert; Kennzahlen
  (Sitzungen, Energie, Ladedauer) erscheinen als große Werte-Kacheln.
- **TH-4 Haptik.** Jedes bedienbare Element gibt sichtbare Rückmeldung: Hover
  (heller), Drücken (kurzes Einsinken), Tastaturfokus (grüner Ring). Bedienflächen
  sind mindestens 44 × 44 px groß.
- **TH-5 Mobil zuerst bedienbar.** Auf schmalen Bildschirmen liegt die Navigation
  als Tab-Leiste mit Symbolen am unteren Rand (wie in der Loxone-App), auf breiten
  Bildschirmen oben.
- **TH-6 Kein fremdes Branding.** Es werden weder Loxone-Logo noch -Name noch die
  proprietäre Schrift „Loxone Sans“ verwendet. Schrift ist **Lexend**, lokal
  eingebunden (keine Anfrage an externe Server).
- **TH-7 PDF unverändert.** Das Theme gilt nur für die App. Der PDF-Bericht bleibt
  hell und druckfreundlich (siehe [monthly-report.md](../features/monthly-report.md)).

## Farb- und Formwerte

| Token | Wert | Verwendung |
|-------|------|------------|
| Hintergrund | `#171717` | Seitenhintergrund |
| Kachel | `#212121` | Kacheln, Formularfelder-Umgebung |
| Kachel erhöht | `#2a2a2a` | Eingabefelder, Hover auf Kacheln |
| Linie | `#333333` | Trennlinien, Ränder |
| Text | `#e7e5e4` | Fließtext |
| Text gedämpft | `#a3a3a3` | Beschriftungen, Nebeninfos |
| Grün | `#8cff70` | Akzent (TH-2); Hover `#a3ff8c`, gedrückt `#70fd4c` |
| Text auf Grün | `#000000` | Beschriftung grüner Buttons |
| Fehler | `#f5525f` | Fehlermeldungen |
| Hinweis | `#ffd900` | Hinweise (z. B. fehlende Einstellungen) |
| Radien | 8 / 12 / 16 px, Buttons als Pille | |

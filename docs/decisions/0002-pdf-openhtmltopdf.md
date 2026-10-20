# ADR 0002: PDF-Erzeugung mit openhtmltopdf

- Status: akzeptiert
- Datum: 2026-08-12

## Kontext

Der Node-RED-Flow rendert den Bericht als HTML (Handlebars) und wandelt ihn per
`html-to-pdf`-Node (headless Browser) in A4-PDF. Für die Java-App wurde eine
PDF-Engine benötigt, die das HTML-Template möglichst direkt übernimmt.

## Entscheidung

Das HTML wird serverseitig mit **Thymeleaf** aus einem View-Modell gerendert und mit
**openhtmltopdf** (`openhtmltopdf-pdfbox`) nach PDF konvertiert. Zur Aufbereitung des
Thymeleaf-Outputs zu wohlgeformtem XHTML wird **jsoup** verwendet.

## Begründung

- Reine JVM-Lösung, **kein Browser/Chromium** im Betrieb erforderlich (einfaches
  Deployment als Fat-JAR).
- Thymeleaf ist bereits Teil des Spring-Boot-Starters und ersetzt die
  Handlebars-Templates 1:1 auf Feldebene.
- openhtmltopdf beherrscht das benötigte einfache CSS-Layout (A4, Tabelle,
  Schriftgrößen) zuverlässig.

## Konsequenzen

- Das Template muss XHTML-tauglich sein; jsoup normalisiert Void-Tags
  (`<br/>`, `<hr/>`, `<meta/>`).
- Komplexeres CSS (Flexbox/Grid, Web-Fonts) ist eingeschränkt; für den vorliegenden
  Bericht nicht relevant.
- Alternative „Headless-Browser (Playwright)" wurde verworfen wegen
  Browser-Abhängigkeit im Betrieb.

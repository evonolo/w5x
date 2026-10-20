# Repository-Layout

Monorepo mit zwei Komponenten.

```text
w5x/
  CLAUDE.md                universelles Arbeitsmodell (§1)
  docs/                    autoritatives Projektwissen (Index: docs/README.md)
  .claude/                 Arbeitskonventionen (Profil, Projektregeln)
  backend/                 Spring-Boot-App (Java 21, Gradle Groovy)
    build.gradle
    settings.gradle
    gradlew, gradle/       Gradle-Wrapper (9.x)
    src/main/java/io/grundner/wallbox/
      WallboxReportApplication.java
      domain/              ChargingSession, MonthlyReport
      parser/              WallboxLogParser
      report/              ReportService, ReportView (View-Modell + Formatierung)
      loxone/              LoxoneClient (Abruf der wallbox.log)
      settings/            Settings, SettingsStore (lokale Einstellungsdatei, ADR 0003)
      desktop/             DesktopLauncher, AppIcon (Windows-Desktop-App, ADR 0004)
      pdf/                 PdfRenderer (Thymeleaf -> jsoup -> openhtmltopdf)
      web/                 ReportController, SettingsController, GlobalExceptionHandler, WebConfig (CORS)
    src/main/resources/
      application.yaml     Ablageort der Einstellungsdatei
      templates/report.html Thymeleaf-Berichtsvorlage
    src/test/java/...      Parser-, Report-, Loxone-Client-, Settings-Tests
    src/test/resources/samples/wallbox-sample.log synthetische Beispiel-Logdatei
  installer/windows/       Inno-Setup-Skript, app.ico (ADR 0004)
  .github/workflows/       CI: Windows-Installer x64/ARM64
  frontend/                React + TS + Vite
    src/                   App, API-Client, Komponenten (Settings.tsx: Einstellungen, Help.tsx: Hilfeseite,
                           Icons.tsx: Symbole, index.css: Theme gemäß docs/ui/theme.md)
```

## Namenskonventionen

- Java-Package-Wurzel: `io.grundner.wallbox`.
- Domänenbegriffe folgen `docs/domain/*` (ChargingSession, MonthlyReport, Settings).
- REST-Pfade folgen `docs/api/report-api.md`.

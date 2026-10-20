# Profil: wallbox-report (aktiv)

Dieses Profil ist für die aktuelle Phase autoritativ (siehe `CLAUDE.md` §29).

## Charakter

Kleines, fokussiertes Zwei-Komponenten-Projekt (Java-Backend + React-Frontend),
Nachbau eines Node-RED-Flows. Profil-Typ: **prototype/utility**, kein
Enterprise-Overhead.

## Angewandte Konventionen

- **Dokumentation zuerst** für neue/­geänderte Domänen-, API- oder
  Berichtsverhalten (Mode A). Kleine, eindeutige Änderungen atomar mit Code (Mode B).
- **Proportionale Traceability**: Regel-IDs (`CS-*`, `MR-*`, `AC-*`) aus den
  Domänen-/Feature-Dokumenten in Tests referenzieren, wo sinnvoll. Kein schweres
  Traceability-Werkzeug.
- **Feature-Branches** für jede Änderung, siehe
  [../project/git-workflow.md](../project/git-workflow.md).
- **ADRs** nur für durable Entscheidungen (bisher: Stateless, PDF-Engine,
  lokale Einstellungsdatei).

## Bewusst abwesend

- Keine Datenbank, keine Persistenz von Ladesitzungen (ADR 0001), keine Migrationen.
  Einzig die Einstellungen liegen in einer lokalen JSON-Datei (ADR 0003).
- Keine Authentifizierung/Autorisierung.
- Keine Kostenausweisung im Bericht.
- Keine Mandantenfähigkeit, nur ein Miniserver (keine Standort-Auswahl).

## Technische Baseline

- Backend: Java 21 (Toolchain), Spring Boot 4.x, Gradle (Groovy), Thymeleaf,
  openhtmltopdf, jsoup.
- Frontend: React + TypeScript + Vite, Node 20+.
- Siehe [../project/repository-layout.md](../project/repository-layout.md) und
  [../project/build-and-test.md](../project/build-and-test.md).

## Herkunftsreferenz

Ursprung ist der Node-RED-Flow „Wallbox" (`~/.node-red/flows.json`, Tab
`cb8767695833d1c9`). Er ist Referenz, nicht autoritativ. Autoritativ ist `docs/`.

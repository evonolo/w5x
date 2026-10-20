# Git-Workflow

- `main` (Remote `origin`) enthält den integrierten Stand.
- **Jede Änderung wird auf einem Feature-Branch entwickelt**, nie direkt auf `main`.
  Namensschema: `feature/<kurzer-name>` (z. B. `feature/theme`), abgezweigt vom
  aktuellen `main`.
- Commits entstehen auf dem Feature-Branch. Merge nach `main` und Push nur auf
  ausdrückliche Anweisung.
- Zugangsdaten und lokale Einstellungsdateien werden nie eingecheckt (siehe
  `.gitignore`).

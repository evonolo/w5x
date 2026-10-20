import type { MouseEvent } from 'react'

// Hilfeseite: erklärt die Log-Auswertung (docs/features/help-page.md).
// Inhalte spiegeln docs/domain/log-format.md und docs/domain/charging-session.md
// wider und müssen bei Regeländerungen mit angepasst werden (HP-1).

const SAMPLE_LOG = `1970-01-01 00:00:00;Wallbox Log;System gestartet
1970-01-01 00:00:00;Wallbox Log;2024-01-31 23:55:55:Fahrzeug getrennt;user:-;Geladene Energie:45.54kWh;Dauer:12000 s;54.45€
1970-01-01 00:00:00;Wallbox Log;2024-02-01 05:20:53:Fahrzeug getrennt;user:-;Geladene Energie:66.99kWh;Dauer:18000 s;99.66€
1970-01-01 00:00:00;Wallbox Log;2024-02-02 15:20:53:Fahrzeug getrennt;user:-;Geladene Energie:23.32kWh;Dauer:11000 s;32.23€
1970-01-01 00:00:00;Wallbox Log;2024-03-01 05:20:53:Fahrzeug getrennt;user:-;Geladene Energie:74.47kWh;Dauer:18000 s;47.74€`

const SECTIONS = [
  ['quelle', 'Woher die Daten kommen'],
  ['zeile', 'Aufbau einer Log-Zeile'],
  ['sitzung', 'Von der Zeile zur Ladesitzung'],
  ['bericht', 'Wie der Monatsbericht entsteht'],
  ['beispiel', 'Rechenbeispiel'],
  ['pruefen', 'Ergebnis selbst prüfen'],
  ['probleme', 'Stolperfallen und Fehlermeldungen'],
] as const

export default function Help() {
  return (
    <article className="help">
      <p className="help-back"><a href="#">← Zurück zum Bericht</a></p>

      <h2>Hilfe: Wie die Log-Datei ausgewertet wird</h2>
      <p>
        Der Ladesitzungsbericht wird vollständig aus der Log-Datei <code>wallbox.log</code> der
        Wallbox berechnet. Diese Seite erklärt jeden Schritt so, dass Sie das Ergebnis mit der
        Log-Datei und einem Taschenrechner selbst nachvollziehen können.
      </p>

      <nav className="help-toc" aria-label="Inhalt">
        <ol>
          {SECTIONS.map(([id, title]) => (
            <li key={id}><a href={`#hilfe-${id}`} onClick={(e) => jumpTo(e, id)}>{title}</a></li>
          ))}
        </ol>
      </nav>

      <section id="hilfe-quelle">
        <h3>1. Woher die Daten kommen</h3>
        <p>
          Bei <strong>jedem</strong> Klick auf „Bericht anzeigen“ oder „PDF herunterladen“ holt die
          App die aktuelle <code>wallbox.log</code> direkt von Ihrem Loxone-Miniserver. Die Adresse
          ergibt sich aus der Seriennummer in den <a href="#einstellungen">Einstellungen</a>:{' '}
          <code>https://dns.loxonecloud.com/&lt;Seriennummer&gt;/dev/fsget/log/wallbox.log</code>.
          Die Ladesitzungen werden nicht gespeichert: Der Bericht zeigt immer genau das, was in
          diesem Moment in der Log-Datei steht.
        </p>
        <p>
          Daraus folgt: Der Bericht kann nur Ladesitzungen enthalten, die noch in der Log-Datei
          stehen. Eine Sitzung, die dort nicht (mehr) vorkommt, kann die App nicht anzeigen.
        </p>
      </section>

      <section id="hilfe-zeile">
        <h3>2. Aufbau einer Log-Zeile</h3>
        <p>
          Die Log-Datei besteht aus Zeilen. Die Felder einer Zeile sind durch Semikolons
          (<code>;</code>) getrennt. Eine typische Zeile sieht so aus:
        </p>
        <pre className="log-line">
          <span className="seg seg-ignored">1970-01-01 00:00:00</span>;
          <span className="seg seg-ignored">Wallbox Log</span>;
          <span className="seg seg-stop">2024-01-31 23:55:55:Fahrzeug getrennt</span>;
          <span className="seg seg-user">user:-</span>;
          <span className="seg seg-energy">Geladene Energie:45.54kWh</span>;
          <span className="seg seg-duration">Dauer:12000 s</span>;
          <span className="seg seg-ignored">54.45€</span>
        </pre>

        <table className="help-table">
          <thead>
            <tr><th>Feld</th><th>Beispiel</th><th>Bedeutung</th></tr>
          </thead>
          <tbody>
            <tr>
              <td><span className="swatch seg-stop" />Stop-Zeitpunkt</td>
              <td><code>2024-01-31 23:55:55:Fahrzeug getrennt</code></td>
              <td>
                Datum und Uhrzeit, zu der das Fahrzeug von der Wallbox getrennt wurde – also das
                <strong> Ende</strong> der Ladesitzung. Dies ist das wichtigste Feld.
              </td>
            </tr>
            <tr>
              <td><span className="swatch seg-energy" />Geladene Energie</td>
              <td><code>Geladene Energie:45.54kWh</code></td>
              <td>In dieser Sitzung geladene Energie in Kilowattstunden.</td>
            </tr>
            <tr>
              <td><span className="swatch seg-duration" />Dauer</td>
              <td><code>Dauer:12000 s</code></td>
              <td>Dauer der Sitzung in <strong>Sekunden</strong> (12000 s = 3 h 20 min).</td>
            </tr>
            <tr>
              <td><span className="swatch seg-user" />Nutzer</td>
              <td><code>user:-</code></td>
              <td>Kennung des Nutzers, <code>-</code> wenn keiner hinterlegt ist. Wird im Bericht nicht angezeigt.</td>
            </tr>
            <tr>
              <td><span className="swatch seg-ignored" />Ignoriert</td>
              <td><code>1970-01-01 00:00:00</code>, <code>Wallbox Log</code>, <code>54.45€</code></td>
              <td>
                Das führende Datum (immer <code>1970-01-01 00:00:00</code>) und der Text
                „Wallbox Log“ haben keine Bedeutung. Der Kostenbetrag wird gelesen, aber im Bericht
                bewusst nicht ausgewiesen.
              </td>
            </tr>
          </tbody>
        </table>

        <ul>
          <li>
            Die Felder werden an ihrem <strong>Inhalt</strong> erkannt, nicht an ihrer Position.
            Kommt ein zusätzliches Feld hinzu oder fehlt eines, wird der Rest trotzdem korrekt
            gelesen.
          </li>
          <li>Dezimalzahlen verwenden einen <strong>Punkt</strong> (<code>45.54</code>), kein Komma.</li>
          <li>
            Alle Zeitangaben sind <strong>Ortszeit der Anlage</strong>. Es findet keine
            Zeitzonenumrechnung statt.
          </li>
        </ul>
      </section>

      <section id="hilfe-sitzung">
        <h3>3. Von der Zeile zur Ladesitzung</h3>
        <ol>
          <li>
            <strong>Nur Zeilen mit „Fahrzeug getrennt“ zählen.</strong> Jede solche Zeile ist genau
            eine abgeschlossene Ladesitzung. Alle anderen Zeilen (z. B.
            <code> System gestartet</code>) werden übersprungen.
          </li>
          <li>
            <strong>Stop</strong> ist der Zeitpunkt direkt vor <code>:Fahrzeug getrennt</code>.
          </li>
          <li>
            <strong>Start wird berechnet:</strong> Start = Stop − Dauer. Ein Startzeitpunkt steht
            nicht in der Log-Datei.
            <div className="help-calc">
              Stop 31.01.2024 23:55:55 − 12000 s (3 h 20 min 00 s) = <strong>Start 31.01.2024 20:35:55</strong>
            </div>
          </li>
          <li>
            <strong>Fehlende Angaben machen die Sitzung nicht ungültig.</strong> Fehlt die Energie
            oder die Dauer, zählt der Wert als 0. Die Sitzung erscheint trotzdem im Bericht. Bei
            fehlender Dauer sind Start und Stop dann identisch.
          </li>
        </ol>
      </section>

      <section id="hilfe-bericht">
        <h3>4. Wie der Monatsbericht entsteht</h3>
        <ol>
          <li>
            <strong>Monatszuordnung nach dem Stop-Zeitpunkt.</strong> Eine Sitzung gehört zu dem
            Monat, in dem sie <em>endet</em>. Eine Ladung vom 30.09. abends bis 01.10. morgens
            erscheint deshalb im <strong>Oktober</strong>-Bericht, nicht im September.
          </li>
          <li>
            <strong>Sortierung:</strong> Die Sitzungen werden aufsteigend nach ihrem
            <strong> Start</strong> sortiert.
          </li>
          <li>
            <strong>Summen:</strong> Gesamte Energie und gesamte Ladedauer sind die Summen über
            genau die Sitzungen dieses Monats. Die Anzahl der Sitzungen ist die Anzahl der
            Tabellenzeilen.
          </li>
          <li>
            <strong>Darstellung:</strong>
            <ul>
              <li>Energie mit zwei Nachkommastellen, z. B. <code>45.54</code> kWh.</li>
              <li>
                Dauer als „HH Stunden, mm Minuten, ss Sekunden“. Die Stunden laufen über 24 hinaus
                weiter (z. B. <code>108 Stunden, 39 Minuten, 00 Sekunden</code>) und werden nicht in
                Tage umgerechnet.
              </li>
              <li>Zeitpunkte als <code>TT.MM.JJJJ hh:mm:ss</code> (im PDF).</li>
            </ul>
          </li>
          <li>
            <strong>Sitzungszeilen in der App:</strong> Zur besseren Lesbarkeit zeigt jede Zeile
            Kurzformen: Wochentag und Datum des Starts, die Uhrzeiten ohne Sekunden
            (<code>19:06 → 06:24</code>) mit dem Endtag daneben (z. B. <code>Do 01.10.</code> oder
            „gleicher Tag“) und die Dauer als <code>11 h 17 min</code>. Die Sekunden werden dabei
            abgeschnitten, nicht gerundet. Die exakten Werte erscheinen, wenn Sie mit der Maus auf
            die Zeile zeigen; im PDF stehen sie immer. Der grüne Balken zeigt die Energie im
            Verhältnis zur größten Sitzung des Monats.
          </li>
        </ol>
      </section>

      <section id="hilfe-beispiel">
        <h3>5. Rechenbeispiel (Beispieldaten, Februar 2024)</h3>
        <p>Angenommen, die Log-Datei enthält diese fünf Zeilen (erfundene Beispieldaten):</p>
        <pre className="log-block">{SAMPLE_LOG}</pre>

        <table className="help-table">
          <thead>
            <tr><th>Zeile</th><th>Stop</th><th>Dauer</th><th>Start = Stop − Dauer</th><th>Energie</th><th>Februar 2024?</th></tr>
          </thead>
          <tbody>
            <tr className="excluded">
              <td>1</td><td colSpan={4}>kein „Fahrzeug getrennt“ → keine Sitzung</td><td>–</td>
            </tr>
            <tr className="excluded">
              <td>2</td><td>31.01.2024 23:55:55</td><td>12000 s = 3:20:00</td><td>31.01.2024 20:35:55</td><td>45.54</td><td>nein (endet im Januar)</td>
            </tr>
            <tr>
              <td>3</td><td>01.02.2024 05:20:53</td><td>18000 s = 5:00:00</td><td>01.02.2024 00:20:53</td><td>66.99</td><td><strong>ja</strong></td>
            </tr>
            <tr>
              <td>4</td><td>02.02.2024 15:20:53</td><td>11000 s = 3:03:20</td><td>02.02.2024 12:17:33</td><td>23.32</td><td><strong>ja</strong></td>
            </tr>
            <tr className="excluded">
              <td>5</td><td>01.03.2024 05:20:53</td><td>18000 s = 5:00:00</td><td>01.03.2024 00:20:53</td><td>74.47</td><td>nein (endet im März)</td>
            </tr>
          </tbody>
        </table>

        <p>Für Februar 2024 bleiben die Zeilen 3 und 4:</p>
        <div className="help-calc">
          Energie: 66.99 + 23.32 = <strong>90.31 kWh</strong><br />
          Dauer: 18000 s + 11000 s = 29000 s = 8 × 3600 s + 3 × 60 s + 20 s
          = <strong>08 Stunden, 03 Minuten, 20 Sekunden</strong><br />
          Sitzungen: <strong>2</strong>
        </div>
        <p>
          Genau diese Werte zeigt der Bericht für Februar 2024, wenn die Log-Datei diese fünf Zeilen
          enthält.
        </p>
      </section>

      <section id="hilfe-pruefen">
        <h3>6. Ergebnis selbst prüfen</h3>
        <p>So rechnen Sie einen Bericht von Hand nach:</p>
        <ol>
          <li>
            <strong>Log-Datei öffnen.</strong> Rufen Sie{' '}
            <code>https://dns.loxonecloud.com/&lt;Seriennummer&gt;/dev/fsget/log/wallbox.log</code> im
            Browser auf und melden Sie sich mit Benutzer und Kennwort aus den Einstellungen an.
            Speichern Sie die Datei, damit sich der Inhalt während der Prüfung nicht ändert.
          </li>
          <li>
            <strong>Relevante Zeilen suchen.</strong> Suchen Sie mit der Suchfunktion des
            Texteditors nach <code>Fahrzeug getrennt</code>. Nur diese Zeilen zählen.
          </li>
          <li>
            <strong>Nach Monat filtern.</strong> Maßgeblich ist das Datum <em>unmittelbar vor</em>{' '}
            <code>:Fahrzeug getrennt</code>, <strong>nicht</strong> das <code>1970-01-01</code> am
            Zeilenanfang. Für Oktober 2026 suchen Sie z. B. nach <code>2026-10-</code> direkt vor
            „Fahrzeug getrennt“.
          </li>
          <li>
            <strong>Zählen.</strong> Die Anzahl der gefundenen Zeilen muss der Anzahl der Sitzungen im
            Bericht entsprechen.
          </li>
          <li>
            <strong>Energie addieren.</strong> Addieren Sie die Werte hinter
            <code> Geladene Energie:</code>. Die Summe muss der „Gesamten Energie“ entsprechen.
          </li>
          <li>
            <strong>Dauer addieren.</strong> Addieren Sie die Sekunden hinter <code>Dauer:</code>.
            Umrechnung: Stunden = Summe ÷ 3600 (ganzzahlig), Minuten = Rest ÷ 60 (ganzzahlig),
            Sekunden = verbleibender Rest.
          </li>
          <li>
            <strong>Einzelne Zeile prüfen.</strong> Für eine Sitzung: Stop muss mit dem
            Zeitpunkt aus der Log-Zeile übereinstimmen, Start = Stop − Dauer. Die sekundengenauen
            Werte zeigt der Tooltip der Zeile oder das PDF.
          </li>
        </ol>
        <p>
          Stimmt etwas nicht überein, prüfen Sie zuerst die Stolperfallen unten. Bleibt eine
          Abweichung, halten Sie die betroffene Log-Zeile fest. Mit ihr lässt sich der Fall genau
          nachvollziehen.
        </p>
      </section>

      <section id="hilfe-probleme">
        <h3>7. Stolperfallen und Fehlermeldungen</h3>

        <h4>Typische Abweichungen beim Nachrechnen</h4>
        <dl className="help-faq">
          <dt>Eine Sitzung fehlt im Monat, obwohl sie in diesem Monat begonnen hat.</dt>
          <dd>
            Sitzungen werden dem Monat zugeordnet, in dem sie <strong>enden</strong>. Eine Ladung
            über den Monatswechsel steht im Bericht des Folgemonats.
          </dd>

          <dt>Ich habe nach dem Datum am Zeilenanfang gefiltert.</dt>
          <dd>
            Das führende Datum ist immer <code>1970-01-01 00:00:00</code> und hat keine Bedeutung.
            Maßgeblich ist der Zeitpunkt vor „Fahrzeug getrennt“.
          </dd>

          <dt>Eine Sitzung zeigt 0.00 kWh oder Start = Stop.</dt>
          <dd>
            In der Log-Zeile fehlt die Energie- bzw. Dauerangabe. Die Sitzung wird trotzdem
            gezählt, der fehlende Wert gilt als 0.
          </dd>

          <dt>Die Kosten fehlen im Bericht.</dt>
          <dd>Das ist beabsichtigt. Der Kostenbetrag aus der Log-Datei wird nicht ausgewiesen.</dd>

          <dt>Der Bericht von heute unterscheidet sich von einem früheren.</dt>
          <dd>
            Der Bericht wird jedes Mal neu aus der aktuellen Log-Datei berechnet. Für den laufenden
            Monat kommen neue Sitzungen hinzu. Ändert sich die Log-Datei auf dem Miniserver, ändert
            sich auch der Bericht.
          </dd>

          <dt>Der Bericht zeigt „Keine Ladesitzungen in diesem Zeitraum“.</dt>
          <dd>
            Die Log-Datei wurde erfolgreich gelesen, enthält aber keine Zeile mit „Fahrzeug
            getrennt“, deren Stop-Zeitpunkt im gewählten Monat liegt. Prüfen Sie Monat und Jahr
            und suchen Sie wie oben beschrieben in der Log-Datei.
          </dd>
        </dl>

        <h4>Fehlermeldungen</h4>
        <p>
          Kann die Log-Datei nicht gelesen werden, zeigt die App eine rote Fehlermeldung und
          <strong> keinen</strong> Bericht. Ein leerer Bericht bedeutet also nie, dass der Abruf
          fehlgeschlagen ist.
        </p>
        <table className="help-table">
          <thead>
            <tr><th>Meldung enthält</th><th>Bedeutung</th><th>Was tun</th></tr>
          </thead>
          <tbody>
            <tr>
              <td><code>HTTP 401</code></td>
              <td>Der Miniserver hat Benutzer oder Kennwort abgelehnt.</td>
              <td>Benutzer und Kennwort in den <a href="#einstellungen">Einstellungen</a> prüfen.</td>
            </tr>
            <tr>
              <td><code>HTTP 404</code> (vom Miniserver)</td>
              <td>Die Log-Datei existiert unter der konfigurierten Adresse nicht.</td>
              <td>Seriennummer in den <a href="#einstellungen">Einstellungen</a> prüfen.</td>
            </tr>
            <tr>
              <td>„Abruf der wallbox.log fehlgeschlagen“ ohne HTTP-Code</td>
              <td>Der Miniserver ist nicht erreichbar (offline, Netzwerkproblem).</td>
              <td>Später erneut versuchen, Erreichbarkeit des Miniservers prüfen.</td>
            </tr>
            <tr>
              <td>„Weiterleitung“</td>
              <td>Die Loxone-Cloud hat nicht auf den Miniserver weitergeleitet.</td>
              <td>Seriennummer in den <a href="#einstellungen">Einstellungen</a> und Erreichbarkeit über die Loxone-Cloud prüfen.</td>
            </tr>
            <tr>
              <td>„Einstellungen unvollständig“</td>
              <td>Seriennummer, Benutzer oder Kennwort fehlen.</td>
              <td>Fehlende Angaben in den <a href="#einstellungen">Einstellungen</a> eintragen.</td>
            </tr>
          </tbody>
        </table>
      </section>

      <p className="help-back"><a href="#">← Zurück zum Bericht</a></p>
    </article>
  )
}

// Sprung innerhalb der Seite, ohne den Hash (#hilfe) und damit die Ansicht zu verlassen.
function jumpTo(e: MouseEvent, id: string) {
  e.preventDefault()
  document.getElementById(`hilfe-${id}`)?.scrollIntoView({ behavior: 'smooth' })
}

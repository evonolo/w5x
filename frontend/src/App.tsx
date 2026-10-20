import { useEffect, useMemo, useState } from 'react'
import type { ReportView, SettingsView } from './types'
import { fetchReport, fetchSettings, reportPdfUrl } from './api'
import Help from './Help'
import Settings from './Settings'
import { IconBolt, IconClock, IconDownload, IconHelp, IconPlug, IconReport, IconSettings } from './Icons'

const MONTHS = [
  'Januar', 'Februar', 'März', 'April', 'Mai', 'Juni',
  'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember',
]

type View = 'report' | 'settings' | 'help'

// Ansichten per Hash: #einstellungen (docs/features/settings.md),
// #hilfe bzw. #hilfe-* (docs/features/help-page.md), sonst Bericht.
function viewFromHash(): View {
  const hash = window.location.hash
  if (hash.startsWith('#hilfe')) return 'help'
  if (hash === '#einstellungen') return 'settings'
  return 'report'
}

export default function App() {
  const [view, setView] = useState<View>(viewFromHash)
  const [settings, setSettings] = useState<SettingsView | null>(null)
  const now = useMemo(() => new Date(), [])
  const [year, setYear] = useState<number>(now.getFullYear())
  const [month, setMonth] = useState<number>(now.getMonth() + 1)

  const [report, setReport] = useState<ReportView | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const onHashChange = () => {
      setView(viewFromHash())
      window.scrollTo(0, 0)
    }
    window.addEventListener('hashchange', onHashChange)
    return () => window.removeEventListener('hashchange', onHashChange)
  }, [])

  useEffect(() => {
    fetchSettings()
      .then(setSettings)
      .catch((e) => setError(e.message))
  }, [])

  const years = useMemo(() => {
    const y = now.getFullYear()
    return Array.from({ length: 8 }, (_, i) => y - i)
  }, [now])

  const ready = settings?.complete ?? false

  async function loadReport() {
    setLoading(true)
    setError(null)
    try {
      setReport(await fetchReport(year, month))
    } catch (e) {
      setReport(null)
      setError((e as Error).message)
    } finally {
      setLoading(false)
    }
  }

  function downloadPdf() {
    window.open(reportPdfUrl(year, month), '_blank')
  }

  function onSettingsSaved(saved: SettingsView) {
    setSettings(saved)
    // Bericht kann sich durch neue Verbindung/Kopfdaten ändern
    setReport(null)
    setError(null)
  }

  return (
    <div className="page">
      <header className="app-bar">
        <div className="brand">
          <span className="brand-mark"><IconBolt /></span>
          <div>
            <h1>Ladesitzungsbericht</h1>
            <p className="subtitle">Wallbox-Auswertung pro Monat</p>
          </div>
        </div>
        <nav className="app-nav" aria-label="Hauptnavigation">
          <a href="#" className={view === 'report' ? 'active' : ''}><IconReport /><span>Bericht</span></a>
          <a href="#einstellungen" className={view === 'settings' ? 'active' : ''}><IconSettings /><span>Einstellungen</span></a>
          <a href="#hilfe" className={view === 'help' ? 'active' : ''}><IconHelp /><span>Hilfe</span></a>
        </nav>
      </header>

      {view === 'help' && <Help />}

      {view === 'settings' && settings && <Settings settings={settings} onSaved={onSettingsSaved} />}

      {view === 'report' && (
        <>
          {settings && !ready && (
            <div className="notice">
              Bevor ein Bericht erstellt werden kann, tragen Sie bitte Seriennummer, Benutzer und
              Kennwort Ihres Miniservers in den <a href="#einstellungen">Einstellungen</a> ein.
            </div>
          )}

          <section className="card controls">
            <label>
              Monat
              <select value={month} onChange={(e) => setMonth(Number(e.target.value))}>
                {MONTHS.map((name, i) => (
                  <option key={i + 1} value={i + 1}>{name}</option>
                ))}
              </select>
            </label>

            <label>
              Jahr
              <select value={year} onChange={(e) => setYear(Number(e.target.value))}>
                {years.map((y) => (
                  <option key={y} value={y}>{y}</option>
                ))}
              </select>
            </label>

            <div className="actions">
              <button onClick={loadReport} disabled={!ready || loading}>
                {loading ? 'Lädt …' : 'Bericht anzeigen'}
              </button>
              <button className="secondary" onClick={downloadPdf} disabled={!ready}>
                <IconDownload /> PDF
              </button>
            </div>
          </section>

          {error && <div className="error">{error}</div>}

          {report && !error && <Report report={report} />}
        </>
      )}
    </div>
  )
}

function Report({ report }: { report: ReportView }) {
  return (
    <section className="report">
      <div className="report-head">
        <h2>{report.location || 'Wallbox'}</h2>
        <div className="muted">
          {report.wallbox && <>Wallbox {report.wallbox} · </>}
          {MONTHS[report.month - 1]} {report.year} · erstellt {report.generatedDate}, {report.generatedTime} Uhr
        </div>
      </div>

      <dl className="tiles">
        <div className="tile">
          <dt><IconPlug /> Sitzungen</dt>
          <dd>{report.sessionCount}</dd>
        </div>
        <div className="tile">
          <dt><IconBolt /> Gesamte Energie</dt>
          <dd>{report.totalEnergyKwh}<small>kWh</small></dd>
        </div>
        <div className="tile">
          <dt><IconClock /> Gesamte Ladedauer</dt>
          <dd className="tile-duration">{report.totalDurationFormatted}</dd>
        </div>
      </dl>

      {report.sessions.length === 0 ? (
        <p className="card empty">Keine Ladesitzungen in diesem Zeitraum.</p>
      ) : (
        <div className="card sessions">
          <table>
            <thead>
              <tr>
                <th>Energie</th>
                <th>Dauer</th>
                <th>Zeitraum</th>
              </tr>
            </thead>
            <tbody>
              {report.sessions.map((s, i) => (
                <tr key={i}>
                  <td className="energy" data-label="Energie">{s.energyKwh} kWh</td>
                  <td data-label="Dauer">{s.durationFormatted}</td>
                  <td data-label="Zeitraum">{s.start} – {s.stop}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

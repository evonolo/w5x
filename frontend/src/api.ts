import type { ApiError, ReportView, SettingsUpdate, SettingsView } from './types'

async function handle<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let message = `Fehler ${response.status}`
    try {
      const body = (await response.json()) as ApiError
      if (body?.message) message = body.message
    } catch {
      // keine JSON-Fehlermeldung verfügbar
    }
    throw new Error(message)
  }
  return (await response.json()) as T
}

export function fetchSettings(): Promise<SettingsView> {
  return fetch('/api/settings').then((r) => handle<SettingsView>(r))
}

export function saveSettings(settings: SettingsUpdate): Promise<SettingsView> {
  return fetch('/api/settings', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(settings),
  }).then((r) => handle<SettingsView>(r))
}

export function fetchReport(year: number, month: number): Promise<ReportView> {
  const params = new URLSearchParams({
    year: String(year),
    month: String(month),
  })
  return fetch(`/api/report?${params}`).then((r) => handle<ReportView>(r))
}

export function reportPdfUrl(year: number, month: number): string {
  const params = new URLSearchParams({
    year: String(year),
    month: String(month),
  })
  return `/api/report/pdf?${params}`
}

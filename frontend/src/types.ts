// Spiegelt das API-View-Modell aus docs/api/report-api.md.

export interface SettingsView {
  serialNumber: string
  username: string
  passwordSet: boolean
  locationLabel: string
  wallboxId: string
  complete: boolean
}

// password leer = gespeichertes Kennwort beibehalten (ST-5)
export interface SettingsUpdate {
  serialNumber: string
  username: string
  password: string
  locationLabel: string
  wallboxId: string
}

export interface SessionView {
  energyKwh: string
  durationFormatted: string
  start: string
  stop: string
  user: string
}

export interface ReportView {
  location: string
  wallbox: string
  year: number
  month: number
  generatedDate: string
  generatedTime: string
  totalEnergyKwh: string
  totalDurationFormatted: string
  sessionCount: number
  sessions: SessionView[]
}

export interface ApiError {
  status: number
  error: string
  message: string
}

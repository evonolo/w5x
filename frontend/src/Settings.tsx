import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SettingsUpdate, SettingsView } from './types'
import { saveSettings } from './api'

// Einstellungen der einen Wallbox-Installation (docs/features/settings.md).
// Das Kennwort wird nie angezeigt; leer lassen behält das gespeicherte (ST-4, ST-5).

interface Props {
  settings: SettingsView
  onSaved: (saved: SettingsView) => void
}

export default function Settings({ settings, onSaved }: Props) {
  const [form, setForm] = useState<SettingsUpdate>({
    serialNumber: settings.serialNumber,
    username: settings.username,
    password: '',
    locationLabel: settings.locationLabel,
    wallboxId: settings.wallboxId,
  })
  const [passwordSet, setPasswordSet] = useState(settings.passwordSet)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState<{ ok: boolean; text: string } | null>(null)

  function field(name: keyof SettingsUpdate) {
    return {
      value: form[name],
      onChange: (e: { target: { value: string } }) => {
        const value = e.target.value
        setForm((current) => ({ ...current, [name]: value }))
      },
    }
  }

  async function submit(e: FormEvent) {
    e.preventDefault()
    setSaving(true)
    setMessage(null)
    try {
      const saved = await saveSettings(form)
      setForm({ ...form, password: '' })
      setPasswordSet(saved.passwordSet)
      setMessage({
        ok: true,
        text: saved.complete
          ? 'Gespeichert.'
          : 'Gespeichert. Für einen Bericht fehlen noch Seriennummer, Benutzer oder Kennwort.',
      })
      onSaved(saved)
    } catch (err) {
      setMessage({ ok: false, text: (err as Error).message })
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="settings" onSubmit={submit}>
      <h2>Einstellungen</h2>

      <fieldset>
        <legend>Verbindung zum Miniserver</legend>
        <label>
          Miniserver-Seriennummer *
          <input {...field('serialNumber')} placeholder="z. B. 504F94A22F32" autoComplete="off" />
        </label>
        <label>
          Benutzer *
          <input {...field('username')} autoComplete="username" />
        </label>
        <label>
          Kennwort *
          <input
            type="password"
            {...field('password')}
            placeholder={passwordSet ? 'gespeichert – leer lassen, um es beizubehalten' : ''}
            autoComplete="new-password"
          />
        </label>
      </fieldset>

      <fieldset>
        <legend>Angaben im Berichtskopf</legend>
        <label>
          Standortbezeichnung
          <input {...field('locationLabel')} placeholder="z. B. Neuhausen 22, 6391 Fieberbrunn" />
        </label>
        <label>
          Wallbox-ID
          <input {...field('wallboxId')} />
        </label>
      </fieldset>

      <p className="muted">* Pflichtfelder für den Abruf der Log-Datei.</p>

      <div className="settings-actions">
        <button type="submit" disabled={saving}>{saving ? 'Speichert …' : 'Speichern'}</button>
        {message && <span className={message.ok ? 'success' : 'error-inline'}>{message.text}</span>}
      </div>
    </form>
  )
}

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Der Dev-Server proxyt /api auf das Spring-Boot-Backend (Port 8080),
// damit Berichte und PDF-Download ohne CORS-Sonderfall funktionieren.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    // fester Port: das Backend erlaubt CORS nur für diesen Origin
    strictPort: true,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})

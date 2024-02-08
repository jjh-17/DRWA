import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Quasar } from 'quasar'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// Import icon libraries
import '@quasar/extras/material-icons/material-icons.css'

// Import Quasar css
import 'quasar/dist/quasar.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia().use(piniaPluginPersistedstate))
app.use(router)
app.use(Quasar, {
  config: {}, // import Quasar config here
  plugins: {} // import Quasar plugins here
})

app.mount('#app')

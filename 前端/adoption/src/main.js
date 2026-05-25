import './assets/main.scss'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createApp } from 'vue'
import App from './App.vue'
import locale from 'element-plus/dist/locale/zh-cn.js'
import router from '@/router'
import {createPinia} from 'pinia'
import { createPersistedState } from 'pinia-persistedstate-plugin'

const persist= createPersistedState()
const app = createApp(App)
const pinia = createPinia()
pinia.use(persist)
app.use(ElementPlus,{locale})
app.use(router)
app.use(pinia)
app.mount('#app')


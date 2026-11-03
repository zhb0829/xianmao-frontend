import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import './style.css'
import './styles/cl.css'
import router from './router/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'animate.css/animate.min.css'

// 统一请求封装：token 注入、baseURL、错误提示都在 src/api/request.js 里
import http, { setRequestToken } from './api/request.js'
import { useUserStore } from './stores/user.js'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia).use(router).use(ElementPlus)

// 从 localStorage 恢复登录态后同步给请求层（token 唯一来源：userStore）
const userStore = useUserStore(pinia)
setRequestToken(userStore.token)

// 全局挂载：页面统一用 this.$http / this.$router
app.config.globalProperties.$http = http
app.config.globalProperties.$router = router
app.config.globalProperties.$userStore = userStore

app.mount('#app')
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
import { resetMock } from './mock/index.js'

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

// 调试/演示用：浏览器控制台可直接调接口，例如
// window.__xmHttp({ method: 'get', url: '/commodity/getCommodity' })
// 需要恢复初始数据时执行：window.__xmResetMock()
if (typeof window !== 'undefined') {
  window.__xmHttp = http
  window.__xmRouter = router
  window.__xmResetMock = resetMock
}

app.mount('#app')
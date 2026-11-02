// import { createApp } from 'vue'
// import App from './App.vue'
// import './style.css'
// import router from './router/index.js'
// import ElementPlus from "element-plus"
// import "element-plus/dist/inimport "
// import "animate.css/animate.min.css";
import { createApp } from 'vue'
import App from './App.vue'
import './style.css'
import './styles/cl.css'
import router from './router/index.js'
import ElementPlus from "element-plus"
import "element-plus/dist/index.css"
import "animate.css/animate.min.css"

// 引入axios
import axios from 'axios'
// JS
// axios.defaults.baseURL = "http://172.23.7.207:9080/api";

// 统一使用绝对路径前缀：相对路径 "api/" 在 /shop 等子路由下会被解析成 /shop/api/...，
// 导致请求无法命中 /api 代理或演示 Mock
axios.defaults.baseURL = "/api/";

// axios.defaults.baseURL = "http://localhost:9080";

// DYF
// axios.defaults.baseURL = "http://172.23.44.167:9080/api";
// axios.defaults.timeout = 10000;

// axios.defaults.baseURL = "http://121.40.160.119:9080/api";

const app = createApp(App)
app.use(router).use(ElementPlus)
// 注册vue全局变量来使用axios
// interceptors拦截器
axios.interceptors.request.use(
    (config) => {
        // 请求头携带localStorage里面的token
        // config.headers.Authorization = localStorage.getItem('token')
        config.headers.token = localStorage.getItem('token')
        return config
    },
    function (err) {
        // 对请求错误做些什么
    }
)
app.config.globalProperties.$http = axios
app.config.globalProperties.$router = router
app.mount('#app')
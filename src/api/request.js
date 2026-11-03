// 统一请求封装
// 1) 全部页面通过本实例发请求（main.js 挂到 this.$http），不再各自 new axios
// 2) token 只在这里注入请求头；store 通过 setRequestToken 同步，页面不直接读写 localStorage
// 3) 响应错误统一转成带 status / message 的对象，页面 catch 后直接提示
import axios from 'axios'

export const TOKEN_KEY = 'token'
export const USER_KEY = 'xm:user'

// 模块级 token：刷新页面时从 localStorage 恢复一次（store 初始化也读同一份）
let currentToken = ''
try {
  currentToken = localStorage.getItem(TOKEN_KEY) || ''
} catch {
  currentToken = ''
}

export function getToken() {
  return currentToken
}

export function setRequestToken(token) {
  currentToken = token || ''
  try {
    if (currentToken) localStorage.setItem(TOKEN_KEY, currentToken)
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    /* 隐私模式 / 配额溢出时忽略，不影响本次会话 */
  }
}

const http = axios.create({
  baseURL: '/api/',
  timeout: 15000,
})

http.interceptors.request.use(
  config => {
    if (currentToken) config.headers.token = currentToken
    return config
  },
  error => Promise.reject(error),
)

http.interceptors.response.use(
  res => res,
  error => {
    if (!error.response) {
      error.friendly = '网络异常，请检查开发服务器是否启动'
    } else if (error.response.status === 500) {
      error.friendly = (error.response.data && error.response.data.error) || '服务器开小差了'
    } else if (error.response.status === 404) {
      error.friendly = '接口不存在（' + (error.config && error.config.url) + '）'
    } else {
      error.friendly = '请求失败（' + error.response.status + '）'
    }
    return Promise.reject(error)
  },
)

export default http
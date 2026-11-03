// 统一请求封装
// 1) 全部页面通过本实例发请求（main.js 挂到 this.$http），不再各自 new axios
// 2) token 只在这里注入请求头；store 通过 setRequestToken 同步，页面不直接读写 localStorage
// 3) 请求由浏览器内置 Mock（src/mock）接管：纯前端即可完整演示，无需后端
import axios from 'axios'
import { handleRequest } from '../mock/index.js'

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

// 纯前端演示：请求不真正发到网络，交给浏览器内置 Mock 处理
// 这样 `npm run dev` 与 `npm run build` 的产物行为一致，答辩时不必启动后端
http.defaults.adapter = async function mockAdapter(config) {
  const method = String(config.method || 'get').toUpperCase()
  const headers = config.headers || {}
  const token = typeof headers.get === 'function' ? headers.get('token') : headers.token
  const res = await handleRequest({
    method,
    url: config.url || '',
    params: config.params || {},
    data: config.data,
    headers: token ? { token } : {},
  })
  const response = {
    data: res.data,
    status: res.status,
    statusText: res.status === 200 ? 'OK' : 'ERROR',
    headers: {},
    config,
    request: {},
  }
  if (res.status >= 400) {
    const err = new Error((res.data && res.data.error) || '请求失败')
    err.config = config
    err.response = response
    throw err
  }
  return response
}

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
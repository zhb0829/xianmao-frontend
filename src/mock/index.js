// 浏览器内置 Mock：所有 /api 请求都由本模块接管（axios adapter 调用），
// 不启动后端、也不依赖 Vite 开发服务器，因此 `npm run dev` 与 `npm run build` 的产物都能完整演示。
import { createRoutes } from './routes.js'
import { loadImages } from './images.js'

let routes = null
let initPromise = null

function init() {
  if (initPromise) return initPromise
  initPromise = loadImages().then(() => {
    routes = createRoutes()
    console.info(`[mock] 浏览器内置 Mock 已启用，共 ${routes.length} 个接口`)
  })
  return initPromise
}

function normalizePath(raw) {
  let p = String(raw || '')
  p = p.replace(/^https?:\/\/[^/]+/i, '') // 去掉域名
  p = p.split('?')[0] // 去掉查询串（参数在 config.params 里）
  if (p.startsWith('/api')) p = p.slice(4)
  if (!p.startsWith('/')) p = '/' + p
  if (p.length > 1 && p.endsWith('/')) p = p.slice(0, -1)
  return p
}

// axios 的 data 可能是 FormData / JSON 字符串 / 普通对象
function toPlainBody(data) {
  if (!data) return {}
  if (typeof FormData !== 'undefined' && data instanceof FormData) {
    const obj = {}
    for (const [k, v] of data.entries()) {
      if (typeof File !== 'undefined' && v instanceof File) {
        obj[k] = '' // 演示模式不落盘，文件字段忽略
      } else {
        obj[k] = v
      }
    }
    return obj
  }
  if (typeof data === 'string') {
    try {
      return JSON.parse(data)
    } catch {
      return {}
    }
  }
  return data
}

/**
 * 处理一次 /api 请求
 * @param {{method:string, url:string, params?:object, data?:any, headers?:object}} req
 * @returns {Promise<{status:number, data:any}>}
 */
export async function handleRequest({ method, url, params, data, headers }) {
  await init()
  const pathname = normalizePath(url)
  const query = { ...(params || {}) }
  const body = toPlainBody(data)
  const upper = String(method || 'GET').toUpperCase()

  const route =
    routes.find(r => r.method === upper && r.path === pathname) ||
    routes.find(r => r.path === pathname)

  if (!route) {
    console.warn(`[mock] 未匹配 ${upper} ${pathname}，返回 false`)
    return { status: 200, data: false }
  }

  try {
    const result = await route.handler({ query, body, headers: headers || {} })
    return { status: 200, data: result }
  } catch (e) {
    const status = e && e.status ? e.status : 500
    console.warn(`[mock] ${upper} ${pathname} -> ${status} ${e.message}`)
    return { status, data: { error: e.message } }
  }
}
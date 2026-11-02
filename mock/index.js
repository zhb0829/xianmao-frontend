// Vite 插件：开发环境 Mock 服务
// 在 configureServer 阶段注册 /api 中间件，优先于 Vite 内置 proxy 执行，
// 因此 npm run dev 时无需启动 Spring Boot 也能完整演示。
import { createRoutes } from './routes.js'
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const demoDir = path.resolve(__dirname, 'assets', 'demo')

function readBody(req) {
  return new Promise(resolve => {
    const chunks = []
    req.on('data', c => chunks.push(c))
    req.on('end', () => resolve(Buffer.concat(chunks)))
    req.on('error', () => resolve(Buffer.alloc(0)))
  })
}

function parseMultipart(raw, contentType) {
  const result = {}
  try {
    const m = /boundary=(?:"([^"]+)"|([^;]+))/i.exec(contentType || '')
    const boundary = m ? (m[1] || m[2]) : ''
    if (!boundary) return result
    const text = raw.toString('latin1')
    const parts = text.split('--' + boundary)
    for (const part of parts) {
      const nameMatch = /name="([^"]+)"/i.exec(part)
      if (!nameMatch) continue
      const sep = part.indexOf('\r\n\r\n')
      if (sep < 0) continue
      let value = part.slice(sep + 4)
      value = value.replace(/\r\n$/, '')
      // part 是按 latin1 切分的，先还原为字节再按 UTF-8 解码，避免中文乱码
      value = Buffer.from(value, 'latin1').toString('utf8')
      result[nameMatch[1]] = value
    }
  } catch {
    /* 忽略解析失败 */
  }
  return result
}

async function parseBody(req) {
  const raw = await readBody(req)
  if (!raw.length) return {}
  const ct = String(req.headers['content-type'] || '').toLowerCase()
  const text = raw.toString('utf8')
  if (ct.includes('application/json')) {
    try { return JSON.parse(text) } catch { return {} }
  }
  if (ct.includes('application/x-www-form-urlencoded')) {
    return Object.fromEntries(new URLSearchParams(text))
  }
  if (ct.includes('multipart/form-data')) {
    return parseMultipart(raw, req.headers['content-type'])
  }
  try { return JSON.parse(text) } catch { return {} }
}

export function mockServer() {
  return {
    name: 'xianmao-demo-mock',
    apply: 'serve',
    configureServer(server) {
      const routes = createRoutes()
      let hit = 0
      let miss = 0

      // 演示图片托管：/demo/* 从 mock/assets/demo 读取，不占用 public 目录，
      // 因此生产构建产物不会带上演示图片
      server.middlewares.use('/demo', (req, res, next) => {
        const url = new URL(req.url, 'http://localhost')
        let rel = decodeURIComponent(url.pathname)
        if (rel.startsWith('/demo')) rel = rel.slice(5)
        if (!rel.startsWith('/')) rel = '/' + rel
        const file = path.join(demoDir, rel)
        if (!file.startsWith(demoDir) || !fs.existsSync(file) || !fs.statSync(file).isFile()) {
          res.statusCode = 404
          res.end('not found')
          return
        }
        const ext = path.extname(file).toLowerCase()
        const type = ext === '.jpg' || ext === '.jpeg' ? 'image/jpeg'
          : ext === '.png' ? 'image/png'
            : ext === '.svg' ? 'image/svg+xml' : 'application/octet-stream'
        res.setHeader('Content-Type', type)
        res.setHeader('Cache-Control', 'public, max-age=60')
        fs.createReadStream(file).pipe(res)
      })

      server.middlewares.use('/api', async (req, res) => {
        // connect 不会剥离挂载前缀，需要手动去掉 '/api'
        const url = new URL(req.url, 'http://localhost')
        let pathname = url.pathname
        if (pathname.startsWith('/api')) pathname = pathname.slice(4)
        if (!pathname.startsWith('/')) pathname = '/' + pathname
        if (pathname.length > 1 && pathname.endsWith('/')) pathname = pathname.slice(0, -1)

        const query = Object.fromEntries(url.searchParams.entries())
        let body = {}
        try { body = await parseBody(req) } catch { body = {} }

        const ctx = { query, body, headers: req.headers }
        const method = String(req.method || 'GET').toUpperCase()
        const route = routes.find(r => r.method === method && r.path === pathname)
          || routes.find(r => r.path === pathname)

        const send = (status, payload) => {
          res.statusCode = status
          res.setHeader('Content-Type', 'application/json; charset=utf-8')
          res.setHeader('Cache-Control', 'no-store')
          res.end(JSON.stringify(payload))
        }

        if (!route) {
          miss++
          console.log(`[mock] 未匹配 ${method} ${pathname} -> 返回 false`)
          return send(200, false)
        }

        try {
          const data = await route.handler(ctx)
          hit++
          console.log(`[mock] ${method} ${pathname} -> 200`)
          send(200, data)
        } catch (e) {
          const status = e && e.status ? e.status : 500
          console.log(`[mock] ${method} ${pathname} -> ${status} ${e.message}`)
          send(status, { error: e.message })
        }
      })

      console.log(`[mock] 演示模式已启用，已注册 ${routes.length} 个接口（/api/*）`)
    },
  }
}
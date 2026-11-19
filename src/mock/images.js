// 演示图片：public/demo 下的静态资源，Mock 启动时转成 base64 供接口层使用。
// 这样无论 `npm run dev` 还是 `npm run build` 的产物，都能完整演示（不依赖后端与 Vite 中间件）。
const BASE = import.meta.env.BASE_URL || '/'
const GOODS_IDS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
const AVATAR_KEY = 'avatar'

const cache = new Map()
let loaded = false
let loading = null

function arrayBufferToBase64(buffer) {
  const bytes = new Uint8Array(buffer)
  let binary = ''
  const chunk = 0x8000
  for (let i = 0; i < bytes.length; i += chunk) {
    binary += String.fromCharCode.apply(null, bytes.subarray(i, i + chunk))
  }
  return btoa(binary)
}

async function loadOne(key, url) {
  try {
    const res = await fetch(url)
    if (!res.ok) throw new Error('图片加载失败：' + url)
    cache.set(key, arrayBufferToBase64(await res.arrayBuffer()))
  } catch {
    cache.set(key, '')
  }
}

export function loadImages() {
  if (loaded) return Promise.resolve()
  if (loading) return loading
  loading = Promise.all([
    ...GOODS_IDS.map(id => loadOne('goods:' + id, `${BASE}demo/goods/${id}.jpg`)),
    loadOne(AVATAR_KEY, `${BASE}demo/avatar.jpg`),
  ]).then(() => {
    // 个别序号缺图时兜底，避免商品卡片空白
    if (!cache.get('goods:1')) cache.set('goods:1', cache.get('goods:2') || '')
    loaded = true
  })
  return loading
}

export function goodsBase64(img) {
  const key = 'goods:' + (Number(img) || 1)
  return cache.get(key) || cache.get('goods:1') || ''
}

export function avatarBase64() {
  return cache.get(AVATAR_KEY) || ''
}
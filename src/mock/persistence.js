// Mock 状态持久化：把内存状态保存到 localStorage，刷新页面后恢复。
//
// 说明：
// 1) 种子商品不含图片（img 是序号，图片由 images.js 提供），所以常规演示体积很小；
// 2) 用户上传的商品带 base64 图片，可能超过 localStorage 配额，保存时逐级降级：
//    完整保存 → 丢弃商品图片 → 只保核心数据 → 放弃（控制台提示）。
// 3) tokens 不持久化：登录态由 localStorage 的 token 恢复，账号表已持久化，userOf 能解析出账号。
const KEY = 'xm:mock-state:v1'
const VERSION = 1

const LIST_FIELDS = [
  'accounts',
  'profiles',
  'goods',
  'orders',
  'sells',
  'comments',
  'reviews',
  'categories',
]
const COUNTER_FIELDS = [
  'nextGoodsId',
  'nextOrderId',
  'nextUserId',
  'nextCommentId',
  'nextReviewId',
]

export function serializeState(state) {
  const data = {
    version: VERSION,
    savedAt: Date.now(),
  }
  for (const key of LIST_FIELDS) data[key] = state[key]
  for (const key of COUNTER_FIELDS) data[key] = state[key]
  // Map / Set 转数组
  data.liked = [...state.liked.entries()].map(([userId, ids]) => [userId, [...ids]])
  data.likeCount = [...state.likeCount.entries()]
  return data
}

export function loadSnapshot() {
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) return null
    const data = JSON.parse(raw)
    if (!data || data.version !== VERSION) return null
    return data
  } catch {
    return null
  }
}

export function applySnapshot(state, snapshot) {
  if (!snapshot) return false
  for (const key of LIST_FIELDS) {
    if (snapshot[key] !== undefined) state[key] = snapshot[key]
  }
  for (const key of COUNTER_FIELDS) {
    if (typeof snapshot[key] === 'number') state[key] = snapshot[key]
  }
  if (Array.isArray(snapshot.liked)) {
    state.liked = new Map(snapshot.liked.map(([userId, ids]) => [userId, new Set(ids)]))
  }
  if (Array.isArray(snapshot.likeCount)) {
    state.likeCount = new Map(snapshot.likeCount)
  }
  return true
}

function trySave(data) {
  try {
    localStorage.setItem(KEY, JSON.stringify(data))
    return true
  } catch {
    return false
  }
}

/**
 * 保存状态；遇到配额溢出时逐级降级
 * @returns {'full'|'no-images'|'core'|'failed'}
 */
export function saveState(state) {
  const snapshot = serializeState(state)

  if (trySave(snapshot)) return 'full'

  // 降级：丢弃商品图片（上传的 base64），保留所有文字数据
  const slim = {
    ...snapshot,
    goods: snapshot.goods.map(g => ({ ...g, photos: [] })),
  }
  if (trySave(slim)) {
    console.warn('[mock] 上传图片过大，已降级保存：刷新后商品文字数据保留、图片不保留')
    return 'no-images'
  }

  // 再降级：只保留核心业务数据
  const core = {
    version: VERSION,
    savedAt: Date.now(),
    goods: slim.goods,
    orders: snapshot.orders,
    comments: snapshot.comments,
    reviews: snapshot.reviews,
    categories: snapshot.categories,
    liked: snapshot.liked,
    likeCount: snapshot.likeCount,
  }
  if (trySave(core)) {
    console.warn('[mock] 存储空间不足，仅保存核心数据')
    return 'core'
  }

  console.warn('[mock] 浏览器存储不可用，演示数据不会被保留')
  return 'failed'
}

export function clearSnapshot() {
  try {
    localStorage.removeItem(KEY)
  } catch {
    /* 忽略 */
  }
}

export function snapshotSize() {
  try {
    const raw = localStorage.getItem(KEY)
    return raw ? raw.length : 0
  } catch {
    return 0
  }
}
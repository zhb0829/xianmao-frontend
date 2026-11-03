// 接口路由表：按前端实际调用的端点与字段结构返回演示数据
// 目标：不启动 Spring Boot / MySQL 时，前端每个按钮都能走通一条完整业务链路：
//   发布 -> 管理端审核 -> 商品市场 -> 下单 -> 支付 -> 买卖列表 -> 收货/发货 -> 收藏 -> 设置 -> 注册登录
import {
  categories, accounts, seedGoods, seedBought, seedSold, defaultProfile,
  GOODS_STATUS,
} from './data.js'
import { goodsBase64, avatarBase64 } from './images.js'

// 运行时状态：只存在浏览器内存中，刷新页面会重置为初始数据
export const state = {
  // token -> 用户
  tokens: new Map(),
  // 账号表（注册会写入这里）
  accounts: { ...accounts },
  // userId -> 个人资料
  profiles: {
    1001: { ...defaultProfile },
    1002: { ...defaultProfile, nickname: '管理员' },
  },
  // 商品库：唯一数据源，市场 / 我发布的 / 管理端都从这里派生
  goods: seedGoods.map(g => ({ ...g, photos: [] })),
  // 买到的订单
  orders: seedBought.map(o => ({ ...o })),
  // 卖出的订单
  sells: seedSold.map(o => ({ ...o })),
  // 当前用户收藏的商品 id：userId -> Set<commodityId>
  liked: new Map([[1001, new Set([1, 3, 5])]]),
  // 商品收藏数（含别人的收藏）
  likeCount: new Map(seedGoods.map(g => [g.id, 3 + (g.id % 7)])),
  // 商品留言：作者可以是任何账号，商品 10 / 12 的卖家是 demo，
  // 因此演示账号在「个人中心-消息留言」能看到收到的留言
  comments: [
    { id: 1, commodityId: 10, authorId: 2001, authorName: '张同学', content: '零食大礼包还能便宜点吗？我就在东校园。', time: '2026-11-01 09:20' },
    { id: 2, commodityId: 10, authorId: 2002, authorName: '李同学', content: '还在吗？今晚可以当面拿，现金或转账都行。', time: '2026-11-01 12:02' },
    { id: 3, commodityId: 12, authorId: 2003, authorName: '王同学', content: '明天下午有货吗？想订一个，低糖的最好。', time: '2026-11-02 08:45' },
    { id: 4, commodityId: 1, authorId: 1001, authorName: '演示同学', content: '能小刀吗？6000 可以的话我今晚面交。', time: '2026-11-02 15:10' },
    { id: 5, commodityId: 14, authorId: 1001, authorName: '演示同学', content: '冰箱用了多久？压缩机声音大不大？', time: '2026-11-02 19:33' },
  ],
  nextCommentId: 100,
  // 分类可在管理端增删改，用可变副本（初始值来自 data.js）
  categories: [...categories],
  // 商品评价：订单完成后买家提交，商品详情页展示
  reviews: [
    { id: 1, orderId: 9102, commodityId: 8, score: 5, content: '书很新，价格也合适，面交很顺利。', authorName: '演示同学', time: '2026-10-29 10:20' },
    { id: 2, orderId: 9201, commodityId: 1, score: 4, content: '手机成色和描述一致，卖家很好说话。', authorName: '陈同学', time: '2026-10-30 18:40' },
  ],
  nextReviewId: 100,
  nextGoodsId: 100,
  nextOrderId: 10000,
  nextUserId: 3000,
}

// 前端统一按 'data:image/Jpeg;base64,' + 纯 base64 拼接，这里返回纯 base64
function readBase64(img) {
  return goodsBase64(img)
}

// 头像：所有演示卖家共用一个头像
function readAvatar() {
  return avatarBase64()
}

// 前端 FileReader 上传的是完整 data URI，存库时只保留 base64 部分
function stripDataUri(value) {
  return typeof value === 'string' ? value.replace(/^data:[^;,]+;base64,/, '') : ''
}

function pad(n) {
  return String(n).padStart(2, '0')
}

function nowStr() {
  const d = new Date()
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

// 取参数：query 优先，其次 body（前端个别请求 method 写错成 methods，靠 query 兜底）
function val(ctx, ...keys) {
  for (const k of keys) {
    const v = ctx.query[k] ?? ctx.body[k]
    if (v !== undefined && v !== null && v !== '') return v
  }
  return undefined
}

function fail(message, status = 500) {
  const err = new Error(message)
  err.status = status
  throw err
}

function findGoods(id) {
  return state.goods.find(g => g.id === Number(id))
}

function photosOf(good) {
  if (Array.isArray(good.photos) && good.photos.length) return good.photos
  return []
}

function firstPhotoOf(good) {
  const photos = photosOf(good)
  if (photos.length) return photos[0]
  return readBase64(good.img || 1)
}

function slideShowOf(good) {
  const photos = photosOf(good)
  if (photos.length) return photos.slice(0, 5)
  const img = good.img || 1
  // 演示商品每种只有一张主图：详情页只放自己的图，
  // 避免轮播里出现别的商品图片（示意图与商品名对不上）
  return [readBase64(img)]
}

// 商品列表项：市场、收藏、我发布的共用
function goodsItem(good) {
  return {
    id: good.id,
    tradeName: good.tradeName,
    price: good.price,
    img: imagesIndex(good),
    status: good.status,
  }
}

// 前端"我收藏的 / 我发布的"用 getFirstPhoto 按 id 取图，这里不需要返回图片本身，
// 但保留 img 字段方便以后直接取演示图序号
function imagesIndex(good) {
  return good.img || 1
}

function userOf(ctx) {
  const raw = ctx.headers['token'] || ctx.headers['authorization'] || ''
  const token = String(raw).replace(/^Bearer\s+/i, '')
  if (token && state.tokens.has(token)) return state.tokens.get(token)
  // token 在 dev server 重启后从内存里消失，用 token 里的账号名恢复身份，
  // 这样刷新页面不会突然变成"未登录"或掉权限
  if (token) {
    const m = /^mock-token-(.+?)-\d+$/.exec(token)
    if (m) {
      const acc = state.accounts[m[1]]
      if (acc) return publicUser(acc)
    }
  }
  // 未登录也返回演示用户，避免"我发布的"等页面空白
  return publicUser(state.accounts.demo)
}

function publicUser(acc) {
  return { id: acc.id, account: acc.account, nickname: acc.nickname, authority: acc.authority }
}

function profileOf(user) {
  if (!state.profiles[user.id]) {
    state.profiles[user.id] = { ...defaultProfile, nickname: user.nickname }
  }
  return state.profiles[user.id]
}

function avatarOf(user) {
  const p = profileOf(user)
  return p.avatar || readAvatar()
}

function goodsCount(id) {
  return state.likeCount.get(Number(id)) || 0
}

// 每个用户的收藏集合
function likedOf(user) {
  if (!state.liked.has(user.id)) state.liked.set(user.id, new Set())
  return state.liked.get(user.id)
}

export function createRoutes() {
  const R = []
  const add = (method, p, handler) => R.push({ method: method.toUpperCase(), path: p, handler })

  // ---------- 商品 ----------
  add('GET', '/commodity/getCommodity', ({ query }) => {
    // 商品市场只展示审核通过的商品
    let list = state.goods.filter(g => g.status === GOODS_STATUS.PASS)
    const category = query.category || ''
    const keyword = String(query.tradeName || '').trim()
    if (category && category !== 'all') list = list.filter(g => g.category === category)
    if (keyword) list = list.filter(g => g.tradeName.includes(keyword))

    // 排序：价格/时间按钮每点一次 flag+1；flag 从 -1 起步，
    // 0 表示降序、1 表示升序，与后端 SelectAll 的 flag%2 语义保持一致
    const priceFlag = Number(query.priceFlag)
    const timeFlag = Number(query.timeFlag)
    if (Number.isFinite(priceFlag) && priceFlag >= 0) list.sort((a, b) => (priceFlag % 2 === 1 ? a.price - b.price : b.price - a.price))
    else if (Number.isFinite(timeFlag) && timeFlag >= 0) list.sort((a, b) => (timeFlag % 2 === 1 ? a.time.localeCompare(b.time) : b.time.localeCompare(a.time)))

    const currentPage = Math.max(1, Number(query.currentPage) || 1)
    const pageSize = Math.max(1, Number(query.pageSize) || 9)
    const total = list.length
    const pages = Math.max(1, Math.ceil(total / pageSize))
    const records = list
      .slice((currentPage - 1) * pageSize, currentPage * pageSize)
      .map(g => ({ id: g.id, tradeName: g.tradeName, price: g.price }))
    return { records, pages, total }
  })

  // 前端会自行拼接 'data:image/Jpeg;base64,' 前缀，这里统一返回纯 base64
  add('GET', '/commodity/getFirstPhoto', ctx => {
    const good = findGoods(val(ctx, 'commodityId', 'commodityID', 'id'))
    return good ? firstPhotoOf(good) : ''
  })

  add('GET', '/commodity/getCount', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    return { likeCount: goodsCount(id), likeStatus: likedOf(userOf(ctx)).has(id) }
  })

  add('POST', '/commodity/queryCommodity', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    const good = findGoods(id) || state.goods[0]
    return {
      tradeName: good.tradeName,
      price: good.price,
      description: good.description,
      sellerId: good.sellerId,
      sellerName: good.sellerName,
      sellerAvatar: readAvatar(),
      // 卖家在售件数（详情页卖家卡片展示）
      listingCount: state.goods.filter(g => g.sellerId === good.sellerId && g.status === GOODS_STATUS.PASS).length,
      shippingAddress: good.shippingAddress,
      category: good.category,
      time: good.time,
      slideShow: slideShowOf(good),
    }
  })

  // 发布商品：写入商品库，状态为"未审核"，随后可在管理端审核上架
  add('POST', '/commodity/launchCommodityInfo', ctx => {
    const user = userOf(ctx)
    const id = state.nextGoodsId++
    const firstPhoto = stripDataUri(val(ctx, 'firstPhoto', 'firstphoto'))
    const item = {
      id,
      tradeName: String(val(ctx, 'tradeName', 'tradename') || '未命名商品').slice(0, 30),
      price: Number(val(ctx, 'price')) || 0,
      category: val(ctx, 'category') || '其他',
      sellerId: user.id,
      sellerName: user.nickname,
      status: GOODS_STATUS.PENDING,
      time: nowStr(),
      shippingAddress: val(ctx, 'shippingAddress', 'shippingaddress') || '',
      description: val(ctx, 'description') || '',
      img: 1,
      photos: firstPhoto ? [firstPhoto] : [],
    }
    state.goods.unshift(item)
    state.likeCount.set(id, 0)
    return { commodityId: id }
  })

  // 轮播图：前端传完整 data URI 数组
  add('POST', '/commodity/saveSlideShow', ctx => {
    const id = Number(val(ctx, 'commodityId', 'id'))
    const good = findGoods(id)
    if (!good) fail('商品不存在')
    const raw = ctx.body.base64
    const list = Array.isArray(raw) ? raw : []
    const slides = list.map(stripDataUri).filter(Boolean)
    const cover = good.photos && good.photos.length ? good.photos[0] : ''
    good.photos = [...(cover ? [cover] : []), ...slides].slice(0, 6)
    return true
  })

  // 删除自己发布的商品
  add('GET', '/commodity/undercarriage', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    const idx = state.goods.findIndex(g => g.id === id)
    if (idx >= 0) state.goods.splice(idx, 1)
    // 所有用户的收藏里都移除该商品
    for (const set of state.liked.values()) set.delete(id)
    state.likeCount.delete(id)
    return true
  })

  // 编辑商品：只允许卖家本人（或管理员），编辑后重新进入审核队列
  add('POST', '/commodity/updateCommodityInfo', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    const good = findGoods(id)
    if (!good) fail('商品不存在')
    const user = userOf(ctx)
    if (good.sellerId !== user.id && user.authority !== 1) fail('只能编辑自己发布的商品', 403)

    const name = String(val(ctx, 'tradeName', 'tradename') || '').trim()
    if (!name) fail('商品名称不能为空', 400)
    good.tradeName = name.slice(0, 30)
    good.price = Number(val(ctx, 'price')) || good.price
    good.category = val(ctx, 'category') || good.category
    good.description = val(ctx, 'description') || ''
    good.shippingAddress = val(ctx, 'shippingAddress', 'shippingaddress') || ''

    const cover = stripDataUri(val(ctx, 'firstPhoto', 'firstphoto'))
    if (cover) {
      const rest = (good.photos || []).slice(1)
      good.photos = [cover, ...rest]
    }

    // 轮播图：前端以 JSON 字符串提交 data URI 数组
    const rawSlides = ctx.body.slideShow
    if (typeof rawSlides === 'string' && rawSlides) {
      try {
        const arr = JSON.parse(rawSlides)
        const slides = (Array.isArray(arr) ? arr : []).map(stripDataUri).filter(Boolean)
        const first = good.photos && good.photos.length ? good.photos[0] : cover
        good.photos = [...(first ? [first] : []), ...slides].slice(0, 6)
      } catch {
        /* 忽略非法 JSON */
      }
    }

    // 编辑后重新审核，符合「发布-审核-上架」的业务口径
    good.status = GOODS_STATUS.PENDING
    good.time = nowStr()
    return { commodityId: id, status: good.status }
  })

  // 卖家自己下架（保留记录，只在管理端与「我发布的」可见）
  add('GET', '/commodity/offline', ctx => {
    const good = findGoods(val(ctx, 'commodityId', 'commodityID', 'id'))
    if (!good) fail('商品不存在')
    good.status = GOODS_STATUS.OFFLINE
    return true
  })

  // 卖家重新上架（演示直接回到「通过」，真实后端可改为重新审核）
  add('GET', '/commodity/online', ctx => {
    const good = findGoods(val(ctx, 'commodityId', 'commodityID', 'id'))
    if (!good) fail('商品不存在')
    good.status = GOODS_STATUS.PASS
    return true
  })

  // ---------- 卖家主页 ----------
  // 卖家公开信息：昵称、信用、在售/已售数量
  add('GET', '/seller/detail', ctx => {
    const userId = Number(val(ctx, 'userId', 'sellerId', 'id'))
    const goods = state.goods.filter(g => g.sellerId === userId)
    const first = goods[0]
    const listing = goods.filter(g => g.status === GOODS_STATUS.PASS).length
    const sold = goods.filter(g => g.status === GOODS_STATUS.SOLD).length
    const rank = 0.7 + ((userId % 3) * 0.1)
    return {
      id: userId,
      nickname: first ? first.sellerName : '校园卖家',
      avatar: readAvatar(),
      rank: Number(rank.toFixed(2)),
      creditText: rank >= 0.75 ? '信用极好' : '信用良好',
      listingCount: listing,
      soldCount: sold,
      goodsCount: goods.length,
      joinedAt: '2025-09',
      intro: '在校学生，闲置多为自用物品，支持校内当面验货，交易后可留言评价。',
    }
  })

  // 卖家在售商品（只返回审核通过的）
  add('GET', '/seller/goods', ctx => {
    const userId = Number(val(ctx, 'userId', 'sellerId', 'id'))
    return state.goods
      .filter(g => g.sellerId === userId && g.status === GOODS_STATUS.PASS)
      .map(g => ({ id: g.id, tradeName: g.tradeName, price: g.price, category: g.category, time: g.time }))
  })

  // ---------- 搜索 ----------
  // 热门搜索词：由商品名称派生，避免写死一份与商品库无关的词
  add('GET', '/search/hot', () => {
    const words = ['iPhone', '键盘', '自行车', '教材', '台灯', '冰箱', '羽毛球', '考研', '耳机', '宿舍']
    return words.filter(w => state.goods.some(g => g.tradeName.includes(w))).slice(0, 8)
  })

  // ---------- 分类 ----------
  // 与后端一致：分类返回对象数组（前端读取 name 字段）
  add('GET', '/category/getAllCategories', () => state.categories.map(name => ({ name })))

  // 分类管理（管理端）：带商品数量统计
  add('GET', '/category/list', () =>
    state.categories.map(name => ({
      name,
      count: state.goods.filter(g => g.category === name).length,
    })),
  )

  add('POST', '/category/add', ctx => {
    const name = String(val(ctx, 'name') || '').trim()
    if (!name) fail('分类名称不能为空', 400)
    if (name.length > 12) fail('分类名称不超过 12 个字', 400)
    if (state.categories.includes(name)) fail('该分类已存在', 400)
    state.categories.push(name)
    return true
  })

  add('POST', '/category/update', ctx => {
    const oldName = String(val(ctx, 'oldName', 'name') || '').trim()
    const newName = String(val(ctx, 'newName') || '').trim()
    const index = state.categories.indexOf(oldName)
    if (index < 0) fail('分类不存在', 404)
    if (!newName) fail('新分类名称不能为空', 400)
    if (newName !== oldName && state.categories.includes(newName)) fail('该分类已存在', 400)
    state.categories[index] = newName
    // 同步已发布商品的分类
    for (const g of state.goods) {
      if (g.category === oldName) g.category = newName
    }
    return true
  })

  add('POST', '/category/delete', ctx => {
    const name = String(val(ctx, 'name') || '').trim()
    const index = state.categories.indexOf(name)
    if (index < 0) fail('分类不存在', 404)
    const used = state.goods.filter(g => g.category === name).length
    if (used > 0) fail(`该分类下还有 ${used} 件商品，不能删除`, 400)
    state.categories.splice(index, 1)
    return true
  })

  // ---------- 用户 ----------
  add('POST', '/user/login', ctx => {
    const account = String(val(ctx, 'account') || '').trim()
    const pwd = String(val(ctx, 'pwd', 'password') || '')
    const user = state.accounts[account] || state.accounts[account.toLowerCase()]
    if (!user || user.password !== pwd) fail('账号或密码错误', 500)
    const token = `mock-token-${user.account}-${Date.now()}`
    state.tokens.set(token, publicUser(user))
    return { token, ...publicUser(user), avatar: avatarOf(user) }
  })

  // 注册：前端的 account 字段是昵称，email 才是登录账号
  add('POST', '/user/register', ctx => {
    const email = String(val(ctx, 'email') || '').trim().toLowerCase()
    const nickname = String(val(ctx, 'account') || email).trim()
    const password = String(val(ctx, 'password') || '')
    if (!email) fail('邮箱不能为空')
    if (!password) fail('密码不能为空')
    if (state.accounts[email]) fail('该邮箱已注册，请直接登录')
    const user = {
      account: email,
      password,
      id: state.nextUserId++,
      nickname: nickname || email,
      authority: 0,
    }
    state.accounts[email] = user
    state.profiles[user.id] = { ...defaultProfile, nickname: user.nickname }
    return true
  })

  add('GET', '/user/getCode', ctx => {
    const email = String(val(ctx, 'email') || '').trim().toLowerCase()
    if (email && state.accounts[email]) {
      return { code: 401, msg: '该邮箱已注册，请直接登录' }
    }
    return true
  })

  // ---------- 个人 ----------
  add('GET', '/personal/getUserInfo', ctx => {
    const user = userOf(ctx)
    const p = profileOf(user)
    return {
      id: user.id,
      nickname: p.nickname || user.nickname,
      avatar: avatarOf(user),
      authority: user.authority,
      address: p.address || '',
      telephone: p.telephone || '',
    }
  })

  add('POST', '/personal/savePersonal', ctx => {
    const user = userOf(ctx)
    const p = profileOf(user)
    const nickname = val(ctx, 'nickname')
    const address = val(ctx, 'address')
    const telephone = val(ctx, 'telephone')
    const avatar = stripDataUri(val(ctx, 'avatar'))
    if (nickname) p.nickname = nickname
    if (address !== undefined) p.address = address
    if (telephone !== undefined) p.telephone = telephone
    if (avatar) p.avatar = avatar
    // 昵称同步到账号，登录后欢迎语与卖家名保持一致
    if (nickname && state.accounts[user.account]) state.accounts[user.account].nickname = nickname
    return true
  })

  add('GET', '/person/details', ctx => {
    const user = userOf(ctx)
    return {
      liked: likedOf(user).size,
      bought: state.orders.filter(o => o.buyerId === user.id).length,
      sold: state.sells.filter(o => o.sellerId === user.id).length,
      published: state.goods.filter(g => g.sellerId === user.id).length,
      rank: 0.78,
    }
  })

  add('GET', '/person/liked', ctx => {
    const liked = likedOf(userOf(ctx))
    return state.goods.filter(g => liked.has(g.id)).map(goodsItem)
  })

  add('GET', '/person/bought', ctx => {
    const user = userOf(ctx)
    return state.orders
      .filter(o => o.buyerId === user.id)
      .map(o => ({ ...o }))
  })

  add('GET', '/person/sold', ctx => {
    const user = userOf(ctx)
    return state.sells
      .filter(o => o.sellerId === user.id)
      .map(o => ({ ...o }))
  })

  add('GET', '/person/published', ctx => {
    const user = userOf(ctx)
    return state.goods.filter(g => g.sellerId === user.id).map(goodsItem)
  })

  // 确认收货
  add('GET', '/person/receive', ctx => {
    const orderId = Number(val(ctx, 'orderId', 'id'))
    const order = state.orders.find(o => o.id === orderId)
    if (order) {
      order.logisticsStatus = '已签收'
      order.orderStatus = '已完成'
    }
    return true
  })

  // 删除订单
  add('POST', '/person/deleteOrder', ctx => {
    const orderId = Number(val(ctx, 'orderId', 'id'))
    const idx = state.orders.findIndex(o => o.id === orderId)
    if (idx >= 0) state.orders.splice(idx, 1)
    return true
  })

  // ---------- 交易 ----------
  add('GET', '/shop/collectAdd', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'orderId', 'id'))
    const good = findGoods(id)
    if (!good) fail('商品不存在')
    const liked = likedOf(userOf(ctx))
    const now = !liked.has(id)
    if (now) liked.add(id)
    else liked.delete(id)
    const count = goodsCount(id) + (now ? 1 : -1)
    state.likeCount.set(id, Math.max(0, count))
    return now
  })

  add('POST', '/shop/generateOrder', ctx => {
    const user = userOf(ctx)
    const good = findGoods(val(ctx, 'commodityId', 'commodityID', 'id'))
    if (!good) fail('商品不存在')
    const profile = profileOf(user)
    const order = {
      id: state.nextOrderId++,
      commodityId: good.id,
      name: good.tradeName,
      price: good.price,
      seller: good.sellerName,
      orderStatus: '未支付',
      logisticsStatus: '未发货',
      submitTime: nowStr(),
      buyerId: user.id,
    }
    state.orders.push(order)
    return {
      id: order.id,
      sellerName: good.sellerName,
      address: profile.address || '广西大学东校园 7 栋',
      userName: profile.nickname || user.nickname,
      price: good.price,
    }
  })

  add('GET', '/shop/payment', ctx => {
    const orderId = Number(val(ctx, 'orderId', 'id'))
    const order = state.orders.find(o => o.id === orderId)
    if (order) order.orderStatus = '已支付'
    return '微信：138****8888（演示）'
  })

  // 卖家发货
  add('GET', '/shop/confirmDeliverGood', ctx => {
    const orderId = Number(val(ctx, 'orderId', 'id'))
    const order = state.sells.find(o => o.id === orderId)
    if (order) {
      order.logisticsStatus = '已发货'
      if (order.orderStatus === '未支付') order.orderStatus = '已支付'
    }
    return true
  })

  add('POST', '/collect/add', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'orderId', 'id'))
    if (!findGoods(id)) return false
    const liked = likedOf(userOf(ctx))
    if (liked.has(id)) return false
    liked.add(id)
    state.likeCount.set(id, goodsCount(id) + 1)
    return true
  })

  add('GET', '/collect/del', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'orderId', 'id'))
    const liked = likedOf(userOf(ctx))
    if (liked.has(id)) {
      liked.delete(id)
      state.likeCount.set(id, Math.max(0, goodsCount(id) - 1))
    }
    return true
  })

  // ---------- 订单详情与评价 ----------
  function findOrder(orderId) {
    const id = Number(orderId)
    const bought = state.orders.find(o => o.id === id)
    if (bought) return bought
    return state.sells.find(o => o.id === id) || null
  }

  add('GET', '/order/detail', ctx => {
    const orderId = Number(val(ctx, 'orderId', 'id'))
    const user = userOf(ctx)
    const order = findOrder(orderId)
    if (!order) fail('订单不存在', 404)
    const good = findGoods(order.commodityId)
    const review = state.reviews.find(r => r.orderId === orderId) || null
    const isBuyer = order.buyerId === user.id
    const isSeller = order.sellerId === user.id || (good && good.sellerId === user.id)
    return {
      id: order.id,
      commodityId: order.commodityId,
      name: order.name,
      price: order.price,
      seller: order.seller,
      buyer: isBuyer ? user.nickname : '同学',
      orderStatus: order.orderStatus,
      logisticsStatus: order.logisticsStatus,
      submitTime: order.submitTime,
      buyerId: order.buyerId || 0,
      sellerId: order.sellerId || (good ? good.sellerId : 0),
      role: isBuyer ? 'buyer' : isSeller ? 'seller' : 'viewer',
      review,
    }
  })

  add('POST', '/review/add', ctx => {
    const orderId = Number(val(ctx, 'orderId', 'id'))
    const score = Number(val(ctx, 'score'))
    const content = String(val(ctx, 'content') || '').trim()
    const user = userOf(ctx)
    const order = findOrder(orderId)
    if (!order) fail('订单不存在', 404)
    if (order.buyerId !== user.id) fail('只有买家可以评价该订单', 403)
    if (state.reviews.some(r => r.orderId === orderId)) fail('该订单已经评价过了', 400)
    if (!(score >= 1 && score <= 5)) fail('评分需要在 1~5 之间', 400)
    if (!content) fail('评价内容不能为空', 400)
    if (order.orderStatus !== '已完成' && order.logisticsStatus !== '已签收') {
      fail('订单完成后才能评价', 400)
    }
    const item = {
      id: state.nextReviewId++,
      orderId,
      commodityId: order.commodityId,
      score,
      content: content.slice(0, 200),
      authorName: user.nickname || '同学',
      time: nowStr(),
    }
    state.reviews.push(item)
    return item
  })

  add('GET', '/review/list', ctx => {
    const commodityId = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    const list = state.reviews.filter(r => r.commodityId === commodityId)
    const avg = list.length ? Number((list.reduce((s, r) => s + r.score, 0) / list.length).toFixed(1)) : 0
    return { list: [...list].sort((a, b) => b.id - a.id), avg, total: list.length }
  })

  // ---------- 留言 ----------
  // 商品详情页的留言板：按商品查、登录后新增
  add('GET', '/comment/list', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    return state.comments
      .filter(c => c.commodityId === id)
      .sort((a, b) => b.id - a.id)
  })

  add('POST', '/comment/add', ctx => {
    const id = Number(val(ctx, 'commodityId', 'commodityID', 'id'))
    if (!findGoods(id)) fail('商品不存在')
    const content = String(val(ctx, 'content') || '').trim()
    if (!content) fail('留言内容不能为空', 400)
    if (content.length > 200) fail('留言最多 200 字', 400)
    const user = userOf(ctx)
    const item = {
      id: state.nextCommentId++,
      commodityId: id,
      authorId: user.id,
      authorName: user.nickname || '同学',
      content,
      time: nowStr(),
    }
    state.comments.push(item)
    return item
  })

  // 个人中心「消息留言」：我收到的（我发布的商品下的留言）+ 我发出的
  add('GET', '/comment/mine', ctx => {
    const user = userOf(ctx)
    const received = []
    const sent = []
    for (const c of state.comments) {
      const good = findGoods(c.commodityId)
      const row = { ...c, goodsName: good ? good.tradeName : '已删除商品' }
      if (good && good.sellerId === user.id) received.push(row)
      if (c.authorId === user.id) sent.push(row)
    }
    received.sort((a, b) => b.id - a.id)
    sent.sort((a, b) => b.id - a.id)
    return { received, sent }
  })

  // ---------- 管理端 ----------
  add('GET', '/manage/select', ({ query }) => {
    let list = [...state.goods]
    const keyword = String(query.input || '').trim()
    const status = query.status || ''
    const category = query.category || ''
    if (keyword) list = list.filter(x => x.tradeName.includes(keyword) || (x.description || '').includes(keyword))
    if (status) list = list.filter(x => x.status === status)
    if (category) list = list.filter(x => x.category === category)
    if (query.orderBuy === 'ASC') list.sort((a, b) => a.time.localeCompare(b.time))
    else list.sort((a, b) => b.time.localeCompare(a.time))

    const currentPage = Math.max(1, Number(query.pageNum) || 1)
    const pageSize = Math.max(1, Number(query.pageSize) || 8)
    const pages = Math.max(1, Math.ceil(list.length / pageSize))
    return {
      records: list.slice((currentPage - 1) * pageSize, currentPage * pageSize).map(g => ({
        id: g.id,
        tradeName: g.tradeName,
        sellerName: g.sellerName,
        status: g.status,
        time: g.time,
        description: g.description,
      })),
      pages,
      total: list.length,
    }
  })

  // 饼图：商品分类占比（0~1 小数，前端会 *100）
  add('GET', '/manage/category', () => {
    const result = {}
    const total = state.goods.length || 1
    for (const g of state.goods) result[g.category] = (result[g.category] || 0) + 1
    for (const key of Object.keys(result)) result[key] = Number((result[key] / total).toFixed(3))
    return result
  })

  for (const action of ['refuse', 'remove', 'approved']) {
    add('POST', `/manage/${action}`, ctx => {
      const id = Number(val(ctx, 'id', 'commodityId', 'commodityID'))
      const good = findGoods(id)
      if (good) {
        if (action === 'approved') good.status = GOODS_STATUS.PASS
        else if (action === 'refuse') good.status = GOODS_STATUS.REJECT
        else good.status = GOODS_STATUS.OFFLINE
      }
      return true
    })
  }

  return R
}
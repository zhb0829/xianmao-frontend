// 求购专区 / 校园圈的本地数据层
//
// 现状：后端没有这两块接口，先把数据存在浏览器 localStorage 里，
// 页面通过本文件读写，不直接碰 storage。
// 以后接后端时，只需把这里的函数换成 $http 请求，页面不用改。

import p1 from '../assets/cl/img/p1.jpg'
import library from '../assets/cl/img/hero-campus.jpg'
import { getToken } from './request.js'

const NS = 'cl:'

function readJson(key, fallback) {
  try {
    const raw = localStorage.getItem(NS + key)
    if (!raw) return fallback
    const value = JSON.parse(raw)
    return value === null || value === undefined ? fallback : value
  } catch {
    return fallback
  }
}

function writeJson(key, value) {
  try {
    localStorage.setItem(NS + key, JSON.stringify(value))
    return true
  } catch {
    // 配额溢出（QuotaExceededError）时返回 false，由页面提示用户
    return false
  }
}

export function uid(prefix = 'id') {
  return `${prefix}_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 7)}`
}

// 当前账号标识：演示模式 token 形如 mock-token-demo-1234，真实后端是 JWT
// token 统一由请求层（src/api/request.js）维护，这里只读
export function currentOwner() {
  const token = getToken()
  const mock = /^mock-token-([a-zA-Z0-9]+)-\d+$/.exec(token)
  if (mock) return mock[1]
  return token ? `u_${token.slice(-8)}` : 'guest'
}

export function hasToken() {
  return !!getToken()
}

// ---------------- 求购 ----------------

const NOW = Date.now()
const MIN = 60 * 1000
const HOUR = 60 * MIN
const DAY = 24 * HOUR

export const WANT_CATEGORIES = [
  '学习用品',
  '电子产品',
  '生活用品',
  '运动户外',
  '家居家装',
  '其他',
]

const WANT_SEED = [
  {
    id: 'want_seed_1',
    owner: 'seed',
    author: { name: '林同学', credit: '信用极好' },
    category: '学习用品',
    title: '求购《数据结构》严蔚敏 C 语言版',
    budget: 25,
    desc: '期末复习要用，二手就行，笔记多一点更好，价格可以再商量。',
    place: '东校园 3 栋',
    contact: '私信',
    createdAt: NOW - 26 * MIN,
    seeded: true,
  },
  {
    id: 'want_seed_2',
    owner: 'seed',
    author: { name: '周同学', credit: '信用良好' },
    category: '电子产品',
    title: '收一台 iPad 9 代 / 10 代，能看网课就行',
    budget: 1200,
    desc: '屏幕无划痕、电池健康 80% 以上，带充电线，可当面试机。',
    place: '西校园 12 栋',
    contact: '私信',
    createdAt: NOW - 3 * HOUR,
    seeded: true,
  },
  {
    id: 'want_seed_3',
    owner: 'seed',
    author: { name: '赵同学', credit: '信用极好' },
    category: '生活用品',
    title: '求一个宿舍小冰箱，42L 以上',
    budget: 260,
    desc: '宿舍想存点水果酸奶，能制冷就行，外观新旧无所谓，可以自己搬。',
    place: '东校园 7 栋',
    contact: '私信',
    createdAt: NOW - 7 * HOUR,
    seeded: true,
  },
  {
    id: 'want_seed_4',
    owner: 'seed',
    author: { name: '许同学', credit: '信用良好' },
    category: '运动户外',
    title: '求购山地车 / 公路车，能通勤就行',
    budget: 300,
    desc: '每天从西校园骑到教学楼，车况正常、刹车好用就行，带锁更好。',
    place: '西校园 5 栋',
    contact: '私信',
    createdAt: NOW - DAY - 2 * HOUR,
    seeded: true,
  },
  {
    id: 'want_seed_5',
    owner: 'seed',
    author: { name: '罗同学', credit: '信用极好' },
    category: '学习用品',
    title: '收考研英语真题（英语一）2020-2025',
    budget: 30,
    desc: '黄皮书或试卷版都可以，答案册要完整，做过一部分也能接受。',
    place: '图书馆 2 层',
    contact: '私信',
    createdAt: NOW - DAY - 5 * HOUR,
    seeded: true,
  },
  {
    id: 'want_seed_6',
    owner: 'seed',
    author: { name: '黄同学', credit: '信用良好' },
    category: '电子产品',
    title: '求购 24 寸显示器，1080P 即可',
    budget: 400,
    desc: '宿舍外接笔记本用，接口 HDMI 就好，有一点亮斑也能接受。',
    place: '东校园 9 栋',
    contact: '私信',
    createdAt: NOW - 2 * DAY,
    seeded: true,
  },
]

export function loadWants() {
  const mine = readJson('want:mine', [])
  return [...mine, ...WANT_SEED].sort((a, b) => b.createdAt - a.createdAt)
}

export function addWant(data) {
  const mine = readJson('want:mine', [])
  const item = {
    id: uid('want'),
    owner: currentOwner(),
    author: { name: data.authorName || '我', credit: data.credit || '信用良好' },
    category: data.category,
    title: data.title,
    budget: Number(data.budget) || 0,
    desc: data.desc,
    place: data.place,
    contact: data.contact || '私信',
    createdAt: Date.now(),
  }
  const next = [item, ...mine]
  return writeJson('want:mine', next) ? item : null
}

export function removeWant(id) {
  const mine = readJson('want:mine', [])
  const next = mine.filter(item => item.id !== id)
  if (next.length === mine.length) return true
  return writeJson('want:mine', next)
}

export function isMine(owner) {
  return owner === currentOwner()
}

// ---------------- 校园圈 ----------------

export const POST_TOPICS = ['出闲置', '求助', '分享', '活动', '吐槽']

const POST_SEED = [
  {
    id: 'post_seed_1',
    owner: 'seed',
    author: { name: '梁同学' },
    topic: '出闲置',
    text: '搬宿舍清出来一摞教材和笔记，免费送，东校园 11 栋自提，先到先得。',
    images: [p1],
    likeCount: 12,
    comments: [
      { id: 'c1', name: '韦同学', text: '还有高数吗？我明天过去拿', createdAt: NOW - 18 * MIN },
    ],
    createdAt: NOW - 42 * MIN,
    seeded: true,
  },
  {
    id: 'post_seed_2',
    owner: 'seed',
    author: { name: '郑同学' },
    topic: '分享',
    text: '期末周，图书馆三楼靠窗的位置最抢手，早上八点去还有座，带杯热水能坐一天。',
    images: [library],
    likeCount: 28,
    comments: [],
    createdAt: NOW - 2 * HOUR,
    seeded: true,
  },
  {
    id: 'post_seed_3',
    owner: 'seed',
    author: { name: '陈同学' },
    topic: '求助',
    text: '有同学知道图书馆现在几点闭馆吗？想过去自习到十点。',
    images: [],
    likeCount: 4,
    comments: [
      { id: 'c2', name: '罗同学', text: '十点半闭馆，九点半以后人少', createdAt: NOW - HOUR },
    ],
    createdAt: NOW - 3 * HOUR,
    seeded: true,
  },
  {
    id: 'post_seed_4',
    owner: 'seed',
    author: { name: '孙同学' },
    topic: '活动',
    text: '周六下午三点，东篮球场约球，缺两个人，水平随意，主要是运动一下。',
    images: [],
    likeCount: 9,
    comments: [],
    createdAt: NOW - DAY,
    seeded: true,
  },
  {
    id: 'post_seed_5',
    owner: 'seed',
    author: { name: '韦同学' },
    topic: '吐槽',
    text: '食堂三楼新窗口排队太长了，建议错峰，十一点半之前基本不用等。',
    images: [],
    likeCount: 17,
    comments: [],
    createdAt: NOW - DAY - 4 * HOUR,
    seeded: true,
  },
]

export function loadPosts(topic) {
  const mine = readJson('circle:mine', [])
  const liked = readJson('circle:likes', [])
  const comments = readJson('circle:comments', {})

  return [...mine, ...POST_SEED]
    .filter(post => !topic || post.topic === topic)
    .sort((a, b) => b.createdAt - a.createdAt)
    .map(post => {
      const isLiked = liked.includes(post.id)
      return {
        ...post,
        liked: isLiked,
        likeCount: (post.likeCount || 0) + (isLiked ? 1 : 0),
        comments: [...(post.comments || []), ...(comments[post.id] || [])],
      }
    })
}

export function addPost(data) {
  const mine = readJson('circle:mine', [])
  const item = {
    id: uid('post'),
    owner: currentOwner(),
    author: { name: data.authorName || '我' },
    topic: data.topic || '分享',
    text: data.text,
    images: data.images || [],
    likeCount: 0,
    comments: [],
    createdAt: Date.now(),
  }
  const next = [item, ...mine]
  return writeJson('circle:mine', next) ? item : null
}

export function removePost(id) {
  const mine = readJson('circle:mine', [])
  const next = mine.filter(post => post.id !== id)
  if (next.length === mine.length) return true
  return writeJson('circle:mine', next)
}

export function toggleLike(id) {
  const liked = readJson('circle:likes', [])
  const index = liked.indexOf(id)
  if (index >= 0) liked.splice(index, 1)
  else liked.push(id)
  writeJson('circle:likes', liked)
  return index < 0
}

export function addComment(postId, comment) {
  const map = readJson('circle:comments', {})
  const list = map[postId] || []
  map[postId] = [...list, comment]
  return writeJson('circle:comments', map)
}

// 时间显示：刚刚 / x 分钟前 / x 小时前 / 昨天 / MM-DD
export function timeAgo(timestamp) {
  const diff = Date.now() - Number(timestamp || 0)
  if (!Number.isFinite(diff) || diff < 0) return '刚刚'
  if (diff < MIN) return '刚刚'
  if (diff < HOUR) return `${Math.floor(diff / MIN)} 分钟前`
  if (diff < DAY) return `${Math.floor(diff / HOUR)} 小时前`
  if (diff < 2 * DAY) return '昨天'
  const d = new Date(timestamp)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getMonth() + 1}-${pad(d.getDate())}`
}
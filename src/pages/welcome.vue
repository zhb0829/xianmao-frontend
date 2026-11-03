<template>
  <div class="cl-app landing">
    <site-header active="home" />

    <main>
      <!-- ================= Hero ================= -->
      <section class="hero">
        <div class="cl-wrap hero-row">
          <div class="hero-card">
            <div class="hero-photo" :style="{ backgroundImage: `url(${img.hero})` }"></div>
            <div class="hero-shade"></div>
            <div class="hero-copy">
              <span class="hero-badge">
                <img :src="ico.check" alt="" />
                已认证 42 所高校 · 18.6 万名在校同学
              </span>
              <h1 class="hero-title">闲置不浪费，同学之间<br />更好流转</h1>
              <p class="hero-lede">
                教材、数码、代步车、宿舍家具，都与同校同学当面交易。实名认证 + 验货担保，让每一笔二手买卖都清爽可信。
              </p>
              <div class="hero-actions">
                <button class="btn-primary" @click="go('/shop')">开始逛逛</button>
                <button class="btn-ghost" @click="go('/publish')">一键发布闲置</button>
              </div>
              <ul class="hero-trust">
                <li>校内自提</li>
                <li>资金担保</li>
                <li>7 天可退</li>
              </ul>
            </div>
          </div>

          <aside class="hero-side">
            <div class="publish-card">
              <div class="publish-head">
                <p class="publish-title">发布闲置，最快 30 秒</p>
                <img class="publish-spark" :src="ico.sparkles" alt="" />
              </div>
              <p class="publish-desc">拍照自动识别品类与建议价，同校同学第一时间看到。</p>
              <button class="publish-btn" @click="go('/publish')">
                <img :src="ico.cameraWhite" alt="" />
                立即发布
              </button>
            </div>

            <div class="heat-card">
              <div class="heat-head">
                <p class="heat-title">今日校园热度</p>
                <span class="heat-live">
                  <i class="dot"></i>
                  实时
                </span>
              </div>
              <div class="heat-stats">
                <div v-for="stat in heat" :key="stat.label" class="heat-stat">
                  <p class="heat-num" :class="{ accent: stat.accent }">{{ stat.value }}</p>
                  <p class="heat-label">{{ stat.label }}</p>
                </div>
              </div>
              <p class="heat-foot">
                <img :src="ico.trending" alt="" />
                热度最高：教材书籍 · 计算机学院 2,480 件在售
              </p>
            </div>
          </aside>
        </div>
      </section>

      <!-- ================= 分类 ================= -->
      <section class="cats">
        <div class="cl-wrap cats-inner">
          <div class="section-head">
            <div class="section-head-text">
              <h2 class="section-title">按类目逛一逛</h2>
              <p class="section-sub">24 个校内常用类目，覆盖从开学到毕业的全周期需求</p>
            </div>
            <a class="section-more" @click="go('/shop')">
              查看全部类目
              <img :src="ico.chevron" alt="" />
            </a>
          </div>

          <div class="cat-grid">
            <button
              v-for="cat in cats"
              :key="cat.name"
              class="cat-card"
              :class="{ all: cat.all }"
              @click="go(cat.to)"
            >
              <span class="cat-ico">
                <img :src="cat.ico" alt="" />
              </span>
              <span class="cat-name">{{ cat.name }}</span>
              <span class="cat-count">{{ cat.count }}</span>
            </button>
          </div>
        </div>
      </section>

      <!-- ================= 推荐 ================= -->
      <section class="picks" id="cl-picks">
        <div class="cl-wrap picks-inner">
          <div class="picks-head">
            <div class="picks-title-wrap">
              <h2 class="section-title">为你推荐</h2>
              <span class="picks-tag">同校 3 公里内</span>
            </div>
            <div class="sorts">
              <button
                v-for="sort in sorts"
                :key="sort.key"
                class="sort"
                :class="{ active: sortKey === sort.key }"
                @click="sortKey = sort.key"
              >
                {{ sort.name }}
              </button>
              <div class="filter-wrap">
                <button class="sort filter" :class="{ active: !!filterType }" @click="filterOpen = !filterOpen">
                  <img :src="ico.sliders" alt="" />
                  筛选
                </button>
                <transition name="pop">
                  <div v-if="filterOpen" class="filter-menu">
                    <button
                      v-for="opt in filters"
                      :key="opt.value"
                      class="filter-opt"
                      :class="{ on: filterType === opt.value }"
                      @click="pickFilter(opt.value)"
                    >
                      {{ opt.label }}
                    </button>
                  </div>
                </transition>
              </div>
            </div>
          </div>

          <!-- 加载骨架：接口请求中 -->
          <div v-if="goodsLoading" class="goods-grid">
            <article v-for="n in 8" :key="'sk' + n" class="goods-card is-skeleton">
              <div class="goods-cover skeleton-cover"></div>
              <div class="goods-body">
                <span class="skeleton-line w-70"></span>
                <span class="skeleton-line w-40"></span>
                <span class="skeleton-line w-55"></span>
              </div>
            </article>
          </div>

          <!-- 接口失败：给出错误与重试，而不是静默展示假数据 -->
          <div v-else-if="goodsError" class="goods-empty">
            <p>{{ goodsError }}</p>
            <button class="load-more-link" @click="loadGoods">重新加载</button>
          </div>

          <div v-else class="goods-grid">
            <article
              v-for="(item, index) in visibleGoods"
              :key="item.id"
              class="goods-card"
              :style="{ '--i': index }"
              @click="openGoods(item)"
            >
              <div class="goods-cover">
                <span v-if="item.tag" class="goods-flag">{{ item.tag }}</span>
                <img v-if="item.img" class="goods-img" :src="item.img" :alt="item.name" loading="lazy" />
                <span v-else class="goods-img goods-img-empty">暂无图片</span>
              </div>
              <div class="goods-body">
                <h3 class="goods-name">{{ item.name }}</h3>
                <div class="goods-price-row">
                  <span class="goods-yen">¥</span>
                  <span class="goods-price">{{ money(item.price) }}</span>
                  <span v-if="item.old" class="goods-old">原价 {{ money(item.old) }}</span>
                </div>
                <div class="goods-meta">
                  <span class="goods-pick">{{ item.pick }}</span>
                  <span class="goods-loc">{{ item.loc }}</span>
                </div>
                <div class="goods-seller">
                  <span class="seller-dot"></span>
                  <span class="seller-name">{{ item.seller }}</span>
                  <span class="seller-verified">已实名</span>
                  <span class="seller-time">{{ agoLabel(item) }}</span>
                </div>
              </div>
            </article>
          </div>

          <button v-if="!goodsLoading && !goodsError && filteredGoods.length" class="load-more" @click="loadMore">
            <span>已展示 {{ visibleCount }} / {{ filteredGoods.length }} 件在售闲置</span>
            <span class="load-more-link">{{ expanded ? '收起商品' : '加载更多商品' }}</span>
          </button>
        </div>
      </section>

      <!-- ================= 保障 ================= -->
      <section class="guarantee" id="cl-guarantee">
        <div class="cl-wrap guarantee-inner">
          <div class="guarantee-head">
            <h2 class="guarantee-title">每一笔交易，都有校园身份担保</h2>
            <p class="guarantee-sub">
              从发布、议价到当面交付，平台全程留痕。遇到问题可申请平台介入，48 小时内给出处理结果。
            </p>
          </div>
          <div class="guarantee-grid">
            <div v-for="g in guarantees" :key="g.title" class="guarantee-card">
              <span class="guarantee-ico">
                <img :src="g.ico" alt="" />
              </span>
              <div class="guarantee-text">
                <p class="guarantee-name">{{ g.title }}</p>
                <p class="guarantee-desc">{{ g.desc }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ================= 团队 ================= -->
      <section class="team" id="cl-about">
        <div class="cl-wrap team-inner">
          <div class="section-head">
            <div class="section-head-text">
              <h2 class="section-title">课程团队</h2>

            </div>
            <span class="team-hint">悬停卡片查看详情</span>
          </div>

          <div class="team-grid">
            <div v-for="card in teamCards" :key="card.front" class="flip-card">
              <div class="flip-inner">
                <div class="flip-face flip-front">
                  <span class="flip-ico">
                    <img :src="card.ico" alt="" />
                  </span>
                  <p class="flip-title">{{ card.front }}</p>
                  <span class="flip-more">查看 →</span>
                </div>
                <div class="flip-face flip-back">
                  <p class="flip-back-title">{{ card.front }}</p>
                  <p class="flip-back-text">{{ card.back }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ================= CTA ================= -->
      <section class="cta">
        <div class="cl-wrap cta-inner">
          <div class="cta-copy">
            <h2 class="cta-title">宿舍里那些用不上的，也许正是同学的开学必备</h2>
            <p class="cta-sub">今天已有 1,284 位同学在这里发布闲置，平均 3 小时成交。</p>
          </div>
          <div class="cta-action">
            <p class="cta-ask">还没有账号？</p>
            <button class="cta-btn" @click="go('/login')">用学号登录</button>
          </div>
        </div>
      </section>
    </main>

    <site-footer />
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import http from '../api/request.js'

const router = useRouter()

const asset = (p) => new URL(`../assets/cl/${p}`, import.meta.url).href

const img = {
  hero: asset('img/hero-campus.jpg'),
  p1: asset('img/p1.jpg'),
  p2: asset('img/p2.jpg'),
  p3: asset('img/p3.jpg'),
  p4: asset('img/p4.jpg'),
  p5: asset('img/p5.jpg'),
  p6: asset('img/p6.jpg'),
  p7: asset('img/p7.jpg'),
  p8: asset('img/p8.jpg'),
}

const ico = {
  check: asset('ico/check.svg'),
  sparkles: asset('ico/sparkles.svg'),
  cameraWhite: asset('ico/camera.svg'),
  trending: asset('ico/trendingup.svg'),
  chevron: asset('ico/chevronright.svg'),
  sliders: asset('ico/slidershorizontal.svg'),
  book: asset('ico/bookopentext.svg'),
  laptop: asset('ico/laptop.svg'),
  bike: asset('ico/bike.svg'),
  shirt: asset('ico/shirt.svg'),
  dumbbell: asset('ico/dumbbell.svg'),
  music: asset('ico/music.svg'),
  home: asset('ico/home.svg'),
  grid: asset('ico/packagecheck.svg'),
  shield: asset('ico/shieldcheck.svg'),
  coins: asset('ico/handcoins.svg'),
  cap: asset('ico/graduationcap.svg'),
  users: asset('ico/userround.svg'),
  inbox: asset('ico/inbox.svg'),
}

function go(path) {
  router.push(path)
}

function money(n) {
  return Number(n).toLocaleString('zh-CN')
}

function openGoods(item) {
  // 首页卡片直接进入独立商品详情页 /goods/:id
  router.push(`/goods/${item.id}`)
}

/** 演示用的发布时间文案，按商品 id 稳定取值 */
const AGO_LABELS = [
  '6 分钟前', '12 分钟前', '18 分钟前', '26 分钟前',
  '35 分钟前', '44 分钟前', '52 分钟前', '1 小时前',
  '1.5 小时前', '2 小时前', '今天 09:20', '今天 08:05',
]

function agoLabel(item) {
  return AGO_LABELS[item.id % AGO_LABELS.length]
}

/* ---------------- 热度数据 ---------------- */
const heat = [
  { value: '1,284', label: '今日新增', accent: false },
  { value: '396', label: '今日成交', accent: false },
  { value: '98.4%', label: '好评率', accent: true },
]

/* ---------------- 分类（cat 对应后端真实类目） ---------------- */
const catQuery = (cat) => ({ path: '/shop', query: { category: cat } })

const cats = [
  { name: '教材书籍', count: '2,480 件', ico: ico.book, to: catQuery('学习用品') },
  { name: '数码电子', count: '1,236 件', ico: ico.laptop, to: catQuery('电子产品') },
  { name: '代步工具', count: '864 件', ico: ico.bike, to: catQuery('运动户外') },
  { name: '服饰鞋包', count: '1,905 件', ico: ico.shirt, to: catQuery('服饰鞋包') },
  { name: '运动健身', count: '612 件', ico: ico.dumbbell, to: catQuery('运动户外') },
  { name: '乐器音响', count: '348 件', ico: ico.music, to: catQuery('乐器') },
  { name: '宿舍电器', count: '1,072 件', ico: ico.home, to: catQuery('生活用品') },
  { name: '全部类目', count: '共 24 类', ico: ico.grid, to: { path: '/shop' }, all: true },
]

/* ---------------- 商品 ---------------- */
// 离线兜底数据：仅当 Mock / 后端都不可用且接口报错时展示
const fallbackGoods = [
  {
    id: 101, name: '《深入理解计算机系统》第三版 八成新', price: 48, old: 139,
    tag: '今天发布', pick: '自提', loc: '计算机学院 · 九成新', seller: '林同学', img: img.p1, hot: 1,
  },
  {
    id: 102, name: 'iPad Air 5 64G 深空灰 含原装壳膜', price: 2150, old: 4399,
    tag: '验货宝', pick: '可面交', loc: '东区宿舍 · 九成新', seller: '周同学', img: img.p2, hot: 3,
  },
  {
    id: 103, name: '小米电动滑板车 校园代步通勤款', price: 420, old: 999,
    tag: '急出', pick: '自提', loc: '南门车棚 · 七成新', seller: '陈同学', img: img.p3, hot: 4,
  },
  {
    id: 104, name: '罗技 K380 无线蓝牙键盘 静音键帽', price: 95, old: 259,
    tag: '今天发布', pick: '包邮寄', loc: '文理学部 · 九成新', seller: '许同学', img: img.p4, hot: 2,
  },
  {
    id: 105, name: '山地自行车 26 寸 变速 带车灯', price: 380, old: 1180,
    tag: '验货宝', pick: '自提', loc: '西区宿舍 · 八成新', seller: '赵同学', img: img.p5, hot: 6,
  },
  {
    id: 106, name: '宿舍折叠书桌 免安装 原木色', price: 68, old: 189,
    tag: '急出', pick: '仅限自提', loc: '研究生公寓 · 九成新', seller: '钱同学', img: img.p6, hot: 8,
  },
  {
    id: 107, name: 'Beats Studio 蓝牙降噪耳机 附收纳包', price: 560, old: 1699,
    tag: '验货宝', pick: '可面交', loc: '北区宿舍 · 九成新', seller: '孙同学', img: img.p7, hot: 5,
  },
  {
    id: 108, name: '英语四六级真题全套 无笔记划线', price: 22, old: 78,
    tag: '今天发布', pick: '包邮寄', loc: '图书馆 · 九成新', seller: '李同学', img: img.p8, hot: 7,
  },
  {
    id: 109, name: '宿舍小冰箱 42L 双温区 毕业清仓', price: 260, old: 699,
    tag: '急出', pick: '仅限自提', loc: '西区宿舍 · 八成新', seller: '周同学', img: img.p6, hot: 9,
  },
  {
    id: 110, name: '机械键盘 87 键 茶轴 RGB 背光', price: 199, old: 499,
    tag: '今天发布', pick: '包邮寄', loc: '东区宿舍 · 九成新', seller: '李同学', img: img.p4, hot: 10,
  },
  {
    id: 111, name: '羽毛球拍 碳素双拍 含拍套与三只球', price: 45, old: 168,
    tag: '验货宝', pick: '可面交', loc: '体育馆 · 九成新', seller: '郑同学', img: img.p3, hot: 12,
  },
  {
    id: 112, name: '考研英语真题 2010-2025 黄皮书全套', price: 20, old: 78,
    tag: '今天发布', pick: '包邮寄', loc: '图书馆 · 八成新', seller: '王同学', img: img.p8, hot: 11,
  },
]

/* ---------------- 商品（接口数据） ---------------- */
// 首页推荐位从 /commodity/getCommodity 拉取，不再写死商品数组；
// fallbackGoods 只在接口完全不可用时兜底，避免答辩现场白屏。
const goods = ref([])
const goodsLoading = ref(true)
const goodsError = ref('')

const PICK_LABELS = ['可面交', '校园自提', '包邮寄']

function normalizeGood(g, index) {
  return {
    id: g.id,
    name: g.tradeName || '未命名商品',
    price: Number(g.price) || 0,
    old: Number(g.originPrice) || 0,
    tag: index === 0 ? '热门推荐' : '',
    pick: PICK_LABELS[index % PICK_LABELS.length],
    loc: '校园自提',
    seller: g.sellerName || '同学',
    img: '',
    hot: index,
    likeCount: 0,
  }
}

async function loadGoods() {
  goodsLoading.value = true
  goodsError.value = ''
  try {
    const res = await http({
      method: 'get',
      url: '/commodity/getCommodity',
      params: { currentPage: 1, pageSize: 12, timeFlag: 0, status: '' },
    })
    const records = res.data && Array.isArray(res.data.records) ? res.data.records : []
    if (!records.length) throw new Error('暂无在售商品')
    goods.value = records.map(normalizeGood)

    // 图片与收藏数按商品补齐：单个失败不影响整页
    await Promise.all(
      goods.value.map(async item => {
        try {
          const r = await http({ method: 'get', url: '/commodity/getFirstPhoto', params: { commodityId: item.id } })
          if (typeof r.data === 'string' && r.data.length > 20) item.img = 'data:image/Jpeg;base64,' + r.data
        } catch {
          /* 图片失败留空占位 */
        }
        try {
          const c = await http({ method: 'get', url: '/commodity/getCount', params: { commodityId: item.id } })
          item.likeCount = Number(c.data && c.data.likeCount) || 0
        } catch {
          /* 收藏数失败按 0 */
        }
      }),
    )
  } catch (e) {
    goodsError.value = (e && (e.friendly || e.message)) || '接口请求失败'
    goods.value = fallbackGoods
  } finally {
    goodsLoading.value = false
  }
}

/* ---------------- 排序 / 筛选 ---------------- */
const sorts = [
  { key: 'default', name: '综合排序' },
  { key: 'new', name: '最新发布' },
  { key: 'price', name: '价格最低' },
  { key: 'near', name: '距离最近' },
]

const sortKey = ref('default')
const filterOpen = ref(false)
const filterType = ref('')

const filters = [
  { label: '全部', value: '' },
  { label: '100 元以内', value: 'lt100' },
  { label: '100–500 元', value: 'mid' },
  { label: '500 元以上', value: 'gt500' },
]

function pickFilter(value) {
  filterType.value = value
  filterOpen.value = false
}

const filteredGoods = computed(() => {
  let list = goods.value.slice()
  if (filterType.value === 'lt100') list = list.filter(g => g.price < 100)
  else if (filterType.value === 'mid') list = list.filter(g => g.price >= 100 && g.price <= 500)
  else if (filterType.value === 'gt500') list = list.filter(g => g.price > 500)

  if (sortKey.value === 'new') {
    list.sort((a, b) => Number(b.id) - Number(a.id))
  } else if (sortKey.value === 'price') {
    list.sort((a, b) => a.price - b.price)
  } else if (sortKey.value === 'near') {
    list.sort((a, b) => Number(b.likeCount) - Number(a.likeCount))
  }
  return list
})

const visibleCount = ref(8)
const expanded = computed(() => visibleCount.value >= filteredGoods.value.length)

const visibleGoods = computed(() => filteredGoods.value.slice(0, visibleCount.value))

function loadMore() {
  if (expanded.value) {
    visibleCount.value = 8
    document.getElementById('cl-picks')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    return
  }
  visibleCount.value = Math.min(visibleCount.value + 4, filteredGoods.value.length)
}

/* ---------------- 保障 ---------------- */
const guarantees = [
  { title: '校内实名认证', desc: '仅本校学生凭学号绑定，交易对象真实可查', ico: ico.shield },
  { title: '验货宝担保', desc: '贵重物品先验后付，平台担保资金安全', ico: ico.inbox },
  { title: '零服务费', desc: '发布与成交全流程免费，不抽取任何佣金', ico: ico.coins },
]

/* ---------------- 团队 ---------------- */
const teamCards = [
  { front: '团队介绍', back: '该网站由「绝不秃头」团队开发完成。', ico: ico.users },
  { front: '成员介绍', back: '章恒彬 · 叶信强 · 王宇 · 凌陪钦', ico: ico.cap },
]

/* ---------------- 入场动画 ---------------- */
onMounted(() => {
  // 首屏商品走接口：Mock 模式下由 /api 中间件返回，真实后端同理
  loadGoods()

  const targets = document.querySelectorAll('.hero-card, .cat-card, .goods-card, .guarantee-card, .flip-card')

  const showAll = () => targets.forEach(el => el.classList.add('in-view'))

  if (!('IntersectionObserver' in window)) {
    showAll()
    return
  }

  const io = new IntersectionObserver(
    entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('in-view')
          io.unobserve(entry.target)
        }
      })
    },
    { rootMargin: '0px 0px -8% 0px', threshold: 0.06 }
  )

  targets.forEach(el => io.observe(el))

  // 兜底：无滚动或观察器未触发时，1.5 秒后也展示内容
  window.setTimeout(showAll, 1500)
})
</script>

<style scoped>
/* ============ 通用区块头 ============ */
.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
}

.section-head-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-ink);
}

.section-sub {
  font-size: 13px;
  color: var(--cl-muted);
}

.section-more {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: var(--cl-brand);
  white-space: nowrap;
  transition: gap 0.2s;
}

.section-more:hover {
  gap: 8px;
}

.section-more img {
  width: 14px;
  height: 14px;
}

/* ============ Hero ============ */
.hero {
  background: var(--cl-white);
}

.hero-row {
  display: flex;
  align-items: stretch;
  gap: 32px;
  padding-top: 44px;
  padding-bottom: 44px;
}

.hero-card {
  position: relative;
  flex: 1 1 auto;
  min-width: 0;
  height: 368px;
  border-radius: var(--cl-radius-2xl);
  overflow: hidden;
  isolation: isolate;
  background: #0a2a20;
}

.hero-photo {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center 42%;
  transform: scale(1.02);
  transition: transform 6s ease-out;
}

.hero-card.in-view .hero-photo {
  transform: scale(1.08);
}

.hero-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    rgba(10, 42, 32, 0.94) 0%,
    rgba(10, 42, 32, 0.82) 42%,
    rgba(10, 42, 32, 0.42) 72%,
    rgba(10, 42, 32, 0) 100%
  );
}

.hero-copy {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
  height: 100%;
  max-width: 620px;
  padding: 40px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  align-self: flex-start;
  height: 24px;
  padding: 0 11px;
  border-radius: var(--cl-radius-2xl);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.15);
  color: var(--cl-on-brand);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.3px;
  backdrop-filter: blur(6px);
}

.hero-badge img {
  width: 12px;
  height: 12px;
}

.hero-title {
  font-size: 46px;
  line-height: 1.12;
  font-weight: 700;
  letter-spacing: -1.4px;
  color: #fff;
}

.hero-lede {
  max-width: 480px;
  font-size: 15px;
  line-height: 1.6;
  color: #d6e7e0;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 6px;
  flex-wrap: wrap;
}

.btn-primary {
  height: 43px;
  padding: 0 24px;
  border-radius: var(--cl-radius-m);
  background: var(--cl-brand);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  transition: background 0.2s, transform 0.2s, box-shadow 0.2s;
}

.btn-primary:hover {
  background: #00b98d;
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 166, 126, 0.35);
}

.btn-ghost {
  height: 43px;
  padding: 0 22px;
  border-radius: var(--cl-radius-m);
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  transition: background 0.2s, transform 0.2s;
}

.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.18);
  transform: translateY(-2px);
}

.hero-trust {
  display: flex;
  align-items: center;
  gap: 22px;
  margin: 4px 0 0;
  padding: 0;
  list-style: none;
  font-size: 12px;
  color: var(--cl-mist-text);
}

.hero-trust li {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.hero-trust li::before {
  content: '';
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: rgba(126, 227, 191, 0.9);
}

/* 右侧列 */
.hero-side {
  width: 330px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.publish-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  height: 168px;
  padding: 22px;
  border-radius: var(--cl-radius-xl);
  background: linear-gradient(135deg, #00a67e 0%, #00805f 100%);
  color: #fff;
  box-shadow: 0 16px 32px rgba(0, 128, 95, 0.18);
}

.publish-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.publish-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.2px;
}

.publish-spark {
  width: 18px;
  height: 18px;
  opacity: 0.95;
}

.publish-desc {
  font-size: 12px;
  line-height: 1.55;
  color: #d3f6ea;
}

.publish-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  height: 38px;
  margin-top: auto;
  border-radius: 9px;
  background: #fff;
  color: var(--cl-brand-strong);
  font-size: 13px;
  font-weight: 600;
  transition: transform 0.18s, box-shadow 0.18s;
}

.publish-btn img {
  width: 15px;
  height: 15px;
  filter: invert(30%) sepia(84%) saturate(1100%) hue-rotate(130deg) brightness(82%);
}

.publish-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.16);
}

.heat-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  height: 161px;
  padding: 22px;
  border-radius: var(--cl-radius-xl);
  border: 1px solid var(--cl-line);
  background: var(--cl-surface);
}

.heat-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.heat-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--cl-ink);
}

.heat-live {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 8px;
  border-radius: 20px;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 10px;
  font-weight: 600;
}

.dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--cl-brand);
  box-shadow: 0 0 0 0 rgba(0, 166, 126, 0.6);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  70% {
    box-shadow: 0 0 0 6px rgba(0, 166, 126, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(0, 166, 126, 0);
  }
}

.heat-stats {
  display: flex;
  align-items: flex-start;
  gap: 18px;
}

.heat-stat {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.heat-num {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-ink);
}

.heat-num.accent {
  color: var(--cl-brand);
}

.heat-label {
  font-size: 11px;
  color: var(--cl-muted);
}

.heat-foot {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto;
  font-size: 11px;
  color: var(--cl-text);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.heat-foot img {
  width: 13px;
  height: 13px;
  flex-shrink: 0;
}

/* ============ 分类 ============ */
.cats {
  padding-bottom: 8px;
}

.cats-inner {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding-top: 8px;
  padding-bottom: 40px;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(8, minmax(0, 1fr));
  gap: 12px;
}

.cat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 18px 12px;
  border: 1px solid var(--cl-line);
  border-radius: var(--cl-radius-l);
  background: #fff;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s;
}

.cat-card:hover {
  transform: translateY(-3px);
  border-color: rgba(0, 166, 126, 0.35);
  box-shadow: 0 14px 28px rgba(15, 27, 22, 0.07);
}

.cat-ico {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 13px;
  background: var(--cl-brand-soft);
  transition: background 0.22s;
}

.cat-ico img {
  width: 21px;
  height: 21px;
}

.cat-card:hover .cat-ico {
  background: #d5f4e7;
}

.cat-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--cl-ink);
  white-space: nowrap;
}

.cat-count {
  font-size: 11px;
  color: var(--cl-muted);
}

.cat-card.all .cat-ico {
  background: var(--cl-ink);
}

.cat-card.all .cat-ico img {
  filter: brightness(0) invert(1);
}

/* ============ 推荐 ============ */
.picks {
  background: #fff;
}

.picks-inner {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding-bottom: 44px;
}

.picks-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.picks-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.picks-tag {
  padding: 3px 9px;
  border-radius: 20px;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 11px;
  font-weight: 600;
}

.sorts {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.sort {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 29px;
  padding: 0 13px;
  border-radius: var(--cl-radius-s);
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12px;
  transition: all 0.18s;
}

.sort:hover {
  border-color: rgba(0, 166, 126, 0.4);
  color: var(--cl-brand-strong);
}

.sort.active {
  background: var(--cl-ink);
  border-color: var(--cl-ink);
  color: #fff;
  font-weight: 600;
}

.sort img {
  width: 13px;
  height: 13px;
}

.filter-wrap {
  position: relative;
}

.filter-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  z-index: 20;
  min-width: 132px;
  padding: 6px;
  border-radius: var(--cl-radius-m);
  border: 1px solid var(--cl-line);
  background: #fff;
  box-shadow: 0 18px 40px rgba(15, 27, 22, 0.12);
}

.filter-opt {
  display: block;
  width: 100%;
  text-align: left;
  padding: 8px 10px;
  border-radius: var(--cl-radius-s);
  font-size: 12px;
  color: var(--cl-text);
}

.filter-opt:hover {
  background: var(--cl-surface);
}

.filter-opt.on {
  color: var(--cl-brand-strong);
  background: var(--cl-brand-soft);
  font-weight: 600;
}

.pop-enter-active,
.pop-leave-active {
  transition: opacity 0.16s, transform 0.16s;
}

.pop-enter-from,
.pop-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.goods-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--cl-line);
  border-radius: var(--cl-radius-l);
  background: #fff;
  cursor: pointer;
  transition: transform 0.24s ease, box-shadow 0.24s ease, border-color 0.24s;
}

.goods-card:hover {
  transform: translateY(-4px);
  border-color: rgba(0, 166, 126, 0.3);
  box-shadow: 0 18px 36px rgba(15, 27, 22, 0.1);
}

.goods-cover {
  position: relative;
  height: 176px;
  overflow: hidden;
  background: var(--cl-surface);
}

.goods-flag {
  position: absolute;
  z-index: 2;
  top: 12px;
  left: 12px;
  padding: 3px 8px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--cl-brand-strong);
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(4px);
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.goods-card:hover .goods-img {
  transform: scale(1.06);
}

.goods-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
}

.goods-name {
  font-size: 14px;
  line-height: 1.45;
  font-weight: 600;
  color: var(--cl-ink);
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
}

.goods-price-row {
  display: flex;
  align-items: flex-end;
  gap: 6px;
  height: 24px;
}

.goods-yen {
  font-size: 12px;
  font-weight: 700;
  color: var(--cl-brand);
  line-height: 1.1;
}

.goods-price {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.4px;
  line-height: 1.1;
  color: var(--cl-brand);
}

.goods-old {
  font-size: 11px;
  color: var(--cl-muted);
  text-decoration: line-through;
  line-height: 1.3;
}

.goods-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 16px;
  overflow: hidden;
}

.goods-pick {
  flex-shrink: 0;
  padding: 1px 6px;
  border-radius: 4px;
  background: var(--cl-surface);
  color: var(--cl-text);
  font-size: 10px;
}

.goods-loc {
  font-size: 11px;
  color: var(--cl-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.goods-seller {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 20px;
  font-size: 11px;
  color: var(--cl-text);
}

.seller-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d7f4e9, #9fe6ca);
  flex-shrink: 0;
}

.seller-name {
  white-space: nowrap;
}

.seller-verified {
  padding: 0 5px;
  border-radius: 4px;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 10px;
  line-height: 15px;
}

.seller-time {
  margin-left: auto;
  color: var(--cl-muted);
  font-size: 10px;
  white-space: nowrap;
}

.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;
  height: 46px;
  border: 1px solid var(--cl-line);
  border-radius: 12px;
  background: var(--cl-surface);
  color: var(--cl-text);
  font-size: 13px;
  transition: background 0.2s, border-color 0.2s;
}

.load-more:hover {
  background: #eef3f0;
  border-color: rgba(0, 166, 126, 0.35);
}

.load-more-link {
  color: var(--cl-brand);
  font-weight: 600;
}

/* ============ 保障 ============ */
.guarantee {
  background: var(--cl-surface);
}

.guarantee-inner {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-top: 44px;
  padding-bottom: 44px;
}

.guarantee-head {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-align: center;
}

.guarantee-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-ink);
}

.guarantee-sub {
  max-width: 760px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--cl-muted);
}

.guarantee-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.guarantee-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 22px;
  border: 1px solid var(--cl-line);
  border-radius: var(--cl-radius-l);
  background: #fff;
  transition: transform 0.22s, box-shadow 0.22s;
}

.guarantee-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 32px rgba(15, 27, 22, 0.07);
}

.guarantee-ico {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 12px;
  background: var(--cl-brand-soft);
}

.guarantee-ico img {
  width: 20px;
  height: 20px;
}

.guarantee-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.guarantee-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--cl-ink);
}

.guarantee-desc {
  font-size: 12px;
  line-height: 1.6;
  color: var(--cl-muted);
}

/* ============ 团队 ============ */
.team {
  background: #fff;
}

.team-inner {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding-top: 44px;
  padding-bottom: 44px;
}

.team-hint {
  font-size: 12px;
  color: var(--cl-muted);
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.flip-card {
  height: 150px;
  perspective: 1200px;
}

.flip-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.55s cubic-bezier(0.2, 0.7, 0.2, 1);
  transform-style: preserve-3d;
}

.flip-card:hover .flip-inner {
  transform: rotateY(180deg);
}

.flip-face {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  border-radius: var(--cl-radius-xl);
  backface-visibility: hidden;
  -Webkit-backface-visibility: hidden;
}

.flip-front {
  border: 1px solid var(--cl-line);
  background: linear-gradient(135deg, #f2fbf7 0%, #e6f7f1 100%);
}

.flip-ico {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 11px;
  background: #fff;
  box-shadow: 0 6px 14px rgba(0, 128, 95, 0.12);
}

.flip-ico img {
  width: 20px;
  height: 20px;
}

.flip-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--cl-ink);
}

.flip-more {
  font-size: 11px;
  color: var(--cl-brand-strong);
}

.flip-back {
  transform: rotateY(180deg);
  background: linear-gradient(135deg, #00a67e 0%, #00805f 100%);
  color: #fff;
}

.flip-back-title {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.2px;
  opacity: 0.85;
}

.flip-back-text {
  font-size: 14px;
  line-height: 1.6;
  text-align: center;
}

/* ============ CTA ============ */
.cta {
  background: #fff;
  border-top: 1px solid var(--cl-line);
}

.cta-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding-top: 40px;
  padding-bottom: 40px;
  flex-wrap: wrap;
}

.cta-copy {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cta-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: var(--cl-ink);
}

.cta-sub {
  font-size: 13px;
  color: var(--cl-muted);
}

.cta-action {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cta-ask {
  font-size: 13px;
  color: var(--cl-text);
}

.cta-btn {
  height: 40px;
  padding: 0 22px;
  border-radius: var(--cl-radius-m);
  background: var(--cl-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.2s, transform 0.2s;
}

.cta-btn:hover {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
}

/* ============ 入场 ============ */
.goods-card {
  opacity: 0;
  transform: translateY(12px);
  transition: opacity 0.5s ease, transform 0.5s ease, border-color 0.24s, box-shadow 0.24s;
}

.goods-card.in-view {
  opacity: 1;
  transform: none;
}

.goods-card.in-view:hover {
  transform: translateY(-4px);
}

/* ============ 响应式 ============ */
@media (max-width: 1180px) {
  .hero-row {
    flex-direction: column;
  }
  .hero-side {
    width: 100%;
    flex-direction: row;
  }
  .publish-card,
  .heat-card {
    flex: 1;
    height: auto;
    min-height: 160px;
  }
  .cat-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 1000px) {
  .goods-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
  .hero-title {
    font-size: 40px;
  }
  .guarantee-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 820px) {
  .goods-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .hero-side {
    flex-direction: column;
  }
  .hero-card {
    height: auto;
    min-height: 420px;
  }
  .hero-shade {
    background: linear-gradient(
      180deg,
      rgba(10, 42, 32, 0.5) 0%,
      rgba(10, 42, 32, 0.86) 55%,
      rgba(10, 42, 32, 0.95) 100%
    );
  }
  .hero-copy {
    justify-content: flex-end;
    padding: 28px 24px;
  }
  .hero-title {
    font-size: 32px;
    letter-spacing: -0.8px;
  }
  .team-grid {
    grid-template-columns: 1fr;
  }
  .cta-inner {
    align-items: flex-start;
  }
}

@media (max-width: 640px) {
  .cat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .goods-grid {
    grid-template-columns: 1fr;
  }
  .goods-cover {
    height: 200px;
  }
  .hero-badge {
    font-size: 10px;
  }
  .hero-title {
    font-size: 27px;
  }
  .hero-lede {
    font-size: 13px;
  }
  .section-title {
    font-size: 20px;
  }
}

/* ============ 接口加载态 / 空态 ============ */
.goods-img-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cl-surface-alt, #f2f4f3);
  color: var(--cl-muted, #8a948f);
  font-size: 12px;
}

.is-skeleton {
  pointer-events: none;
}

.skeleton-cover {
  background: linear-gradient(90deg, #eef1ef 25%, #e2e7e4 37%, #eef1ef 63%);
  background-size: 400% 100%;
  animation: sk-loading 1.4s ease infinite;
}

.skeleton-line {
  display: block;
  height: 12px;
  margin: 10px 0;
  border-radius: 6px;
  background: #eef1ef;
}

.w-70 { width: 70%; }
.w-40 { width: 40%; }
.w-55 { width: 55%; }

.goods-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 0;
  color: #5b6661;
  font-size: 14px;
}

@keyframes sk-loading {
  0% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}
</style>
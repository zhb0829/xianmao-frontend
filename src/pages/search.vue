<template>
  <div class="cl-app search-page">
    <site-header active="shop" />

    <main class="cl-wrap search-main">
      <header class="search-head">
        <p class="eyebrow">SEARCH · 搜索</p>
        <h1>搜索校园闲置</h1>
        <form class="search-box" @submit.prevent="submit">
          <input
            v-model="keyword"
            class="search-input"
            maxlength="30"
            placeholder="搜教材、数码、单车… 试试「键盘」"
            aria-label="搜索商品"
          />
          <button class="search-btn" type="submit">搜索</button>
        </form>
      </header>

      <section v-if="!searched" class="suggest">
        <div class="block">
          <h2>热门搜索</h2>
          <div class="chips">
            <button v-for="w in hot" :key="w" class="chip" @click="pick(w)">{{ w }}</button>
            <span v-if="!hot.length" class="muted">暂无热门词</span>
          </div>
        </div>

        <div v-if="history.length" class="block">
          <div class="block-head">
            <h2>最近搜索</h2>
            <button class="clear" @click="clearHistory">清空</button>
          </div>
          <div class="chips">
            <button v-for="w in history" :key="w" class="chip ghost" @click="pick(w)">{{ w }}</button>
          </div>
        </div>

        <p class="tip">搜索会按商品名称匹配，结果可点击进入详情页下单。</p>
      </section>

      <section v-else class="results">
        <header class="result-head">
          <h2>「{{ activeKeyword }}」的搜索结果</h2>
          <span class="count">{{ loading ? '搜索中…' : total + ' 件在售' }}</span>
        </header>

        <div v-if="loading" class="state">正在搜索…</div>

        <div v-else-if="!list.length" class="state">
          <p>没有找到「{{ activeKeyword }}」相关的闲置</p>
          <p class="muted">换个关键词试试，或者去商品市场按分类逛逛。</p>
          <button class="btn-ghost" @click="$router.push('/shop')">去商品市场</button>
        </div>

        <div v-else class="goods-grid">
          <article
            v-for="g in list"
            :key="g.id"
            class="goods-card"
            @click="$router.push(`/goods/${g.id}`)"
          >
            <div class="cover">
              <img v-if="g.img" :src="g.img" :alt="g.name" />
              <span v-else class="cover-empty">暂无图片</span>
            </div>
            <div class="body">
              <h3 class="g-name">{{ g.name }}</h3>
              <p class="g-price">¥{{ g.price }}</p>
            </div>
          </article>
        </div>

        <div v-if="pages > 1" class="pager">
          <button class="page-btn" :disabled="page <= 1" @click="changePage(page - 1)">上一页</button>
          <span class="page-info">第 {{ page }} / {{ pages }} 页</span>
          <button class="page-btn" :disabled="page >= pages" @click="changePage(page + 1)">下一页</button>
        </div>
      </section>
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'

const HISTORY_KEY = 'xm:search-history'

export default {
  name: 'searchPage',
  components: { siteHeader, siteFooter },
  data() {
    return {
      keyword: '',
      activeKeyword: '',
      hot: [],
      history: this.readHistory(),
      list: [],
      total: 0,
      pages: 1,
      page: 1,
      loading: false,
      searched: false,
    }
  },
  watch: {
    // 页头搜索框或浏览器前进后退改地址时，同步触发搜索
    '$route.query.keyword'(value) {
      const kw = String(value || '').trim()
      if (kw && kw !== this.activeKeyword) {
        this.keyword = kw
        this.doSearch(kw, 1)
      }
      if (!kw) {
        this.searched = false
        this.list = []
      }
    },
  },
  created() {
    this.loadHot()
    const kw = String(this.$route.query.keyword || '').trim()
    if (kw) {
      this.keyword = kw
      this.doSearch(kw, 1)
    }
  },
  methods: {
    readHistory() {
      try {
        const raw = localStorage.getItem(HISTORY_KEY)
        const arr = raw ? JSON.parse(raw) : []
        return Array.isArray(arr) ? arr.slice(0, 8) : []
      } catch {
        return []
      }
    },
    saveHistory(word) {
      const next = [word, ...this.history.filter(w => w !== word)].slice(0, 8)
      this.history = next
      try {
        localStorage.setItem(HISTORY_KEY, JSON.stringify(next))
      } catch {
        /* 隐私模式忽略 */
      }
    },
    clearHistory() {
      this.history = []
      try {
        localStorage.removeItem(HISTORY_KEY)
      } catch {
        /* 忽略 */
      }
    },
    loadHot() {
      this.$http({ method: 'get', url: '/search/hot' })
        .then(res => {
          this.hot = Array.isArray(res.data) ? res.data : []
        })
        .catch(() => {
          this.hot = []
        })
    },
    pick(word) {
      this.keyword = word
      this.submit()
    },
    submit() {
      const kw = this.keyword.trim()
      if (!kw) return
      this.$router.replace({ path: '/search', query: { keyword: kw } }).catch(() => {})
      this.doSearch(kw, 1)
    },
    doSearch(kw, page) {
      this.activeKeyword = kw
      this.searched = true
      this.loading = true
      this.page = page
      this.saveHistory(kw)
      this.$http({
        method: 'get',
        url: '/commodity/getCommodity',
        params: { tradeName: kw, currentPage: page, pageSize: 9, timeFlag: 0, status: '' },
      })
        .then(res => {
          const data = res.data || {}
          const records = Array.isArray(data.records) ? data.records : []
          this.list = records.map(r => ({
            id: r.id,
            name: r.tradeName,
            price: r.price,
            img: '',
          }))
          this.pages = data.pages || 1
          this.total = data.total || this.list.length
          this.loading = false
          this.list.forEach(g => this.fetchPhoto(g))
        })
        .catch(err => {
          this.loading = false
          this.list = []
          this.$emit('showmsg', { info: (err && err.friendly) || '搜索失败', colorType: 3 })
        })
    },
    changePage(next) {
      if (next < 1 || next > this.pages) return
      this.doSearch(this.activeKeyword, next)
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    fetchPhoto(g) {
      this.$http({ method: 'get', url: '/commodity/getFirstPhoto', params: { commodityId: g.id } })
        .then(res => {
          if (typeof res.data === 'string' && res.data.length > 20) {
            g.img = 'data:image/Jpeg;base64,' + res.data
          }
        })
        .catch(() => {})
    },
  },
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.search-main {
  max-width: 1120px;
  margin: 0 auto;
  padding: 40px 20px 72px;
}

.eyebrow {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.14em;
  color: var(--cl-muted);
}

h1 {
  margin: 8px 0 20px;
  font-size: 30px;
  color: var(--cl-ink);
}

.search-box {
  display: flex;
  gap: 10px;
  max-width: 680px;
}

.search-input {
  flex: 1;
  height: 48px;
  padding: 0 18px;
  border-radius: 14px;
  border: 1px solid var(--cl-line);
  background: #fff;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: var(--cl-brand);
}

.search-btn {
  padding: 0 26px;
  height: 48px;
  border: none;
  border-radius: 14px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.suggest {
  margin-top: 34px;
  display: grid;
  gap: 26px;
}

.block h2,
.block-head h2 {
  margin: 0 0 12px;
  font-size: 15px;
  color: var(--cl-ink);
}

.block-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}

.clear {
  border: none;
  background: none;
  color: var(--cl-muted);
  font-size: 12.5px;
  cursor: pointer;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chip {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid transparent;
  background: #e6f2ed;
  color: var(--cl-brand);
  font-size: 13px;
  cursor: pointer;
  transition: transform 0.15s;
}

.chip:hover {
  transform: translateY(-1px);
}

.chip.ghost {
  background: #fff;
  border-color: var(--cl-line);
  color: var(--cl-text-2);
}

.muted {
  font-size: 12.5px;
  color: var(--cl-muted);
}

.tip {
  margin: 6px 0 0;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.results {
  margin-top: 30px;
}

.result-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;
}

.result-head h2 {
  margin: 0;
  font-size: 18px;
  color: var(--cl-ink);
}

.count {
  font-size: 12.5px;
  color: var(--cl-muted);
}

.state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 56px 0;
  font-size: 13.5px;
  color: var(--cl-text-2);
}

.btn-ghost {
  margin-top: 8px;
  padding: 9px 20px;
  border-radius: 999px;
  border: 1px solid var(--cl-line);
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.goods-card {
  border-radius: 18px;
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--cl-line);
  cursor: pointer;
  transition: transform 0.18s, box-shadow 0.18s;
}

.goods-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 36px rgba(9, 24, 17, 0.1);
}

.cover {
  aspect-ratio: 4 / 3;
  background: var(--cl-surface-alt, #f2f4f3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-empty {
  font-size: 12px;
  color: var(--cl-muted);
}

.body {
  padding: 12px 14px 16px;
}

.g-name {
  margin: 0 0 8px;
  font-size: 13.5px;
  line-height: 1.45;
  color: var(--cl-ink);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.g-price {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--cl-brand);
}

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-top: 26px;
}

.page-btn {
  padding: 8px 18px;
  border-radius: 999px;
  border: 1px solid var(--cl-line);
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.page-info {
  font-size: 12.5px;
  color: var(--cl-muted);
}

@media (max-width: 900px) {
  .goods-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
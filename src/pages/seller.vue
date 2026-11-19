<template>
  <div class="cl-app seller-page">
    <site-header active="shop" />

    <main class="cl-wrap seller-main">
      <div v-if="loading" class="state">正在加载卖家主页…</div>

      <div v-else-if="error" class="state">
        <p>{{ error }}</p>
        <button class="btn-primary" @click="$router.push('/shop')">返回商品市场</button>
      </div>

      <template v-else>
        <section class="seller-card">
          <img v-if="seller.avatar" class="avatar" :src="seller.avatar" alt="" />
          <span v-else class="avatar avatar-empty">卖</span>

          <div class="seller-main">
            <h1 class="name">
              {{ seller.nickname }}
              <span class="tag">{{ seller.creditText }}</span>
            </h1>
            <p class="meta">校园认证卖家 · {{ seller.joinedAt }} 加入 · 信用分 {{ creditScore }}</p>
            <p class="intro">{{ seller.intro }}</p>
          </div>

          <div class="stats">
            <div class="stat"><b>{{ seller.listingCount }}</b><span>在售</span></div>
            <div class="stat"><b>{{ seller.soldCount }}</b><span>已售出</span></div>
            <div class="stat"><b>{{ seller.goodsCount }}</b><span>累计发布</span></div>
          </div>
        </section>

        <section class="goods-section">
          <header class="section-head">
            <h2>TA 的在售闲置</h2>
            <span>{{ goods.length }} 件</span>
          </header>

          <div class="goods-grid">
            <article
              v-for="g in goods"
              :key="g.id"
              class="goods-card"
              @click="$router.push(`/goods/${g.id}`)"
            >
              <div class="cover">
                <img v-if="g.img" :src="g.img" :alt="g.tradeName" />
                <span v-else class="cover-empty">暂无图片</span>
              </div>
              <div class="body">
                <h3 class="g-name">{{ g.tradeName }}</h3>
                <p class="g-price">¥{{ g.price }}</p>
                <p class="g-meta">{{ g.category }}</p>
              </div>
            </article>
            <p v-if="!goods.length" class="empty">该卖家暂无可交易的闲置</p>
          </div>
        </section>
      </template>
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'

export default {
  name: 'sellerPage',
  components: { siteHeader, siteFooter },
  data() {
    return {
      loading: true,
      error: '',
      seller: {
        id: 0,
        nickname: '',
        avatar: '',
        rank: 0,
        creditText: '',
        listingCount: 0,
        soldCount: 0,
        goodsCount: 0,
        joinedAt: '',
        intro: '',
      },
      goods: [],
    }
  },
  computed: {
    creditScore() {
      return this.seller.rank ? Math.round(this.seller.rank * 100) : '—'
    },
  },
  watch: {
    '$route.params.id'() {
      this.load()
    },
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      const id = Number(this.$route.params.id)
      if (!id) {
        this.error = '卖家参数无效'
        this.loading = false
        return
      }
      this.loading = true
      this.error = ''
      Promise.all([
        this.$http({ method: 'get', url: '/seller/detail', params: { userId: id } }),
        this.$http({ method: 'get', url: '/seller/goods', params: { userId: id } }),
      ])
        .then(([detail, goods]) => {
          const d = detail.data || {}
          this.seller = {
            ...this.seller,
            ...d,
            // 接口返回纯 base64，前端统一拼 data URI 前缀
            avatar: d.avatar ? 'data:image/Jpeg;base64,' + d.avatar : '',
          }
          this.goods = (Array.isArray(goods.data) ? goods.data : []).map(g => ({ ...g, img: '' }))
          this.loading = false
          this.goods.forEach(g => this.fetchPhoto(g))
        })
        .catch(err => {
          this.error = (err && err.friendly) || '卖家信息加载失败'
          this.loading = false
        })
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
.seller-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.seller-main {
  max-width: 1160px;
  margin: 0 auto;
  padding: 36px 20px 72px;
}

.state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 96px 0;
  color: var(--cl-text-2);
  font-size: 14px;
}

.btn-primary {
  padding: 10px 22px;
  border: none;
  border-radius: 999px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13.5px;
  cursor: pointer;
}

.seller-card {
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 26px 28px;
  border-radius: 22px;
  background: #fff;
  border: 1px solid var(--cl-line);
  box-shadow: 0 18px 44px rgba(9, 24, 17, 0.07);
}

.avatar {
  width: 76px;
  height: 76px;
  border-radius: 50%;
  object-fit: cover;
  flex: none;
}

.avatar-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cl-brand-weak, #e6f2ed);
  color: var(--cl-brand);
  font-size: 24px;
  font-weight: 600;
}

.seller-main {
  flex: 1;
  min-width: 0;
}

.name {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  color: var(--cl-ink);
}

.tag {
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--cl-brand-weak, #e6f2ed);
  color: var(--cl-brand);
  font-size: 12px;
  font-weight: 600;
}

.meta {
  margin: 8px 0 10px;
  font-size: 13px;
  color: var(--cl-muted);
}

.intro {
  margin: 0;
  max-width: 640px;
  font-size: 13.5px;
  line-height: 1.7;
  color: var(--cl-text-2);
}

.stats {
  display: flex;
  gap: 26px;
  flex: none;
}

.stat {
  text-align: center;
}

.stat b {
  display: block;
  font-size: 24px;
  color: var(--cl-ink);
}

.stat span {
  font-size: 12px;
  color: var(--cl-muted);
}

.goods-section {
  margin-top: 34px;
}

.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-head h2 {
  margin: 0;
  font-size: 19px;
  color: var(--cl-ink);
}

.section-head span {
  font-size: 12.5px;
  color: var(--cl-muted);
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
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
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 700;
  color: var(--cl-brand);
}

.g-meta {
  margin: 0;
  font-size: 11.5px;
  color: var(--cl-muted);
}

.empty {
  grid-column: 1 / -1;
  padding: 36px 0;
  text-align: center;
  font-size: 13.5px;
  color: var(--cl-muted);
}

@media (max-width: 980px) {
  .goods-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .seller-card {
    flex-wrap: wrap;
  }
  .stats {
    width: 100%;
    justify-content: space-around;
  }
}
</style>
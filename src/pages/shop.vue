<template>
  <div class="cl-app shop-page">
    <site-header active="shop" />

    <main>
      <!-- 页面头 + 搜索 -->
      <section class="shop-top">
        <div class="cl-wrap shop-top-inner">
          <p class="eyebrow">CAMPUS MARKET · 全校在售</p>
          <h1 class="shop-title">全部商品</h1>
          <p class="shop-sub">
            共 {{ total }} 件在售闲置 · 同校同学当面交易，验货满意再付款
          </p>

          <form class="search" @submit.prevent="doSearch">
            <img class="search-ico" :src="ico.search" alt="" />
            <input v-model="inputText" class="search-input" placeholder="搜索教材、数码、单车… 试试「高数 教材」" />
            <button class="search-btn" type="submit">搜索</button>
          </form>
        </div>
      </section>

      <!-- 筛选与列表 -->
      <section class="cl-wrap shop-body">
        <div class="toolbar">
          <div class="chips">
            <button class="chip" :class="{ on: !category }" @click="fliter('', -1)">全部</button>
            <button
              v-for="(item, index) in categoryList"
              :key="item"
              class="chip"
              :class="{ on: category === item }"
              @click="fliter(item, index)"
            >
              {{ item }}
            </button>
          </div>

          <div class="sorts">
            <button class="sortbtn" :class="{ on: priceFlag >= 0 }" @click="fliter('价格', 4)">
              价格
              <span class="arrow" :class="{ asc: priceFlag > 0, desc: priceFlag === 0 }">↕</span>
            </button>
            <button class="sortbtn" :class="{ on: timeFlag >= 0 }" @click="fliter('时间', 5)">
              时间
              <span class="arrow" :class="{ asc: timeFlag > 0, desc: timeFlag === 0 }">↕</span>
            </button>
          </div>
        </div>

        <!-- 商品网格 -->
        <div v-if="!isLoading" class="grid">
          <article
            v-for="item in goodsList"
            :key="item.goodsId"
            class="card"
            @click="openGoodsDetail(item)"
          >
            <div class="cover">
              <img
                v-if="item.goodsImg"
                class="cover-img"
                :src="'data:image/Jpeg;base64,' + item.goodsImg"
                :alt="item.goodsName"
              />
              <div v-else class="cover-empty">
                <img src="../material/ico/xm-logo.png" alt="" />
              </div>

              <button class="like" :class="{ liked: item.likeStatus }" @click.stop="collect(item.goodsId)">
                <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
                  <path
                    d="M12 20.5s-7.5-4.6-9.3-9A5.2 5.2 0 0 1 12 6.6a5.2 5.2 0 0 1 9.3 4.9c-1.8 4.4-9.3 9-9.3 9z"
                    :fill="item.likeStatus ? 'currentColor' : 'none'"
                    stroke="currentColor"
                    stroke-width="1.6"
                    stroke-linejoin="round"
                  />
                </svg>
                <span>{{ item.likeCount }}</span>
              </button>
            </div>

            <div class="card-body">
              <h3 class="card-name">{{ item.goodsName }}</h3>
              <div class="card-price">
                <span class="yen">¥</span>
                <span class="num">{{ item.goodsPrice }}</span>
              </div>
              <div class="card-meta">
                <span class="meta-pill">校内自提</span>
                <span class="meta-text">已通过平台审核 · 支持验货</span>
              </div>
            </div>
          </article>
        </div>

        <!-- 骨架 -->
        <div v-else class="grid">
          <div v-for="n in 8" :key="n" class="card skeleton">
            <div class="sk-cover"></div>
            <div class="card-body">
              <div class="sk-line w80"></div>
              <div class="sk-line w40"></div>
            </div>
          </div>
        </div>

        <div v-if="!isLoading && !goodsList.length" class="empty">
          <p class="empty-title">没有找到相关商品</p>
          <p class="empty-sub">换个关键词，或者看看其他类目</p>
          <button class="empty-btn" @click="resetAll">查看全部商品</button>
        </div>

        <!-- 分页 -->
        <nav v-if="!isLoading && pageSetting.pageL > 1" class="pager">
          <button class="page-btn nav" :disabled="pageSetting.cNum <= 1" @click="changePage(pageSetting.cNum - 1)">
            上一页
          </button>
          <button
            v-for="p in pageNumbers"
            :key="p"
            class="page-btn"
            :class="{ on: p === pageSetting.cNum }"
            @click="changePage(p)"
          >
            {{ p }}
          </button>
          <button
            class="page-btn nav"
            :disabled="pageSetting.cNum >= pageSetting.pageL"
            @click="changePage(pageSetting.cNum + 1)"
          >
            下一页
          </button>
        </nav>
      </section>
    </main>

    <site-footer />

    <!-- 商品详情 -->
    <Transition name="detail">
      <goodsDetail
        v-if="goodsOpen"
        :gData="goodsData"
        Type="purchase"
        @gclose="closeDetail"
        ref="gD"
        v-bind="$attrs"
      ></goodsDetail>
    </Transition>
  </div>
</template>

<script>
import goodsDetail from '../components/goodsDetail.vue'
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'

export default {
  name: 'shop',
  inheritAttrs: false,
  components: { goodsDetail, siteHeader, siteFooter },
  data() {
    return {
      ico: {
        search: new URL('../assets/cl/ico/search.svg', import.meta.url).href,
      },
      // -1 表示未排序；此后每点一次 +1，奇数次升序、偶数次降序（与后端一致）
      timeFlag: -1,
      priceFlag: -1,
      goodsOpen: 0,
      inputText: '',
      category: '',
      isLoading: true,
      total: 0,
      categoryList: ['电子产品', '生活用品', '学习用品', '美食', '图书文具', '服饰鞋包', '运动户外', '乐器'],
      goodsData: {
        name: '商品名称',
        price: '商品价格',
        likes: '商品收藏数',
        imgL: [],
      },
      goodsList: [],
      pageSetting: {
        cNum: 1,
        pSize: 12,
        tNum: 1,
        pageL: 1,
        currentType: 'all',
      },
    }
  },
  computed: {
    pageNumbers() {
      const totalPages = this.pageSetting.pageL
      const current = this.pageSetting.cNum
      const span = 5
      let start = Math.max(1, current - Math.floor(span / 2))
      let end = Math.min(totalPages, start + span - 1)
      start = Math.max(1, end - span + 1)
      const pages = []
      for (let i = start; i <= end; i++) pages.push(i)
      return pages
    },
  },
  mounted() {
    this.init()
  },
  watch: {
    // 页头/首页搜索会改变 query，需要重新拉取
    '$route.query.keyword'(val) {
      if (val === undefined) return
      this.inputText = val ? String(val) : ''
      this.pageSetting.cNum = 1
      this.search()
    },
    // 首页分类卡会带 category 进入
    '$route.query.category'(val) {
      if (val === undefined) return
      this.category = val ? String(val) : ''
      this.pageSetting.cNum = 1
      this.search()
    },
  },
  methods: {
    openGoodsDetail(item) {
      this.goodsOpen = 1
      this.goodsData.id = item.goodsId
      this.goodsData.name = item.goodsName
      this.goodsData.price = item.goodsPrice
      this.goodsData.likes = item.goodsLikes
      this.goodsData.imgL = []
      this.goodsData.imgL.push(item.goodsImg)
    },
    closeDetail() {
      this.goodsOpen = 0
    },
    collect(id) {
      this.$http({
        method: 'get',
        url: 'shop/collectAdd',
        params: { commodityId: id },
      })
        .then(res => {
          if (res.data == false) {
            for (let i = 0; i < this.goodsList.length; i++) {
              if (this.goodsList[i].goodsId == id) {
                this.goodsList[i].likeCount = this.goodsList[i].likeCount - 1
                this.goodsList[i].likeStatus = false
              }
            }
            this.$emit('showmsg', { info: '取消收藏', colorType: 3 })
          }
          if (res.data == true) {
            for (let i = 0; i < this.goodsList.length; i++) {
              if (this.goodsList[i].goodsId == id) {
                this.goodsList[i].likeCount = this.goodsList[i].likeCount + 1
                this.goodsList[i].likeStatus = true
              }
            }
            this.$emit('showmsg', { info: '加入收藏', colorType: 1 })
          }
        })
        .catch(() => {
          this.$emit('showmsg', { info: '收藏失败', colorType: 3 })
        })
    },
    // index 4/5 分别代表价格、时间排序
    fliter(item, index) {
      if (index === 4) {
        this.priceFlag = this.priceFlag + 1
        this.pageSetting.cNum = 1
        this.search()
      } else if (index === 5) {
        this.timeFlag = this.timeFlag + 1
        this.pageSetting.cNum = 1
        this.search()
      } else {
        this.category = item
        this.pageSetting.cNum = 1
        this.search()
      }
    },
    request_goodsImg(id) {
      this.$http({
        method: 'get',
        url: 'commodity/getFirstPhoto',
        params: { commodityId: id },
      })
        .then(res => {
          const base64Image = res.data
          for (let i = 0; i < this.goodsList.length; i++) {
            if (id == this.goodsList[i].goodsId) {
              this.goodsList[i].goodsImg = base64Image
            }
          }
        })
        .catch(() => {
          this.$emit('showmsg', { info: id + '图片加载失败', colorType: 3 })
        })
    },
    request_goodsLikes(id) {
      this.$http({
        method: 'get',
        url: 'commodity/getCount',
        params: { commodityId: id },
      })
        .then(res => {
          const likeCount = res.data.likeCount
          const likeStatus = res.data.likeStatus
          for (let i = 0; i < this.goodsList.length; i++) {
            if (id == this.goodsList[i].goodsId) {
              this.goodsList[i].likeCount = likeCount
              this.goodsList[i].likeStatus = likeStatus
            }
          }
        })
        .catch(() => {
          this.$emit('showmsg', { info: id + '收藏加载失败', colorType: 3 })
        })
    },
    reflashList(list) {
      this.goodsList = []
      list.forEach(item => {
        const temp = {
          goodsId: item.id,
          goodsName: item.tradeName,
          goodsPrice: item.price,
          likeCount: 0,
          likeStatus: false,
          goodsImg: '',
        }
        this.request_goodsImg(temp.goodsId)
        this.request_goodsLikes(temp.goodsId)
        this.goodsList.push(temp)
      })
    },
    changePage(item) {
      if (item < 1 || item > this.pageSetting.pageL) return
      this.pageSetting.cNum = item
      this.search()
      const el = document.querySelector('.shop-body')
      if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
    },
    // 模糊查询/筛选查询
    search() {
      this.isLoading = true
      this.$http({
        url: '/commodity/getCommodity',
        method: 'get',
        params: {
          timeFlag: this.timeFlag,
          priceFlag: this.priceFlag,
          currentPage: this.pageSetting.cNum,
          pageSize: this.pageSetting.pSize,
          category: this.category,
          tradeName: this.inputText,
          status: '',
        },
      })
        .then(res => {
          if (res.data != false) {
            this.reflashList(res.data.records || [])
            this.pageSetting.pageL = res.data.pages || 1
            this.total = res.data.total != null ? res.data.total : this.pageSetting.pageL * this.pageSetting.pSize
          } else {
            this.$router.push({ path: 'customer' })
          }
          this.isLoading = false
        })
        .catch(err => {
          this.isLoading = false
          this.$emit('showmsg', { info: err, colorType: 3 })
        })
    },
    doSearch() {
      this.pageSetting.cNum = 1
      this.search()
    },
    resetAll() {
      this.inputText = ''
      this.category = ''
      this.priceFlag = -1
      this.timeFlag = -1
      this.pageSetting.cNum = 1
      this.search()
    },
    // 初始化
    init() {
      this.priceFlag = -1
      this.timeFlag = -1
      // 支持从首页/页头带关键词或类目进入
      const q = (this.$route && this.$route.query) || {}
      this.inputText = q.keyword ? String(q.keyword) : ''
      this.category = q.category ? String(q.category) : ''
      this.search()
    },
  },
}
</script>

<style scoped>
.shop-page {
  background: #fff;
}

/* ---------- 页面头 ---------- */
.shop-top {
  background: linear-gradient(180deg, #f6faf8 0%, #ffffff 100%);
  border-bottom: 1px solid var(--cl-line);
}

.shop-top-inner {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding-top: 44px;
  padding-bottom: 32px;
}

.eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1.2px;
  color: var(--cl-brand);
}

.shop-title {
  font-size: 34px;
  font-weight: 700;
  letter-spacing: -1px;
  color: var(--cl-ink);
  margin: 0;
}

.shop-sub {
  font-size: 13px;
  color: var(--cl-muted);
}

.search {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  max-width: 620px;
  height: 48px;
  margin-top: 14px;
  padding: 0 6px 0 16px;
  border: 1px solid var(--cl-line);
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(15, 27, 22, 0.05);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.search:focus-within {
  border-color: rgba(0, 166, 126, 0.55);
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
}

.search-ico {
  width: 17px;
  height: 17px;
  opacity: 0.7;
}

.search-input {
  flex: 1;
  min-width: 0;
  font-size: 14px;
}

.search-input::placeholder {
  color: var(--cl-muted);
}

.search-btn {
  height: 36px;
  padding: 0 22px;
  border-radius: 9px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.2s;
}

.search-btn:hover {
  background: var(--cl-brand-strong);
}

/* ---------- 工具条 ---------- */
.shop-body {
  padding-top: 24px;
  padding-bottom: 56px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--cl-line);
}

.chips {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.chip {
  height: 34px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 13px;
  transition: all 0.18s;
}

.chip:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.chip.on {
  border-color: var(--cl-ink);
  background: var(--cl-ink);
  color: #fff;
  font-weight: 600;
}

.sorts {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sortbtn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 34px;
  padding: 0 14px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  font-size: 13px;
  color: var(--cl-text);
  transition: all 0.18s;
}

.sortbtn:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.sortbtn.on {
  border-color: rgba(0, 166, 126, 0.55);
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-weight: 600;
}

.arrow {
  font-size: 11px;
  opacity: 0.5;
  transition: transform 0.2s, opacity 0.2s;
}

.arrow.asc,
.arrow.desc {
  opacity: 1;
}

.arrow.asc {
  transform: rotate(180deg);
}

/* ---------- 商品网格 ---------- */
.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  padding-top: 20px;
}

.card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--cl-line);
  border-radius: var(--cl-radius-l);
  background: #fff;
  cursor: pointer;
  transition: transform 0.24s ease, box-shadow 0.24s ease, border-color 0.24s;
}

.card:hover {
  transform: translateY(-4px);
  border-color: rgba(0, 166, 126, 0.3);
  box-shadow: 0 18px 36px rgba(15, 27, 22, 0.1);
}

.cover {
  position: relative;
  height: 190px;
  overflow: hidden;
  background: var(--cl-surface);
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.card:hover .cover-img {
  transform: scale(1.05);
}

.cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: var(--cl-surface);
}

.cover-empty img {
  width: 64px;
  opacity: 0.25;
}

.like {
  position: absolute;
  top: 10px;
  right: 10px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 28px;
  padding: 0 9px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: var(--cl-text);
  font-size: 11px;
  backdrop-filter: blur(4px);
  transition: color 0.18s, transform 0.18s;
}

.like:hover {
  transform: scale(1.06);
}

.like.liked {
  color: #e5484d;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
}

.card-name {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  font-weight: 600;
  color: var(--cl-ink);
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}

.card-price {
  display: flex;
  align-items: flex-end;
  gap: 3px;
  color: var(--cl-brand);
}

.yen {
  font-size: 12px;
  font-weight: 700;
}

.num {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.4px;
  line-height: 1.1;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 7px;
  overflow: hidden;
}

.meta-pill {
  flex-shrink: 0;
  padding: 1px 7px;
  border-radius: 4px;
  background: var(--cl-surface);
  color: var(--cl-text);
  font-size: 10px;
}

.meta-text {
  font-size: 11px;
  color: var(--cl-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ---------- 骨架 ---------- */
.skeleton {
  pointer-events: none;
}

.sk-cover {
  height: 190px;
  background: linear-gradient(90deg, #f2f5f3 25%, #e9efec 37%, #f2f5f3 63%);
  background-size: 400% 100%;
  animation: shimmer 1.4s infinite;
}

.sk-line {
  height: 12px;
  border-radius: 6px;
  background: #f1f4f2;
}

.sk-line.w80 {
  width: 80%;
}

.sk-line.w40 {
  width: 40%;
  height: 18px;
}

@keyframes shimmer {
  0% {
    background-position: 100% 0;
  }
  100% {
    background-position: 0 0;
  }
}

/* ---------- 空态 ---------- */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 80px 0;
  text-align: center;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--cl-ink);
}

.empty-sub {
  font-size: 13px;
  color: var(--cl-muted);
}

.empty-btn {
  margin-top: 10px;
  height: 38px;
  padding: 0 20px;
  border-radius: 9px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

/* ---------- 分页 ---------- */
.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding-top: 36px;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 12px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 13px;
  transition: all 0.18s;
}

.page-btn:hover:not(:disabled) {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.page-btn.on {
  background: var(--cl-ink);
  border-color: var(--cl-ink);
  color: #fff;
  font-weight: 600;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-btn.nav {
  padding: 0 14px;
}

/* ---------- 详情过渡 ---------- */
.detail-enter-active,
.detail-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.detail-enter-from,
.detail-leave-to {
  opacity: 0;
  transform: translateY(14px);
}

/* ---------- 响应式 ---------- */
@media (max-width: 1000px) {
  .grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .shop-title {
    font-size: 26px;
  }
  .shop-top-inner {
    padding-top: 28px;
  }
  .cover,
  .sk-cover {
    height: 150px;
  }
}

@media (max-width: 460px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .cover,
  .sk-cover {
    height: 200px;
  }
}
</style>
<template>
  <div class="cl-app goods-page">
    <site-header active="shop" />

    <main class="cl-wrap goods-main">
      <div v-if="loading" class="goods-state">
        <span class="spinner" aria-hidden="true"></span>
        正在加载商品详情…
      </div>

      <div v-else-if="error" class="goods-state">
        <p>{{ error }}</p>
        <div class="goods-state-actions">
          <button class="btn-ghost" @click="load">重新加载</button>
          <button class="btn-primary" @click="$router.push('/shop')">返回商品市场</button>
        </div>
      </div>

      <goods-detail
        v-else
        standalone
        Type="purchase"
        :gData="gData"
        @close-page="$router.push('/shop')"
        @showmsg="passMsg"
      />
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import goodsDetail from '../components/goodsDetail.vue'

export default {
  name: 'goodsPage',
  components: { siteHeader, siteFooter, goodsDetail },
  data() {
    return {
      loading: true,
      error: '',
      gData: { id: 0, name: '商品详情', price: 0, imgL: [] },
    }
  },
  watch: {
    // 详情页之间的卡片跳转会复用同一组件，需要按新 id 重新拉数据
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
        this.error = '商品参数无效'
        this.loading = false
        return
      }
      this.loading = true
      this.error = ''
      this.$http({ method: 'post', url: '/commodity/queryCommodity', params: { commodityId: id } })
        .then(res => {
          const info = res.data || {}
          this.gData = {
            id,
            name: info.tradeName || '商品详情',
            price: Number(info.price) || 0,
            imgL: [],
            classification: info.category || '',
            address: info.shippingAddress || '',
            description: info.description || '',
            likeCount: 0,
            likeStatus: false,
          }
          this.loading = false
        })
        .catch(err => {
          this.error = (err && err.friendly) || '商品加载失败'
          this.loading = false
        })
    },
    passMsg(msg) {
      this.$emit('showmsg', msg)
    },
  },
}
</script>

<style scoped>
.goods-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.goods-main {
  max-width: 1160px;
  margin: 0 auto;
  padding: 24px 20px 64px;
  min-height: 60vh;
}

.goods-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 18px;
  padding: 96px 0;
  color: var(--cl-text-2);
  font-size: 14px;
}

.goods-state-actions {
  display: flex;
  gap: 12px;
}

.btn-primary,
.btn-ghost {
  padding: 10px 22px;
  border-radius: 999px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary {
  border: none;
  background: var(--cl-brand);
  color: #fff;
}

.btn-ghost {
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
}

.spinner {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: 3px solid var(--cl-line);
  border-top-color: var(--cl-brand);
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
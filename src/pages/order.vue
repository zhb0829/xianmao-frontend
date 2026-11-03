<template>
  <div class="cl-app order-page">
    <site-header active="account" />

    <main class="cl-wrap order-main">
      <div v-if="loading" class="state">正在加载订单…</div>

      <div v-else-if="error" class="state">
        <p>{{ error }}</p>
        <button class="btn-primary" @click="$router.push('/customer?tab=7')">返回我的订单</button>
      </div>

      <template v-else>
        <header class="order-head">
          <div>
            <p class="eyebrow">ORDER · 订单</p>
            <h1>订单详情 <span class="oid">#{{ order.id }}</span></h1>
            <p class="sub">下单时间 {{ order.submitTime }} · 当前身份：{{ roleText }}</p>
          </div>
          <div class="pills">
            <span class="pill" :class="orderTone">{{ order.orderStatus }}</span>
            <span class="pill ghost">{{ order.logisticsStatus }}</span>
          </div>
        </header>

        <section class="goods-card">
          <div class="pic">
            <img v-if="pic" :src="pic" :alt="order.name" />
            <span v-else>{{ String(order.name || '商').slice(0, 1) }}</span>
          </div>
          <div class="main">
            <h2>{{ order.name }}</h2>
            <p class="meta">卖家 {{ order.seller }} · 买家 {{ order.buyer }}</p>
            <p class="price">¥{{ order.price }}</p>
          </div>
          <button class="link" @click="$router.push(`/goods/${order.commodityId}`)">查看商品 →</button>
        </section>

        <section class="timeline">
          <h2>订单进度</h2>
          <ol>
            <li
              v-for="(s, i) in steps"
              :key="s.name"
              :class="{ done: s.done, current: i === currentStep }"
            >
              <span class="dot" aria-hidden="true"></span>
              <div class="t-main">
                <p class="t-name">{{ s.name }}</p>
                <p class="t-desc">{{ s.desc }}</p>
              </div>
            </li>
          </ol>
        </section>

        <section class="actions">
          <button v-if="canPay" class="btn-primary" @click="pay">去支付</button>
          <button v-if="canReceive" class="btn-primary" @click="receive">确认收货</button>
          <button v-if="canDeliver" class="btn-primary" @click="deliver">去发货</button>
          <button v-if="isBuyer" class="btn-ghost danger" @click="removeOrder">删除订单</button>
          <span v-if="!canPay && !canReceive && !canDeliver && !isBuyer" class="hint">
            当前账号不是该订单的买卖双方，仅可查看
          </span>
        </section>

        <section class="review">
          <header class="review-head">
            <h2>买家评价</h2>
            <span v-if="order.review" class="score">{{ order.review.score }}.0 分</span>
          </header>

          <div v-if="order.review" class="review-done">
            <p class="stars">{{ stars(order.review.score) }}</p>
            <p class="content">{{ order.review.content }}</p>
            <p class="time">{{ order.review.authorName }} · {{ order.review.time }}</p>
          </div>

          <form v-else-if="canReview" class="review-form" @submit.prevent="submitReview">
            <p class="pick-label">给这次交易打个分</p>
            <div class="star-picker">
              <button
                v-for="n in 5"
                :key="n"
                type="button"
                class="star"
                :class="{ on: n <= score }"
                :aria-label="n + ' 星'"
                @click="score = n"
              >
                ★
              </button>
            </div>
            <textarea
              v-model="content"
              rows="3"
              maxlength="200"
              placeholder="说说商品成色、交易过程和卖家服务…"
            ></textarea>
            <div class="form-foot">
              <span class="count">{{ content.length }} / 200</span>
              <button type="submit" class="btn-primary" :disabled="!content.trim() || submitting">
                {{ submitting ? '提交中…' : '提交评价' }}
              </button>
            </div>
          </form>

          <p v-else class="review-hint">{{ reviewHint }}</p>
        </section>
      </template>
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import { useUserStore } from '../stores/user'

export default {
  name: 'orderPage',
  components: { siteHeader, siteFooter },
  data() {
    return {
      loading: true,
      error: '',
      pic: '',
      order: {
        id: 0,
        commodityId: 0,
        name: '',
        price: 0,
        seller: '',
        buyer: '',
        orderStatus: '',
        logisticsStatus: '',
        submitTime: '',
        role: 'viewer',
        review: null,
      },
      score: 5,
      content: '',
      submitting: false,
    }
  },
  computed: {
    isBuyer() {
      return this.order.role === 'buyer'
    },
    isSeller() {
      return this.order.role === 'seller'
    },
    roleText() {
      return { buyer: '买家', seller: '卖家', viewer: '旁观' }[this.order.role] || '买家'
    },
    canPay() {
      return this.isBuyer && this.order.orderStatus === '未支付'
    },
    canReceive() {
      return this.isBuyer && this.order.orderStatus === '已支付' && this.order.logisticsStatus === '已发货'
    },
    canDeliver() {
      return this.isSeller && this.order.logisticsStatus === '未发货'
    },
    canReview() {
      if (!this.isBuyer) return false
      const finished =
        this.order.orderStatus === '已完成' || this.order.logisticsStatus === '已签收'
      return finished && !this.order.review
    },
    reviewHint() {
      if (this.order.review) return ''
      if (this.isSeller) return '只有买家可以在交易完成后评价订单。'
      if (!this.isBuyer) return '只有订单买家可以评价。'
      return '订单完成后（确认收货）即可评价这笔交易。'
    },
    orderTone() {
      const s = this.order.orderStatus
      if (s === '已完成') return 'done'
      if (s === '已支付') return 'paid'
      if (s === '未支付') return 'wait'
      return ''
    },
    steps() {
      const paid = ['已支付', '已完成'].includes(this.order.orderStatus)
      const delivered = ['已发货', '已签收'].includes(this.order.logisticsStatus)
      const received = this.order.logisticsStatus === '已签收' || this.order.orderStatus === '已完成'
      return [
        { name: '提交订单', desc: this.order.submitTime || '——', done: true },
        { name: '买家支付', desc: paid ? '已完成支付（演示为模拟支付）' : '等待买家支付', done: paid },
        { name: '卖家发货', desc: delivered ? '卖家已发货' : '等待卖家发货', done: delivered },
        { name: '确认收货', desc: received ? '交易完成，可评价' : '等待买家确认收货', done: received },
      ]
    },
    currentStep() {
      const doneIndex = this.steps.reduce((acc, s, i) => (s.done ? i : acc), 0)
      return Math.min(doneIndex + 1, this.steps.length - 1)
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
    stars(n) {
      return '★'.repeat(n) + '☆'.repeat(Math.max(0, 5 - n))
    },
    load() {
      const id = Number(this.$route.params.id)
      if (!id) {
        this.error = '订单参数无效'
        this.loading = false
        return
      }
      this.loading = true
      this.error = ''
      this.$http({ method: 'get', url: '/order/detail', params: { orderId: id } })
        .then(res => {
          this.order = { ...this.order, ...(res.data || {}) }
          this.loading = false
          this.fetchPicture()
        })
        .catch(err => {
          this.error = (err && err.friendly) || '订单加载失败'
          this.loading = false
        })
    },
    fetchPicture() {
      if (!this.order.commodityId) return
      this.$http({ method: 'get', url: '/commodity/getFirstPhoto', params: { commodityId: this.order.commodityId } })
        .then(res => {
          if (typeof res.data === 'string' && res.data.length > 20) {
            this.pic = 'data:image/Jpeg;base64,' + res.data
          }
        })
        .catch(() => {})
    },
    pay() {
      this.$http({ method: 'get', url: '/shop/payment', params: { orderId: this.order.id } })
        .then(() => {
          this.$emit('showmsg', { info: '支付成功（演示）', colorType: 1 })
          this.load()
        })
        .catch(() => this.$emit('showmsg', { info: '支付失败', colorType: 3 }))
    },
    receive() {
      this.$http({ method: 'get', url: '/person/receive', params: { orderId: this.order.id } })
        .then(() => {
          this.$emit('showmsg', { info: '确认收货成功，可以评价了', colorType: 1 })
          this.load()
        })
        .catch(() => this.$emit('showmsg', { info: '确认收货失败', colorType: 3 }))
    },
    deliver() {
      this.$http({ method: 'get', url: '/shop/confirmDeliverGood', params: { orderId: this.order.id } })
        .then(() => {
          this.$emit('showmsg', { info: '已标记发货', colorType: 1 })
          this.load()
        })
        .catch(() => this.$emit('showmsg', { info: '发货失败', colorType: 3 }))
    },
    removeOrder() {
      if (!window.confirm('删除后订单记录不可恢复，确定删除吗？')) return
      this.$http({ method: 'post', url: '/person/deleteOrder', params: { orderId: this.order.id } })
        .then(() => {
          this.$emit('showmsg', { info: '订单已删除', colorType: 1 })
          this.$router.replace('/customer?tab=7')
        })
        .catch(() => this.$emit('showmsg', { info: '删除失败', colorType: 3 }))
    },
    submitReview() {
      const content = this.content.trim()
      if (!content || this.submitting) return
      this.submitting = true
      this.$http({
        method: 'post',
        url: '/review/add',
        params: { orderId: this.order.id, score: this.score, content },
      })
        .then(res => {
          this.submitting = false
          this.order.review = res.data
          this.content = ''
          this.$emit('showmsg', { info: '评价已提交，感谢反馈', colorType: 1 })
        })
        .catch(err => {
          this.submitting = false
          this.$emit('showmsg', { info: (err && err.friendly) || '评价提交失败', colorType: 3 })
        })
    },
  },
}
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.order-main {
  max-width: 960px;
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
  padding: 10px 24px;
  border: none;
  border-radius: 999px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13.5px;
  cursor: pointer;
}

.btn-primary:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.btn-ghost {
  padding: 10px 22px;
  border: 1px solid var(--cl-line);
  border-radius: 999px;
  background: #fff;
  color: var(--cl-text);
  font-size: 13.5px;
  cursor: pointer;
}

.btn-ghost.danger {
  color: #b4453a;
  border-color: #f0d6d2;
}

.order-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
}

.eyebrow {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.14em;
  color: var(--cl-muted);
}

h1 {
  margin: 8px 0 8px;
  font-size: 26px;
  color: var(--cl-ink);
}

.oid {
  font-size: 15px;
  color: var(--cl-muted);
  font-weight: 500;
}

.sub {
  margin: 0;
  font-size: 13px;
  color: var(--cl-muted);
}

.pills {
  display: flex;
  gap: 8px;
  flex: none;
}

.pill {
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 12.5px;
  background: var(--cl-surface-alt, #eef2f0);
  color: var(--cl-text-2);
}

.pill.paid {
  background: #e6f2ed;
  color: var(--cl-brand);
}

.pill.done {
  background: #e8eef7;
  color: #2f5f9e;
}

.pill.wait {
  background: #fdf0e3;
  color: #b06a1f;
}

.pill.ghost {
  background: #fff;
  border: 1px solid var(--cl-line);
}

.goods-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid var(--cl-line);
}

.pic {
  width: 76px;
  height: 76px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--cl-surface-alt, #f2f4f3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--cl-muted);
  flex: none;
}

.pic img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.main {
  flex: 1;
  min-width: 0;
}

.main h2 {
  margin: 0 0 6px;
  font-size: 16px;
  color: var(--cl-ink);
}

.meta {
  margin: 0 0 6px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.price {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--cl-brand);
}

.link {
  border: none;
  background: none;
  color: var(--cl-brand);
  font-size: 13px;
  cursor: pointer;
  flex: none;
}

.timeline {
  margin-top: 22px;
  padding: 20px 22px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid var(--cl-line);
}

.timeline h2 {
  margin: 0 0 16px;
  font-size: 16px;
  color: var(--cl-ink);
}

.timeline ol {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 14px;
}

.timeline li {
  display: flex;
  gap: 12px;
  opacity: 0.55;
}

.timeline li.done,
.timeline li.current {
  opacity: 1;
}

.dot {
  width: 10px;
  height: 10px;
  margin-top: 5px;
  border-radius: 50%;
  background: var(--cl-line);
  flex: none;
}

li.done .dot {
  background: var(--cl-brand);
}

li.current .dot {
  background: var(--cl-brand);
  box-shadow: 0 0 0 4px rgba(0, 128, 95, 0.16);
}

.t-name {
  margin: 0 0 2px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--cl-ink);
}

.t-desc {
  margin: 0;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 18px;
}

.hint {
  font-size: 12.5px;
  color: var(--cl-muted);
}

.review {
  margin-top: 22px;
  padding: 20px 22px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid var(--cl-line);
}

.review-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 14px;
}

.review-head h2 {
  margin: 0;
  font-size: 16px;
  color: var(--cl-ink);
}

.score {
  font-size: 13px;
  color: var(--cl-brand);
  font-weight: 600;
}

.review-done .stars {
  margin: 0 0 8px;
  color: #d99b2b;
  font-size: 16px;
  letter-spacing: 2px;
}

.review-done .content {
  margin: 0 0 8px;
  font-size: 13.5px;
  line-height: 1.75;
  color: var(--cl-text);
}

.review-done .time,
.review-hint {
  margin: 0;
  font-size: 12px;
  color: var(--cl-muted);
}

.pick-label {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--cl-text-2);
}

.star-picker {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

.star {
  border: none;
  background: none;
  font-size: 26px;
  line-height: 1;
  color: #d9ddd8;
  cursor: pointer;
  transition: color 0.15s;
}

.star.on {
  color: #e0a63a;
}

.review-form textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--cl-line);
  background: #fff;
  font-size: 13.5px;
  line-height: 1.7;
  color: var(--cl-text);
  resize: vertical;
  outline: none;
}

.review-form textarea:focus {
  border-color: var(--cl-brand);
}

.form-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}

.count {
  font-size: 12px;
  color: var(--cl-muted);
}

@media (max-width: 720px) {
  .order-head {
    flex-direction: column;
  }
  .goods-card {
    flex-wrap: wrap;
  }
}
</style>
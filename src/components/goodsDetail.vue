<template>
  <div class="gd-layer">
    <div class="gd-mask" @click="close()"></div>

    <div class="gd-dialog" role="dialog" aria-modal="true">
      <button class="gd-close" aria-label="关闭" @click="close()">
        <span v-html="ICON.close"></span>
      </button>

      <Transition :name="pageAni" mode="out-in">
        <!-- ================= 商品详情 ================= -->
        <div v-if="pageIndex === 1" class="gd-panel" key="detail">
          <div class="gd-gallery">
            <div class="gd-main">
              <img v-if="goodsData.imgL.length" :src="goodsData.imgL[bigImgIndex]" alt="" />
              <span v-else class="gd-placeholder">暂无图片</span>
            </div>

            <div class="gd-thumbs" v-if="goodsData.imgL.length > 1">
              <button
                v-for="(img, i) in goodsData.imgL"
                :key="i"
                class="gd-thumb"
                :class="{ on: i === bigImgIndex }"
                @click="changeImgByIndex(i)"
              >
                <img :src="img" alt="" />
              </button>
            </div>
          </div>

          <div class="gd-info">
            <p class="gd-cat">{{ goodsData.classification || '校园闲置' }}</p>
            <h2 class="gd-name">{{ goodsData.name }}</h2>
            <p class="gd-price">¥{{ formatPrice(goodsData.price) }}</p>

            <p class="gd-addr">
              <span class="gd-ico" v-html="ICON.pin"></span>
              {{ goodsData.address || '校园自提' }}
            </p>

            <p class="gd-desc">{{ goodsData.description }}</p>

            <div class="gd-foot">
              <button class="gd-like" :class="{ on: goodsData.likeStatus }" @click="collect(goodsData.id)">
                <span class="gd-ico" v-html="goodsData.likeStatus ? ICON.heartSolid : ICON.heart"></span>
                {{ goodsData.likeStatus ? '已收藏' : '收藏' }}
                <b>{{ goodsData.likeCount }}</b>
              </button>

              <button v-if="Type === 'purchase'" class="gd-primary" @click="buy()">
                {{ purchaseMsg }}
              </button>
            </div>
          </div>
        </div>

        <!-- ================= 确认订单 ================= -->
        <div v-else-if="pageIndex === 2" class="gd-panel gd-panel-single" key="confirm">
          <h2 class="gd-step">确认你的订单</h2>

          <div class="gd-order-row">
            <img v-if="goodsData.imgL.length" class="gd-order-img" :src="goodsData.imgL[0]" alt="" />
            <span v-else class="gd-order-img gd-placeholder">图</span>
            <div class="gd-order-main">
              <p class="gd-order-name">{{ goodsData.name }}</p>
              <p class="gd-order-meta">卖家 {{ seller.name }}</p>
            </div>
            <p class="gd-order-price">¥{{ formatPrice(goodsData.price) }}</p>
          </div>

          <div class="gd-form">
            <div class="gd-form-row">
              <span class="gd-label">买家</span>
              <span class="gd-value">{{ customer.name }}</span>
            </div>
            <div class="gd-form-row">
              <span class="gd-label">收货地址</span>
              <span class="gd-value">{{ addressText }}</span>
            </div>
            <div class="gd-form-row">
              <span class="gd-label">成交方式</span>
              <span class="gd-value">校内当面交易 / 平台担保</span>
            </div>
          </div>

          <div class="gd-actions">
            <button class="gd-secondary" @click="previousPage()">上一页</button>
            <button class="gd-primary" @click="buy()">{{ purchaseMsg }}</button>
          </div>
        </div>

        <!-- ================= 提交成功 ================= -->
        <div v-else class="gd-panel gd-panel-single gd-done" key="done">
          <span class="gd-check" v-html="ICON.checkCircle"></span>
          <h2>订单已提交</h2>
          <p class="gd-done-desc">{{ seller.communication }}</p>
          <p class="gd-done-order">订单号 <b>{{ purchaseInfo.purchaseId }}</b></p>

          <button class="gd-primary" @click="close()">完成</button>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script>
const svg = d =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;

const ICON = {
  close: svg('<path d="M6 6l12 12M18 6L6 18"/>'),
  pin: svg('<path d="M12 20.6s6.6-5.2 6.6-10.6a6.6 6.6 0 1 0-13.2 0c0 5.4 6.6 10.6 6.6 10.6z"/><circle cx="12" cy="9.8" r="2.4"/>'),
  heart: svg('<path d="M20.4 5.9a4.9 4.9 0 0 0-6.9 0L12 7.4l-1.5-1.5a4.9 4.9 0 1 0-6.9 6.9L12 21l8.4-8.2a4.9 4.9 0 0 0 0-6.9z"/>'),
  heartSolid: '<svg viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M20.4 5.9a4.9 4.9 0 0 0-6.9 0L12 7.4l-1.5-1.5a4.9 4.9 0 1 0-6.9 6.9L12 21l8.4-8.2a4.9 4.9 0 0 0 0-6.9z"/></svg>',
  checkCircle: svg('<circle cx="12" cy="12" r="9"/><path d="m8.4 12.2 2.6 2.6 5-5.2"/>'),
};

export default {
  name: 'goodsDetail',
  props: ['gData', 'Type', 'gclose'],
  data() {
    return {
      ICON,
      orderid: '',
      pageAni: 'toRight',
      purchaseMsg: '购买',
      pageIndex: 1,
      bigImgIndex: 0,
      circulate: '',
      currentAddress: 0,
      openAdMenu: 0,
      goodsData: {
        description: '这是一段有关商品的描述文字',
        id: this.gData.id,
        name: this.gData.name,
        price: this.gData.price,
        likeCount: 0,
        likeStatus: false,
        classification: '商品分类',
        imgL: [],
        address: '商品默认地址',
      },
      seller: {
        name: '示例卖家',
        communication: '联系方式: example@test.com',
        address: '示例地址',
      },
      customer: {
        name: '这里是买家名字',
        address: ['收货地址1', '收货地址2', '收货地址3'],
      },
      purchaseInfo: {
        purchaseId: 88887776,
        purchaseTime: '',
      },
      times: '',
    };
  },
  computed: {
    addressText() {
      const a = this.customer.address;
      if (Array.isArray(a)) return a[this.currentAddress] || a[0] || '未设置';
      return a || '未设置';
    },
  },
  mounted() {
    if (this.Type == 'purchase') {
      this.circulate = 1;
    } else if (this.Type == 'browse') {
      this.circulate = 0;
    }
    this.initGoods();
  },
  methods: {
    formatPrice(v) {
      const n = Number(v);
      return Number.isFinite(n) ? n.toLocaleString('zh-CN', { maximumFractionDigits: 2 }) : v;
    },
    collect(id) {
      this.$http({ method: 'get', url: 'shop/collectAdd', params: { commodityId: id } })
        .then(res => {
          if (res.data == true) {
            this.goodsData.likeCount += 1;
            this.goodsData.likeStatus = true;
            this.$emit('showmsg', { info: '已加入收藏', colorType: 1 });
          } else if (res.data == false) {
            this.goodsData.likeCount = Math.max(0, this.goodsData.likeCount - 1);
            this.goodsData.likeStatus = false;
            this.$emit('showmsg', { info: '已取消收藏', colorType: 3 });
          }
        })
        .catch(() => {
          this.$emit('showmsg', { info: '收藏失败', colorType: 3 });
        });
    },
    close() {
      this.$emit('gclose');
    },
    changeImgByIndex(ind) {
      this.bigImgIndex = ind;
    },
    buy() {
      if (this.pageIndex < 3) {
        if (this.pageIndex == 1) {
          this.purchaseMsg = '确定';
          this.$http({ method: 'post', url: 'shop/generateOrder', params: { commodityId: this.goodsData.id } })
            .then(res => {
              this.seller.name = res.data.sellerName;
              this.customer.address = res.data.address;
              this.customer.name = res.data.userName;
              this.orderid = res.data.id;
            })
            .catch(() => {
              this.$emit('showmsg', { info: '下单失败', colorType: 3 });
            });
        } else if (this.pageIndex == 2) {
          this.purchaseMsg = '确定';
          this.$http({ method: 'get', url: 'shop/payment', params: { orderId: this.orderid } })
            .then(res => {
              this.seller.communication = res.data;
              this.purchaseInfo.purchaseId = this.orderid;
            })
            .catch(() => {
              this.$emit('showmsg', { info: '支付失败', colorType: 3 });
            });
        }
        this.pageIndex += 1;
      } else {
        this.close();
      }
    },
    previousPage() {
      if (this.pageIndex >= 2) {
        if (this.pageIndex == 2) this.purchaseMsg = '购买';
        this.pageIndex -= 1;
      }
    },
    openAd() {
      this.openAdMenu = !this.openAdMenu;
    },
    selectAd(ind) {
      this.currentAddress = ind;
      this.openAd();
    },
    getCurrentTime() {
      const now = new Date();
      return { year: now.getFullYear(), month: now.getMonth() + 1, day: now.getDate() };
    },
    initGoods() {
      this.$http({ method: 'post', url: '/commodity/queryCommodity', params: { commodityId: this.goodsData.id } })
        .then(res => {
          const info = res.data || {};
          const preurl = 'data:image/Jpeg;base64,';
          (info.slideShow || []).forEach(item => this.goodsData.imgL.push(preurl + item));
          this.goodsData.description = info.description || this.goodsData.description;
          this.goodsData.classification = info.category || this.goodsData.classification;
          this.goodsData.address = info.shippingAddress || this.goodsData.address;
        })
        .catch(() => {
          this.$emit('showmsg', { info: '商品详情加载失败', colorType: 3 });
        });

      this.$http({ method: 'get', url: 'commodity/getCount', params: { commodityId: this.goodsData.id } })
        .then(res => {
          const d = res.data || {};
          this.goodsData.likeCount = d.likeCount || 0;
          this.goodsData.likeStatus = !!d.likeStatus;
        })
        .catch(() => {
          this.$emit('showmsg', { info: '收藏信息加载失败', colorType: 3 });
        });
    },
  },
  watch: {
    pageIndex(newVal, oldVal) {
      this.pageAni = newVal < oldVal ? 'toRight' : 'toLeft';
    },
  },
};
</script>

<style scoped>
.gd-layer {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.gd-mask {
  position: absolute;
  inset: 0;
  background: rgba(10, 20, 16, 0.55);
  backdrop-filter: blur(3px);
  animation: maskIn 0.2s ease-out;
}

@keyframes maskIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.gd-dialog {
  position: relative;
  width: min(940px, 100%);
  max-height: min(86vh, 720px);
  overflow: auto;
  background: #fff;
  border-radius: 22px;
  box-shadow: 0 34px 90px rgba(8, 20, 14, 0.35);
  animation: dialogIn 0.24s ease-out;
}

@keyframes dialogIn {
  from {
    opacity: 0;
    transform: translateY(14px) scale(0.985);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

.gd-close {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 3;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid var(--cl-line);
  color: var(--cl-text);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.18s, color 0.18s;
}

.gd-close:hover {
  background: var(--cl-surface);
  color: var(--cl-ink);
}

.gd-close :deep(svg) {
  width: 16px;
  height: 16px;
}

.gd-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 1fr);
  gap: 26px;
  padding: 26px;
}

.gd-panel-single {
  display: block;
  max-width: 620px;
  margin: 0 auto;
  padding: 34px 30px 30px;
}

/* 图片区 */
.gd-gallery {
  min-width: 0;
}

.gd-main {
  width: 100%;
  aspect-ratio: 4 / 3.2;
  border-radius: 16px;
  overflow: hidden;
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.gd-main img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.gd-placeholder {
  font-size: 13px;
  color: var(--cl-muted);
}

.gd-thumbs {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  overflow-x: auto;
  padding-bottom: 2px;
}

.gd-thumb {
  flex-shrink: 0;
  width: 62px;
  height: 62px;
  border-radius: 10px;
  overflow: hidden;
  border: 2px solid transparent;
  background: var(--cl-surface);
  padding: 0;
  opacity: 0.75;
  transition: opacity 0.18s, border-color 0.18s;
}

.gd-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.gd-thumb:hover {
  opacity: 1;
}

.gd-thumb.on {
  border-color: var(--cl-brand);
  opacity: 1;
}

/* 信息区 */
.gd-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.gd-cat {
  display: inline-flex;
  align-self: flex-start;
  height: 23px;
  padding: 0 10px;
  border-radius: 999px;
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
  font-size: 11.5px;
  align-items: center;
}

.gd-name {
  margin-top: 12px;
  font-size: 22px;
  line-height: 1.35;
  font-weight: 700;
  letter-spacing: -0.6px;
  color: var(--cl-ink);
}

.gd-price {
  margin-top: 12px;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -1px;
  color: var(--cl-brand-strong);
}

.gd-addr {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.gd-ico {
  display: inline-flex;
  color: var(--cl-muted-2);
}

.gd-ico :deep(svg) {
  width: 14px;
  height: 14px;
}

.gd-desc {
  margin-top: 14px;
  font-size: 13px;
  line-height: 1.75;
  color: var(--cl-text);
  max-height: 132px;
  overflow: auto;
}

.gd-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: auto;
  padding-top: 20px;
}

.gd-like {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 42px;
  padding: 0 16px;
  border-radius: 10px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 13px;
  transition: all 0.18s;
}

.gd-like :deep(svg) {
  width: 16px;
  height: 16px;
}

.gd-like b {
  font-weight: 600;
  color: var(--cl-muted);
}

.gd-like:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.gd-like.on {
  border-color: rgba(0, 166, 126, 0.5);
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
}

.gd-primary {
  flex: 1;
  height: 42px;
  padding: 0 22px;
  border-radius: 10px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13.5px;
  font-weight: 600;
  transition: background 0.18s, transform 0.18s;
}

.gd-primary:hover {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
}

.gd-secondary {
  height: 42px;
  padding: 0 20px;
  border-radius: 10px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 13px;
  transition: all 0.18s;
}

.gd-secondary:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

/* 确认订单 */
.gd-step {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-ink);
}

.gd-order-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 20px;
  padding: 14px;
  border-radius: 14px;
  background: var(--cl-surface);
}

.gd-order-img {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  object-fit: cover;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--cl-muted);
}

.gd-order-main {
  flex: 1;
  min-width: 0;
}

.gd-order-name {
  font-size: 14px;
  color: var(--cl-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.gd-order-meta {
  margin-top: 4px;
  font-size: 12px;
  color: var(--cl-muted);
}

.gd-order-price {
  font-size: 17px;
  font-weight: 700;
  color: var(--cl-brand-strong);
}

.gd-form {
  margin-top: 16px;
  border-top: 1px solid var(--cl-line);
}

.gd-form-row {
  display: flex;
  gap: 20px;
  padding: 13px 2px;
  border-bottom: 1px solid #f1f4f2;
  font-size: 13px;
}

.gd-label {
  width: 76px;
  flex-shrink: 0;
  color: var(--cl-muted);
}

.gd-value {
  flex: 1;
  color: var(--cl-ink);
  line-height: 1.6;
}

.gd-actions {
  display: flex;
  gap: 10px;
  margin-top: 22px;
}

/* 成功页 */
.gd-done {
  text-align: center;
  padding: 46px 30px 36px;
}

.gd-check {
  display: inline-flex;
  color: var(--cl-brand);
}

.gd-check :deep(svg) {
  width: 54px;
  height: 54px;
}

.gd-done h2 {
  margin-top: 16px;
  font-size: 21px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-ink);
}

.gd-done-desc {
  margin-top: 10px;
  font-size: 13px;
  color: var(--cl-text);
}

.gd-done-order {
  margin-top: 6px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.gd-done-order b {
  color: var(--cl-ink);
  font-weight: 600;
}

.gd-done .gd-primary {
  flex: none;
  margin-top: 22px;
  min-width: 160px;
}

/* 页面切换 */
.toLeft-enter-active,
.toLeft-leave-active,
.toRight-enter-active,
.toRight-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.toLeft-enter-from,
.toRight-leave-to {
  opacity: 0;
  transform: translateX(14px);
}

.toLeft-leave-to,
.toRight-enter-from {
  opacity: 0;
  transform: translateX(-14px);
}

@media (max-width: 860px) {
  .gd-panel {
    grid-template-columns: 1fr;
    gap: 18px;
    padding: 18px;
  }
  .gd-main {
    aspect-ratio: 16 / 10;
  }
}
</style>
<template>
  <div class="my-status">
    <!-- 列表切换 -->
    <div class="status-tabs">
      <button
        v-for="t in tabs"
        :key="t.id"
        class="status-tab"
        :class="{ on: indx === t.id }"
        @click="switchList(t.id)"
      >
        {{ t.label }}<em>{{ t.count }}</em>
      </button>
    </div>

    <!-- 收藏 / 我发布的：商品卡片 -->
    <div v-if="indx === 1 || indx === 4" class="goods-grid">
      <div class="goods-card" v-for="(item, index) in goodsList" :key="item.id">
        <div class="goods-pic">
          <img v-if="item.pic" :src="'data:image/Jpeg;base64,' + item.pic" :alt="item.name" />
          <span v-else class="pic-fallback">{{ String(item.name).slice(0, 1) }}</span>
        </div>

        <div class="goods-body">
          <p class="goods-name">{{ item.name }}</p>
          <p class="goods-price"><b>¥{{ item.price }}</b><span>{{ statusText(item.status) }}</span></p>

          <div class="goods-ops">
            <button class="op-btn" @click="openGoods(item, index)">查看详情</button>
            <button v-if="indx === 1" class="op-btn danger" @click="dislikeGoods(item, index)">
              取消收藏
            </button>
            <button
              v-else-if="item.status === '通过'"
              class="op-btn danger"
              @click="deleteGoods(item, index)"
            >
              下架
            </button>
          </div>
        </div>
      </div>

      <loading v-if="listIsLoading"></loading>
      <p class="empty" v-if="!listIsLoading && !goodsList.length">{{ emptyText }}</p>
    </div>

    <!-- 我买到的 / 我卖出的：订单行 -->
    <div v-else class="order-list">
      <div class="order-row" v-for="(item, index) in cList" :key="item.oid || item.id">
        <div class="order-pic">
          <img v-if="item.pic" :src="'data:image/Jpeg;base64,' + item.pic" :alt="item.name" />
          <span v-else class="pic-fallback">{{ String(item.name).slice(0, 1) }}</span>
        </div>

        <div class="order-main">
          <p class="order-name">{{ item.name }}</p>
          <p class="order-meta">
            {{ indx === 2 ? '卖家' : '买家' }} {{ item.seller }} · {{ item.time }}
          </p>
        </div>

        <div class="order-state">
          <span class="state-pill" :class="stateTone(item.orderStatus)">{{ item.orderStatus }}</span>
          <span class="state-sub">{{ item.logisticsStatus }}</span>
        </div>

        <div class="order-ops">
          <button
            v-if="item.orderStatus === '未支付' && indx === 2"
            class="op-btn primary"
            @click="pay(item.oid, index)"
          >
            去支付
          </button>
          <button
            v-if="item.logisticsStatus === '未发货' && indx === 3"
            class="op-btn"
            @click="prsc(item.oid, index)"
          >
            去发货
          </button>
          <button
            v-if="item.logisticsStatus === '已发货' && item.orderStatus === '已支付' && indx === 2"
            class="op-btn primary"
            @click="comc(item.oid, index)"
          >
            确认收货
          </button>
          <button class="op-btn danger" @click="delc(item.oid, index)">删除</button>
        </div>
      </div>

      <loading v-if="listIsLoading"></loading>
      <p class="empty" v-if="!listIsLoading && !cList.length">{{ emptyText }}</p>
    </div>
  </div>
</template>

<script>
import loading from './loading.vue';

export default {
  name: 'status',
  components: { loading },
  props: {
    personData: { type: Object, default: () => ({}) },
    activeList: { type: Number, default: 1 },
  },
  data() {
    return {
      // 当前列表：1 收藏 / 2 买到 / 3 卖出 / 4 发布
      indx: 1,
      goodsList: [],
      cList: [],
      listIsLoading: false,
      details: { liked: 0, bought: 0, sold: 0, published: 0 },
    };
  },
  computed: {
    tabs() {
      return [
        { id: 1, label: '我收藏的', count: this.details.liked || 0 },
        { id: 2, label: '我买到的', count: this.details.bought || 0 },
        { id: 3, label: '我卖出的', count: this.details.sold || 0 },
        { id: 4, label: '我发布的', count: this.details.published || 0 },
      ];
    },
    emptyText() {
      return {
        1: '还没有收藏的闲置，去市场逛逛吧',
        2: '还没有买到的订单',
        3: '还没有卖出的订单',
        4: '还没有发布过闲置',
      }[this.indx];
    },
  },
  mounted() {
    this.details = (this.personData && this.personData.details) || this.details;
    const start = Number(this.activeList) || 1;
    this.switchList(start >= 1 && start <= 4 ? start : 1);
  },
  watch: {
    personData: {
      handler(v) {
        if (v && v.details) this.details = v.details;
      },
      deep: true,
    },
  },
  methods: {
    // 切换列表并按需拉取数据
    switchList(indx) {
      if (!indx) return;
      this.indx = indx;

      if (indx === 1) {
        this.goodsList = [];
        this.listIsLoading = true;
        this.$http({ url: '/person/liked', method: 'get', params: { userid: this.personData.id } })
          .then(res => {
            this.goodsList = [];
            this.reflashList(res.data, '通过');
            this.listIsLoading = false;
          })
          .catch(() => {
            this.listIsLoading = false;
            this.$emit('showmsg', { info: '收藏加载失败', colorType: 3 });
          });
      } else if (indx === 4) {
        this.goodsList = [];
        this.listIsLoading = true;
        this.$http({ url: '/person/published', method: 'get' })
          .then(res => {
            this.goodsList = [];
            this.reflashList(res.data);
            this.listIsLoading = false;
          })
          .catch(() => {
            this.listIsLoading = false;
            this.$emit('showmsg', { info: '发布记录加载失败', colorType: 3 });
          });
      } else {
        this.cList = [];
        this.listIsLoading = true;
        const url = indx === 2 ? '/person/bought' : '/person/sold';
        this.$http({ url, method: 'get' })
          .then(res => {
            this.cList = [];
            this.loadcList(res.data);
            this.listIsLoading = false;
          })
          .catch(() => {
            this.listIsLoading = false;
            this.$emit('showmsg', { info: '订单加载失败', colorType: 3 });
          });
      }
    },

    // ---------- 商品（收藏 / 发布） ----------
    reflashList(list, fixedStatus) {
      (list || []).forEach(item => {
        const temp = {
          id: item.id,
          name: item.tradeName || item.name,
          price: item.price,
          status: fixedStatus || item.status || '',
          pic: item.firstPhoto || '',
        };
        this.goodsList.push(temp);
        this.request_goodsImg(temp.id, 'like-or-publish');
      });
    },
    loadcList(list) {
      (list || []).forEach(item => {
        const temp = {
          id: item.commodityId,
          name: item.name,
          pic: '',
          seller: item.seller,
          orderStatus: item.orderStatus,
          logisticsStatus: item.logisticsStatus,
          time: this.changeTime(item.submitTime),
          oid: item.id,
        };
        this.cList.push(temp);
        this.request_goodsImg(temp.id, 'buy-or-sell');
      });
    },
    changeTime(t) {
      if (!t) return '';
      const d = new Date(t);
      if (Number.isNaN(d.getTime())) return String(t);
      const pad = n => String(n).padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
    },
    request_goodsImg(id, type) {
      this.$http({ method: 'get', url: 'commodity/getFirstPhoto', params: { commodityId: id } })
        .then(res => {
          const b64 = res.data;
          if (typeof b64 !== 'string' || !b64) return;
          const target = type === 'like-or-publish' ? this.goodsList : this.cList;
          const found = target.find(v => v.id === id);
          if (found) found.pic = b64;
        })
        .catch(() => {});
    },
    openGoods(item) {
      this.$emit('gopene', { id: item.id, name: item.name, price: item.price });
    },
    deleteGoods(item) {
      this.$http({
        method: 'get',
        url: 'commodity/undercarriage',
        params: { commodityId: item.id },
      })
        .then(() => {
          this.goodsList = this.goodsList.filter(v => v.id !== item.id);
          this.$emit('showmsg', { info: '已下架', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.$emit('showmsg', { info: '下架失败', colorType: 3 });
        });
    },
    dislikeGoods(item) {
      this.$http({ method: 'get', url: 'collect/del', params: { commodityId: item.id } })
        .then(() => {
          this.goodsList = this.goodsList.filter(v => v.id !== item.id);
          this.$emit('showmsg', { info: '已取消收藏', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.$emit('showmsg', { info: '操作失败', colorType: 3 });
        });
    },

    // ---------- 订单 ----------
    likc(oid) {
      this.$http({ method: 'get', url: 'collect/add', params: { commodityId: oid } })
        .then(res => {
          if (res.data == true) {
            this.$emit('showmsg', { info: '已收藏', colorType: 1 });
            this.$emit('refresh');
          } else {
            this.$emit('showmsg', { info: '已在收藏列表中', colorType: 3 });
          }
        })
        .catch(() => {
          this.$emit('showmsg', { info: '收藏失败', colorType: 3 });
        });
    },
    comc(oid) {
      this.$http({ method: 'get', url: 'person/receive', params: { orderId: oid } })
        .then(() => {
          const row = this.cList.find(v => v.oid === oid);
          if (row) {
            row.logisticsStatus = '已签收';
            row.orderStatus = '已完成';
          }
          this.$emit('showmsg', { info: '确认收货成功', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.$emit('showmsg', { info: '确认失败', colorType: 3 });
        });
    },
    delc(oid) {
      this.$http({ method: 'post', url: '/person/deleteOrder', params: { orderId: oid } })
        .then(() => {
          this.cList = this.cList.filter(v => v.oid !== oid);
          this.$emit('showmsg', { info: '删除成功', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.$emit('showmsg', { info: '删除失败', colorType: 3 });
        });
    },
    pay(oid) {
      this.$http({ method: 'get', url: 'shop/payment', params: { orderId: oid } })
        .then(() => {
          const row = this.cList.find(v => v.oid === oid);
          if (row) row.orderStatus = '已支付';
          this.$emit('showmsg', { info: '支付成功', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.$emit('showmsg', { info: '支付失败', colorType: 3 });
        });
    },
    prsc(oid) {
      this.$http({ method: 'get', url: 'shop/confirmDeliverGood', params: { orderId: oid } })
        .then(() => {
          const row = this.cList.find(v => v.oid === oid);
          if (row) row.logisticsStatus = '已发货';
          this.$emit('showmsg', { info: '已发货', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.$emit('showmsg', { info: '发货失败', colorType: 3 });
        });
    },
    stateTone(status) {
      const s = String(status || '');
      if (s === '已完成') return 'done';
      if (s === '已支付') return 'paid';
      if (s === '未支付') return 'wait';
      return 'normal';
    },
    // 商品状态文案（把后端状态换成用户更易懂的说法）
    statusText(status) {
      const map = {
        通过: '在售',
        未审核: '审核中',
        驳回: '未通过',
        下架: '已下架',
        已售出: '已售出',
      };
      return map[String(status || '')] || status || '';
    },
  },
};
</script>

<style scoped>
.my-status {
  width: 100%;
}

/* 切换 */
.status-tabs {
  display: flex;
  gap: 22px;
  border-bottom: 1px solid var(--cl-line);
  margin-bottom: 16px;
}

.status-tab {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 0 0 11px;
  font-size: 13px;
  color: var(--cl-muted);
  transition: color 0.18s;
}

.status-tab em {
  font-style: normal;
  font-size: 11px;
  color: var(--cl-muted-2);
}

.status-tab.on {
  color: var(--cl-brand-strong);
  font-weight: 600;
}

.status-tab.on em {
  color: var(--cl-brand);
}

.status-tab.on::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 2px;
  border-radius: 2px;
  background: var(--cl-brand);
}

/* 商品卡片 */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(268px, 1fr));
  gap: 12px;
}

.goods-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--cl-line);
  border-radius: 16px;
  background: #fff;
  transition: border-color 0.18s, box-shadow 0.18s, transform 0.18s;
}

.goods-card:hover {
  border-color: rgba(0, 166, 126, 0.35);
  box-shadow: 0 12px 28px rgba(15, 27, 22, 0.07);
  transform: translateY(-1px);
}

.goods-pic {
  flex-shrink: 0;
  width: 86px;
  height: 86px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.goods-pic img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pic-fallback {
  font-size: 22px;
  font-weight: 600;
  color: var(--cl-muted-2);
}

.goods-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.goods-name {
  font-size: 13.5px;
  line-height: 1.4;
  color: var(--cl-ink);
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.goods-price b {
  font-size: 16px;
  font-weight: 700;
  color: var(--cl-brand-strong);
}

.goods-price span {
  font-size: 11px;
  color: var(--cl-muted);
  padding: 1px 7px;
  border-radius: 999px;
  background: var(--cl-surface);
}

.goods-ops {
  display: flex;
  gap: 8px;
  margin-top: auto;
  padding-top: 4px;
}

/* 订单 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border: 1px solid var(--cl-line);
  border-radius: 16px;
  background: #fff;
  transition: border-color 0.18s, box-shadow 0.18s;
}

.order-row:hover {
  border-color: rgba(0, 166, 126, 0.3);
  box-shadow: 0 10px 24px rgba(15, 27, 22, 0.06);
}

.order-pic {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.order-pic img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.order-main {
  flex: 1;
  min-width: 0;
}

.order-name {
  font-size: 13.5px;
  color: var(--cl-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.order-meta {
  margin-top: 5px;
  font-size: 11.5px;
  color: var(--cl-muted);
}

.order-state {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex-shrink: 0;
}

.state-pill {
  height: 20px;
  padding: 0 9px;
  border-radius: 999px;
  font-size: 11px;
  display: inline-flex;
  align-items: center;
  background: var(--cl-surface);
  color: var(--cl-text);
}

.state-pill.wait {
  background: #fff4e0;
  color: #b4701f;
}

.state-pill.paid {
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
}

.state-pill.done {
  background: #eef2f0;
  color: #6d7c77;
}

.state-sub {
  font-size: 11px;
  color: var(--cl-muted-2);
}

.order-ops {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 按钮 */
.op-btn {
  height: 31px;
  padding: 0 11px;
  border-radius: 8px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12px;
  white-space: nowrap;
  transition: all 0.18s;
}

.op-btn:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
  background: var(--cl-brand-mist);
}

.op-btn.primary {
  background: var(--cl-brand);
  border-color: var(--cl-brand);
  color: #fff;
}

.op-btn.primary:hover {
  background: var(--cl-brand-strong);
  border-color: var(--cl-brand-strong);
  color: #fff;
}

.op-btn.danger {
  color: #a4553a;
}

.op-btn.danger:hover {
  border-color: rgba(164, 85, 58, 0.4);
  background: #fdf3ef;
  color: #8f4526;
}

/* 空状态 */
.empty {
  grid-column: 1 / -1;
  padding: 48px 0;
  text-align: center;
  font-size: 13px;
  color: var(--cl-muted);
}

@media (max-width: 820px) {
  .status-tabs {
    gap: 16px;
  }
  .status-tab {
    font-size: 12px;
  }
  .goods-grid {
    grid-template-columns: 1fr;
  }
  .order-row {
    flex-wrap: wrap;
    gap: 10px;
  }
  .order-main {
    flex-basis: calc(100% - 78px);
  }
  .order-state {
    flex-direction: row;
    align-items: center;
    gap: 8px;
  }
  .order-ops {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
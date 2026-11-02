<template>
  <div class="manage">
    <!-- 概览：分类占比 -->
    <div class="charts">
      <div class="charts-box">
        <sum></sum>
      </div>
      <div class="charts-note">
        <p class="note-title">审核工作台</p>
        <p class="note-desc">
          商品发布后先进入「未审核」，审核通过才在市场展示；
          已上架商品如违规可下架，用户端会立即隐藏。
        </p>
        <div class="note-nums">
          <div class="note-num">
            <b>{{ total }}</b>
            <span>当前筛选结果</span>
          </div>
          <div class="note-num">
            <b>{{ pageSetting.pageL }}</b>
            <span>总页数</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 工具条 -->
    <div class="toolbar">
      <div class="search">
        <input
          v-model="searchData"
          class="search-input"
          placeholder="搜索商品名称 / 卖家"
          @keyup.enter="reload"
        />
        <button class="btn-search" @click="reload">搜索</button>
      </div>

      <div class="filters">
        <label class="select-wrap">
          <select v-model="status" class="select" @change="reload">
            <option value="">全部状态</option>
            <option value="通过">在售（通过）</option>
            <option value="未审核">待审核</option>
            <option value="驳回">已驳回</option>
            <option value="下架">已下架</option>
          </select>
          <span class="select-arrow" aria-hidden="true"></span>
        </label>

        <label class="select-wrap">
          <select v-model="category" class="select" @change="reload">
            <option value="">全部分类</option>
            <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
          </select>
          <span class="select-arrow" aria-hidden="true"></span>
        </label>

        <button class="btn-sort" @click="toggleTime">
          {{ timeOrder === 'DESC' ? '最新发布' : '最早发布' }}
        </button>
      </div>
    </div>

    <!-- 列表 -->
    <div class="table">
      <div class="thead">
        <span>商品</span>
        <span class="col-seller">卖家</span>
        <span class="col-desc">描述</span>
        <span class="col-time">发布时间</span>
        <span class="col-status">状态</span>
        <span class="col-ops">操作</span>
      </div>

      <div class="tbody">
        <div class="tr" v-for="item in cList" :key="item.id">
          <div class="td-goods">
            <span class="thumb">
              <img v-if="item.pic" :src="'data:image/Jpeg;base64,' + item.pic" :alt="item.name" />
              <span v-else class="thumb-ph">{{ String(item.name).slice(0, 1) }}</span>
            </span>
            <span class="goods-name">{{ item.name }}</span>
          </div>

          <div class="td col-seller">{{ item.seller }}</div>
          <div class="td col-desc">{{ item.description }}</div>
          <div class="td col-time">{{ formatTime(item.time) }}</div>
          <div class="td col-status">
            <span class="pill" :class="tone(item.status)">{{ statusText(item.status) }}</span>
          </div>

          <div class="td-ops">
            <button v-if="item.status === '未审核' || item.status === '驳回'" class="op ok" @click="pasc(item.id)">
              通过
            </button>
            <button v-if="item.status === '未审核' || item.status === '驳回'" class="op warn" @click="refc(item.id)">
              驳回
            </button>
            <button v-if="item.status === '通过'" class="op danger" @click="delc(item.id)">下架</button>
          </div>
        </div>

        <loading v-if="listIsloading"></loading>
        <p class="empty" v-if="!listIsloading && !cList.length">没有符合条件的商品</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pager" v-if="pageSetting.pageL > 1">
      <button class="page-btn" :disabled="pageSetting.cNum <= 1" @click="changePage(pageSetting.cNum - 1)">
        上一页
      </button>
      <span class="page-info">第 {{ pageSetting.cNum }} / {{ pageSetting.pageL }} 页</span>
      <button
        class="page-btn"
        :disabled="pageSetting.cNum >= pageSetting.pageL"
        @click="changePage(pageSetting.cNum + 1)"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
import sum from './sum.vue';
import loading from './loading.vue';

export default {
  name: 'manage',
  components: { sum, loading },
  data() {
    return {
      searchData: '',
      status: '',
      category: '',
      timeOrder: 'DESC',
      total: 0,
      categoryOptions: [
        '电子产品', '生活用品', '学习用品', '美食', '图书文具', '服饰鞋包',
        '运动户外', '乐器', '美妆护肤', '母婴', '宠物用品', '家居家装',
        '数码配件', '办公用品', '玩具手办', '其他',
      ],
      pageSetting: { cNum: 1, pSize: 8, tNum: 1, pageL: 1 },
      listIsloading: false,
      cList: [],
    };
  },
  mounted() {
    this.search();
  },
  methods: {
    search() {
      this.cList = [];
      this.listIsloading = true;
      this.$http({
        method: 'get',
        url: 'manage/select',
        params: {
          input: this.searchData,
          orderBuy: this.timeOrder,
          status: this.status,
          category: this.category,
          pageNum: this.pageSetting.cNum,
          pageSize: this.pageSetting.pSize,
        },
      })
        .then(res => {
          const data = res.data || {};
          this.flashCList(data.records || []);
          this.pageSetting.pageL = Number(data.pages) || 1;
          this.total = Number(data.total) || (data.records || []).length;
          this.listIsloading = false;
        })
        .catch(() => {
          this.listIsloading = false;
          this.$emit('showmsg', { info: '列表加载失败', colorType: 3 });
        });
    },
    reload() {
      this.pageSetting.cNum = 1;
      this.search();
    },
    flashCList(list) {
      list.forEach(item => {
        const temp = {
          id: item.id,
          name: item.tradeName || item.name,
          pic: '',
          seller: item.sellerName || item.seller,
          status: item.status,
          time: item.time,
          description: item.description || '—',
        };
        this.cList.push(temp);
        this.request_goodsImg(temp.id);
      });
    },
    request_goodsImg(id) {
      this.$http({ method: 'get', url: 'commodity/getFirstPhoto', params: { commodityId: id } })
        .then(res => {
          const b64 = res.data;
          if (typeof b64 !== 'string' || !b64) return;
          const found = this.cList.find(v => v.id === id);
          if (found) found.pic = b64;
        })
        .catch(() => {});
    },
    changePage(page) {
      if (page < 1 || page > this.pageSetting.pageL) return;
      this.pageSetting.cNum = page;
      this.search();
    },
    toggleTime() {
      this.timeOrder = this.timeOrder === 'DESC' ? 'ASC' : 'DESC';
      this.reload();
    },
    // 通过
    pasc(id) {
      this.$http({ method: 'get', url: 'manage/approved', params: { commodityId: id } })
        .then(() => {
          this.$emit('showmsg', { info: '审核通过，商品已上架', colorType: 1 });
          this.search();
        })
        .catch(() => {
          this.$emit('showmsg', { info: '审核失败', colorType: 3 });
        });
    },
    // 驳回
    refc(id) {
      this.$http({ method: 'get', url: 'manage/refuse', params: { commodityId: id } })
        .then(() => {
          this.$emit('showmsg', { info: '已驳回该商品', colorType: 1 });
          this.search();
        })
        .catch(() => {
          this.$emit('showmsg', { info: '驳回失败', colorType: 3 });
        });
    },
    // 下架
    delc(id) {
      this.$http({ method: 'get', url: 'manage/remove', params: { commodityId: id } })
        .then(() => {
          this.$emit('showmsg', { info: '已下架该商品', colorType: 1 });
          this.search();
        })
        .catch(() => {
          this.$emit('showmsg', { info: '下架失败', colorType: 3 });
        });
    },
    formatTime(t) {
      if (!t) return '—';
      const d = new Date(t);
      if (Number.isNaN(d.getTime())) return String(t).slice(0, 10);
      const pad = n => String(n).padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
    },
    statusText(s) {
      const map = {
        通过: '在售',
        未审核: '待审核',
        驳回: '已驳回',
        下架: '已下架',
        已售出: '已售出',
      };
      return map[String(s || '')] || s || '—';
    },
    tone(s) {
      const v = String(s || '');
      if (v === '通过') return 'ok';
      if (v === '未审核') return 'wait';
      if (v === '驳回' || v === '下架') return 'off';
      return '';
    },
  },
};
</script>

<style scoped>
.manage {
  width: 100%;
}

/* 概览 */
.charts {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 20px;
  margin-bottom: 20px;
}

.charts-box {
  height: 260px;
  border: 1px solid var(--cl-line);
  border-radius: 16px;
  background: var(--cl-surface);
  padding: 6px;
}

.charts-note {
  padding: 18px;
  border: 1px solid var(--cl-line);
  border-radius: 16px;
  background: var(--cl-surface);
}

.note-title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--cl-ink);
}

.note-desc {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.75;
  color: var(--cl-text);
}

.note-nums {
  display: flex;
  gap: 28px;
  margin-top: 18px;
}

.note-num {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.note-num b {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-brand-strong);
}

.note-num span {
  font-size: 11px;
  color: var(--cl-muted);
}

/* 工具条 */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
  padding: 12px 14px;
  border: 1px solid var(--cl-line);
  border-radius: 14px;
  background: var(--cl-surface);
}

.search {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1 1 260px;
  min-width: 0;
}

.search-input {
  flex: 1;
  min-width: 0;
  height: 38px;
  padding: 0 13px;
  border: 1px solid var(--cl-line);
  border-radius: 9px;
  background: #fff;
  font-size: 12.5px;
  color: var(--cl-ink);
}

.search-input::placeholder {
  color: var(--cl-muted);
}

.search-input:focus {
  outline: none;
  border-color: rgba(0, 166, 126, 0.55);
}

.btn-search {
  height: 38px;
  padding: 0 18px;
  border-radius: 9px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 12.5px;
  font-weight: 600;
  transition: background 0.18s;
}

.btn-search:hover {
  background: var(--cl-brand-strong);
}

.filters {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.select-wrap {
  position: relative;
  display: inline-block;
}

.select {
  appearance: none;
  height: 38px;
  padding: 0 30px 0 12px;
  border: 1px solid var(--cl-line);
  border-radius: 9px;
  background: #fff;
  font-size: 12.5px;
  color: var(--cl-ink);
  cursor: pointer;
}

.select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  margin-top: -2px;
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid var(--cl-muted);
  pointer-events: none;
}

.btn-sort {
  height: 38px;
  padding: 0 14px;
  border: 1px solid var(--cl-line);
  border-radius: 9px;
  background: #fff;
  color: var(--cl-text);
  font-size: 12.5px;
  transition: all 0.18s;
}

.btn-sort:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

/* 表格 */
.table {
  margin-top: 14px;
  border: 1px solid var(--cl-line);
  border-radius: 14px;
  overflow: hidden;
}

.thead,
.tr {
  display: grid;
  grid-template-columns: minmax(180px, 1.4fr) 100px minmax(160px, 1.2fr) 108px 92px 170px;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
}

.thead {
  height: 42px;
  background: var(--cl-surface);
  border-bottom: 1px solid var(--cl-line);
  font-size: 11.5px;
  font-weight: 600;
  color: var(--cl-muted);
}

.tr {
  min-height: 68px;
  border-bottom: 1px solid #f1f4f2;
  transition: background 0.18s;
}

.tr:last-child {
  border-bottom: none;
}

.tr:hover {
  background: #fafcfb;
}

.td-goods {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.thumb {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 9px;
  overflow: hidden;
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-ph {
  font-size: 14px;
  color: var(--cl-muted-2);
}

.goods-name {
  font-size: 12.5px;
  color: var(--cl-ink);
  overflow: hidden;
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
}

.td {
  font-size: 12px;
  color: var(--cl-text);
  line-height: 1.5;
  overflow: hidden;
}

.col-desc {
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
}

.pill {
  display: inline-flex;
  align-items: center;
  height: 21px;
  padding: 0 9px;
  border-radius: 999px;
  font-size: 11px;
  background: var(--cl-surface);
  color: var(--cl-text);
}

.pill.ok {
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
}

.pill.wait {
  background: #fff4e0;
  color: #b4701f;
}

.pill.off {
  background: #f1f4f2;
  color: #6d7c77;
}

.td-ops {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
}

.op {
  height: 29px;
  padding: 0 11px;
  border-radius: 8px;
  border: 1px solid var(--cl-line);
  background: #fff;
  font-size: 11.5px;
  color: var(--cl-text);
  white-space: nowrap;
  transition: all 0.18s;
}

.op.ok {
  border-color: rgba(0, 166, 126, 0.45);
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
}

.op.ok:hover {
  background: var(--cl-brand);
  border-color: var(--cl-brand);
  color: #fff;
}

.op.warn:hover {
  border-color: rgba(180, 112, 31, 0.5);
  color: #b4701f;
  background: #fffaf0;
}

.op.danger {
  color: #a4553a;
}

.op.danger:hover {
  border-color: rgba(164, 85, 58, 0.4);
  background: #fdf3ef;
}

.empty {
  padding: 46px 0;
  text-align: center;
  font-size: 12.5px;
  color: var(--cl-muted);
}

/* 分页 */
.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-top: 18px;
}

.page-btn {
  height: 34px;
  padding: 0 16px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12px;
  transition: all 0.18s;
}

.page-btn:hover:not(:disabled) {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.page-info {
  font-size: 12px;
  color: var(--cl-muted);
}

@media (max-width: 1180px) {
  .charts {
    grid-template-columns: 1fr;
  }
  .charts-box {
    height: 230px;
  }
  .thead,
  .tr {
    grid-template-columns: minmax(170px, 1.5fr) 92px 100px 84px 150px;
  }
  .col-desc {
    display: none;
  }
}
</style>
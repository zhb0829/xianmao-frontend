<template>
  <div class="msgs">
    <div class="msgs-tabs">
      <button class="msgs-tab" :class="{ on: tab === 1 }" @click="switchTab(1)">
        我收到的 <b>{{ received.length }}</b>
      </button>
      <button class="msgs-tab" :class="{ on: tab === 2 }" @click="switchTab(2)">
        我发出的 <b>{{ sent.length }}</b>
      </button>
    </div>

    <div v-if="loading" class="msgs-state">正在加载留言…</div>
    <div v-else-if="error" class="msgs-state">{{ error }}</div>
    <div v-else-if="!list.length" class="msgs-state">{{ tab === 1 ? '还没有人给你的商品留言' : '你还没有给别人的商品留言' }}</div>

    <ul v-else class="msgs-list">
      <li v-for="m in list" :key="m.id" class="msgs-item">
        <p class="msgs-head">
          <b>{{ m.authorName }}</b>
          <span class="msgs-good">《{{ m.goodsName }}》</span>
          <time>{{ m.time }}</time>
        </p>
        <p class="msgs-body">{{ m.content }}</p>
        <button class="msgs-link" @click="$emit('open-goods', m.commodityId)">查看商品 →</button>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'messages',
  emits: ['open-goods', 'showmsg'],
  data() {
    return {
      tab: 1,
      loading: true,
      error: '',
      received: [],
      sent: [],
    }
  },
  computed: {
    list() {
      return this.tab === 1 ? this.received : this.sent
    },
  },
  mounted() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      this.error = ''
      this.$http({ method: 'get', url: '/comment/mine' })
        .then(res => {
          const d = res.data || {}
          this.received = Array.isArray(d.received) ? d.received : []
          this.sent = Array.isArray(d.sent) ? d.sent : []
          this.loading = false
        })
        .catch(err => {
          this.error = (err && err.friendly) || '留言加载失败'
          this.loading = false
        })
    },
    switchTab(tab) {
      this.tab = tab
    },
  },
}
</script>

<style scoped>
.msgs {
  display: grid;
  gap: 16px;
}

.msgs-tabs {
  display: flex;
  gap: 8px;
}

.msgs-tab {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text-2);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.16s;
}

.msgs-tab b {
  margin-left: 2px;
  color: inherit;
}

.msgs-tab.on {
  background: var(--cl-brand);
  border-color: var(--cl-brand);
  color: #fff;
}

.msgs-state {
  padding: 40px 0;
  text-align: center;
  font-size: 13.5px;
  color: var(--cl-muted);
}

.msgs-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 12px;
}

.msgs-item {
  padding: 16px 18px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid var(--cl-line);
}

.msgs-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  font-size: 13px;
  color: var(--cl-ink);
}

.msgs-head time {
  margin-left: auto;
  font-size: 11.5px;
  color: var(--cl-muted);
}

.msgs-good {
  font-size: 12px;
  color: var(--cl-text-2);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 46%;
}

.msgs-body {
  margin: 10px 0 10px;
  font-size: 13.5px;
  line-height: 1.75;
  color: var(--cl-text);
}

.msgs-link {
  border: none;
  background: none;
  padding: 0;
  font-size: 12.5px;
  color: var(--cl-brand);
  cursor: pointer;
}

.msgs-link:hover {
  text-decoration: underline;
}
</style>
<template>
  <div class="cl-app want-page">
    <site-header active="want" />

    <main>
      <!-- 页面头 -->
      <section class="want-top">
        <div class="cl-wrap want-top-inner">
          <div class="top-text">
            <p class="eyebrow">WANTED · 求购专区</p>
            <h1 class="want-title">想收什么，说出来</h1>
            <p class="want-sub">
              共 {{ wants.length }} 条求购需求 · 写清楚要什么、预算多少，同校同学看到会主动联系你
            </p>
          </div>

          <div class="top-actions">
            <button class="btn-primary" @click="openComposer">
              <span class="ico" v-html="ICON.plus"></span>
              发布求购
            </button>
          </div>
        </div>
      </section>

      <!-- 筛选与列表 -->
      <section class="cl-wrap want-body">
        <div class="toolbar">
          <div class="chips" role="tablist" aria-label="按分类筛选求购">
            <button
              v-for="c in categoryOptions"
              :key="c.key"
              class="chip"
              :class="{ on: category === c.key }"
              role="tab"
              :aria-selected="category === c.key"
              @click="setCategory(c.key)"
            >
              {{ c.label }}
            </button>
          </div>

          <div class="sorts">
            <button class="sortbtn" :class="{ on: sort === 'latest' }" @click="setSort('latest')">
              最新发布
            </button>
            <button class="sortbtn" :class="{ on: sort === 'budget' }" @click="setSort('budget')">
              预算最高
            </button>
          </div>
        </div>

        <div v-if="shown.length" class="grid">
          <article class="want-card" v-for="w in shown" :key="w.id">
            <header class="wc-top">
              <span class="tag">{{ w.category }}</span>
              <time class="time" :datetime="new Date(w.createdAt).toISOString()">
                {{ timeAgo(w.createdAt) }}
              </time>
            </header>

            <h2 class="wc-title">{{ w.title }}</h2>

            <p class="wc-budget">
              预算 <b>¥{{ w.budget }}</b>
              <span class="muted">可小刀</span>
            </p>

            <p class="wc-desc">{{ w.desc }}</p>

            <footer class="wc-foot">
              <span class="author">
                <span class="avatar" aria-hidden="true">{{ initial(w.author.name) }}</span>
                <span class="author-text">
                  <b>{{ w.author.name }}</b>
                  <i>{{ w.author.credit }}</i>
                </span>
              </span>

              <span class="place">
                <span class="ico" v-html="ICON.pin"></span>{{ w.place }}
              </span>

              <button v-if="w.owner === ownerKey" class="btn-ghost danger" @click="askRemove(w)">
                {{ pendingRemove === w.id ? '确认删除？' : '删除' }}
              </button>
              <button v-else class="btn-ghost" @click="contact(w)">
                <span class="ico" v-html="ICON.chat"></span>联系 TA
              </button>
            </footer>
          </article>
        </div>

        <div v-else class="empty">
          <span class="empty-ico" v-html="ICON.search"></span>
          <p class="empty-title">没有符合条件的求购</p>
          <p class="empty-desc">换个分类看看，或者直接发一条你的需求</p>
          <button class="btn-primary" @click="openComposer">发布求购</button>
        </div>
      </section>
    </main>

    <site-footer />

    <!-- 发布求购 -->
    <Teleport to="body">
      <div
        v-if="composerOpen"
        class="dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="want-dialog-title"
        @keydown.esc="closeComposer"
      >
        <div class="dialog-mask" @click="closeComposer"></div>

        <form class="dialog-card" @submit.prevent="submit">
          <header class="dialog-head">
            <div>
              <h2 id="want-dialog-title">发布求购</h2>
              <p>发布后会显示在求购专区，仅限在校学生查看</p>
            </div>
            <button type="button" class="icon-btn" aria-label="关闭" @click="closeComposer">
              <span class="ico" v-html="ICON.close"></span>
            </button>
          </header>

          <div class="dialog-body">
            <label class="field">
              <span class="label">求购物品 <i aria-hidden="true">*</i></span>
              <input
                ref="firstField"
                v-model="form.title"
                class="input"
                :class="{ err: errors.title }"
                maxlength="40"
                placeholder="例如：求购《数据结构》严蔚敏 C 语言版"
                :aria-invalid="!!errors.title"
                aria-describedby="want-title-err"
                @blur="validateField('title')"
              />
              <em v-if="errors.title" id="want-title-err" class="msg" role="alert">{{ errors.title }}</em>
            </label>

            <div class="field-row">
              <label class="field">
                <span class="label">分类 <i aria-hidden="true">*</i></span>
                <div class="select-wrap">
                  <select
                    v-model="form.category"
                    class="input select"
                    :class="{ err: errors.category }"
                    :aria-invalid="!!errors.category"
                    aria-describedby="want-category-err"
                    @change="validateField('category')"
                  >
                    <option value="" disabled>请选择分类</option>
                    <option v-for="c in WANT_CATEGORIES" :key="c" :value="c">{{ c }}</option>
                  </select>
                  <span class="select-arrow" aria-hidden="true"></span>
                </div>
                <em v-if="errors.category" id="want-category-err" class="msg" role="alert">
                  {{ errors.category }}
                </em>
              </label>

              <label class="field">
                <span class="label">期望价格 <i aria-hidden="true">*</i></span>
                <div class="price-wrap">
                  <span class="price-unit">¥</span>
                  <input
                    v-model="form.budget"
                    class="input price-input"
                    :class="{ err: errors.budget }"
                    inputmode="decimal"
                    placeholder="0"
                    :aria-invalid="!!errors.budget"
                    aria-describedby="want-budget-err"
                    @blur="validateField('budget')"
                  />
                </div>
                <em v-if="errors.budget" id="want-budget-err" class="msg" role="alert">{{ errors.budget }}</em>
              </label>
            </div>

            <label class="field">
              <span class="label">补充说明 <i aria-hidden="true">*</i></span>
              <textarea
                v-model="form.desc"
                class="input textarea"
                :class="{ err: errors.desc }"
                rows="4"
                maxlength="200"
                placeholder="成色要求、用途、什么时候要，写清楚更容易成交"
                :aria-invalid="!!errors.desc"
                aria-describedby="want-desc-err"
                @blur="validateField('desc')"
              ></textarea>
              <em v-if="errors.desc" id="want-desc-err" class="msg" role="alert">{{ errors.desc }}</em>
            </label>

            <div class="field-row">
              <label class="field">
                <span class="label">交易地点 <i aria-hidden="true">*</i></span>
                <input
                  v-model="form.place"
                  class="input"
                  :class="{ err: errors.place }"
                  maxlength="20"
                  placeholder="例如：东校园 3 栋楼下"
                  :aria-invalid="!!errors.place"
                  aria-describedby="want-place-err"
                  @blur="validateField('place')"
                />
                <em v-if="errors.place" id="want-place-err" class="msg" role="alert">{{ errors.place }}</em>
              </label>

              <label class="field">
                <span class="label">联系方式</span>
                <input
                  v-model="form.contact"
                  class="input"
                  maxlength="30"
                  placeholder="微信 / QQ，留空默认站内私信"
                />
              </label>
            </div>
          </div>

          <footer class="dialog-foot">
            <button type="button" class="btn-ghost" @click="closeComposer">取消</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? '发布中…' : '确认发布' }}
            </button>
          </footer>
        </form>
      </div>
    </Teleport>
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import {
  WANT_CATEGORIES,
  addWant,
  currentOwner,
  hasToken,
  isMine,
  loadWants,
  removeWant,
  timeAgo,
} from '../api/localStore'

const svg = d =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`

const ICON = {
  plus: svg('<path d="M12 5.5v13M5.5 12h13"/>'),
  close: svg('<path d="M6 6l12 12M18 6L6 18"/>'),
  pin: svg('<path d="M12 20.6s6.6-5.2 6.6-10.6a6.6 6.6 0 1 0-13.2 0c0 5.4 6.6 10.6 6.6 10.6z"/><circle cx="12" cy="9.8" r="2.4"/>'),
  chat: svg('<path d="M20 14.5a2.5 2.5 0 0 1-2.5 2.5H8l-4 3.5V6.5A2.5 2.5 0 0 1 6.5 4h11A2.5 2.5 0 0 1 20 6.5z"/>'),
  search: svg('<circle cx="11" cy="11" r="6.6"/><path d="m20 20-3.6-3.6"/>'),
}

export default {
  name: 'want',
  components: { siteHeader, siteFooter },
  data() {
    return {
      ICON,
      WANT_CATEGORIES,
      wants: [],
      ownerKey: '',
      category: '',
      sort: 'latest',
      composerOpen: false,
      submitting: false,
      pendingRemove: '',
      myName: '',
      form: { title: '', category: '', budget: '', desc: '', place: '', contact: '' },
      errors: { title: '', category: '', budget: '', desc: '', place: '' },
      removeTimer: null,
    }
  },
  computed: {
    categoryOptions() {
      return [{ key: '', label: '全部' }, ...WANT_CATEGORIES.map(c => ({ key: c, label: c }))]
    },
    shown() {
      const list = this.wants.filter(w => !this.category || w.category === this.category)
      if (this.sort === 'budget') return [...list].sort((a, b) => (b.budget || 0) - (a.budget || 0))
      return list
    },
  },
  mounted() {
    this.readRoute()
    this.loadList()
    this.loadMyName()
  },
  beforeUnmount() {
    if (this.removeTimer) clearTimeout(this.removeTimer)
    document.body.style.overflow = ''
  },
  methods: {
    timeAgo,
    isMine,
    initial(name) {
      const n = String(name || '').trim()
      return n ? n.slice(0, 1).toUpperCase() : '学'
    },
    loadList() {
      this.ownerKey = currentOwner()
      this.wants = loadWants()
    },
    loadMyName() {
      if (!hasToken()) return
      this.$http({ url: '/personal/getUserInfo', method: 'get' })
        .then(res => {
          this.myName = (res.data && res.data.nickname) || ''
        })
        .catch(() => {})
    },
    // 分类 / 排序写进地址栏，便于分享与刷新保持
    readRoute() {
      const q = (this.$route && this.$route.query) || {}
      if (q.category && WANT_CATEGORIES.includes(q.category)) this.category = q.category
      if (q.sort === 'budget') this.sort = 'budget'
    },
    syncRoute() {
      const query = {}
      if (this.category) query.category = this.category
      if (this.sort !== 'latest') query.sort = this.sort
      const current = (this.$route && this.$route.query) || {}
      if ((current.category || '') === (query.category || '') && (current.sort || '') === (query.sort || '')) return
      this.$router.replace({ path: '/want', query }).catch(() => {})
    },
    setCategory(key) {
      this.category = key
      this.syncRoute()
    },
    setSort(key) {
      this.sort = key
      this.syncRoute()
    },

    // ---------- 发布 ----------
    openComposer() {
      if (!hasToken()) {
        this.$emit('showmsg', { info: '登录后才能发布求购', colorType: 3 })
        this.$router.push('/customer')
        return
      }
      this.composerOpen = true
      this.$nextTick(() => {
        const el = this.$refs.firstField
        if (el) el.focus()
        document.body.style.overflow = 'hidden'
      })
    },
    closeComposer() {
      this.composerOpen = false
      document.body.style.overflow = ''
      this.resetForm()
    },
    resetForm() {
      this.form = { title: '', category: '', budget: '', desc: '', place: '', contact: '' }
      this.errors = { title: '', category: '', budget: '', desc: '', place: '' }
    },
    validateField(key) {
      const f = this.form
      const value = String(f[key] || '').trim()

      if (key === 'title') {
        this.errors.title = !value ? '请填写要买的东西' : value.length < 2 ? '至少 2 个字' : ''
      } else if (key === 'category') {
        this.errors.category = value ? '' : '请选择分类'
      } else if (key === 'budget') {
        if (!value) this.errors.budget = '请填写期望价格'
        else if (!/^\d+(\.\d{1,2})?$/.test(value) || Number(value) <= 0) {
          this.errors.budget = '请输入大于 0 的数字'
        } else this.errors.budget = ''
      } else if (key === 'desc') {
        this.errors.desc = !value ? '请补充一下需求' : value.length < 5 ? '至少 5 个字，说清楚要求' : ''
      } else if (key === 'place') {
        this.errors.place = !value ? '请填写交易地点' : value.length < 2 ? '至少 2 个字' : ''
      } else if (key === 'contact') {
        this.errors.contact = ''
      }
      return !this.errors[key]
    },
    validateAll() {
      return ['title', 'category', 'budget', 'desc', 'place'].map(k => this.validateField(k)).every(Boolean)
    },
    submit() {
      if (this.submitting) return
      if (!this.validateAll()) {
        this.$emit('showmsg', { info: '请检查表单里标红的字段', colorType: 3 })
        const firstErr = Object.keys(this.errors).find(k => this.errors[k])
        if (firstErr) {
          const el = document.querySelector(`[aria-describedby="want-${firstErr}-err"]`)
          if (el) el.focus()
        }
        return
      }

      this.submitting = true
      const created = addWant({
        title: this.form.title.trim(),
        category: this.form.category,
        budget: this.form.budget,
        desc: this.form.desc.trim(),
        place: this.form.place.trim(),
        contact: this.form.contact.trim() || '私信',
        authorName: this.myName || '我',
      })
      this.submitting = false

      if (!created) {
        this.$emit('showmsg', { info: '本地存储空间不足，请清理浏览器数据后重试', colorType: 3 })
        return
      }

      this.loadList()
      this.$emit('showmsg', { info: '求购已发布', colorType: 1 })
      this.closeComposer()
    },

    // ---------- 删除（二次确认） ----------
    askRemove(item) {
      if (this.pendingRemove === item.id) {
        this.doRemove(item)
        return
      }
      this.pendingRemove = item.id
      this.$emit('showmsg', { info: '再次点击「确认删除？」即可删除', colorType: 2 })
      if (this.removeTimer) clearTimeout(this.removeTimer)
      this.removeTimer = setTimeout(() => {
        this.pendingRemove = ''
      }, 3200)
    },
    doRemove(item) {
      this.pendingRemove = ''
      const ok = removeWant(item.id)
      if (!ok) {
        this.$emit('showmsg', { info: '删除失败，请稍后重试', colorType: 3 })
        return
      }
      this.loadList()
      this.$emit('showmsg', { info: '已删除该条求购', colorType: 1 })
    },

    // ---------- 联系 ----------
    contact(item) {
      const text = item.contact && item.contact !== '私信' ? item.contact : '私信'
      const done = () => this.$emit('showmsg', { info: `联系方式已复制：${text}`, colorType: 1 })
      if (text === '私信') {
        this.$emit('showmsg', { info: 'TA 选择站内联系，可在「校园圈」发帖互相找到', colorType: 1 })
        return
      }
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(done).catch(() => {
          this.$emit('showmsg', { info: `联系方式：${text}`, colorType: 1 })
        })
      } else {
        this.$emit('showmsg', { info: `联系方式：${text}`, colorType: 1 })
      }
    },
  },
}
</script>

<style scoped>
.want-page {
  background: var(--cl-surface);
}

/* ---------- 页面头 ---------- */
.want-top {
  background: linear-gradient(180deg, #f6faf8 0%, #ffffff 100%);
  border-bottom: 1px solid var(--cl-line);
}

.want-top-inner {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding-top: 42px;
  padding-bottom: 30px;
}

.eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1.2px;
  color: var(--cl-brand);
}

.want-title {
  margin-top: 8px;
  font-size: 34px;
  font-weight: 700;
  letter-spacing: -1px;
  line-height: 1.2;
  color: var(--cl-ink);
}

.want-sub {
  margin-top: 8px;
  max-width: 620px;
  font-size: 13px;
  color: var(--cl-muted);
}

.top-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 4px;
}

/* ---------- 工具条 ---------- */
.want-body {
  padding-top: 22px;
  padding-bottom: 64px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  flex-wrap: wrap;
  margin-bottom: 18px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  height: 34px;
  padding: 0 15px;
  border-radius: 999px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12.5px;
  transition: all 0.18s;
}

.chip:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.chip.on {
  background: var(--cl-ink);
  border-color: var(--cl-ink);
  color: #fff;
  font-weight: 600;
}

.sorts {
  display: flex;
  gap: 6px;
}

.sortbtn {
  height: 34px;
  padding: 0 14px;
  border-radius: 9px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--cl-muted);
  font-size: 12.5px;
  transition: all 0.18s;
}

.sortbtn:hover {
  color: var(--cl-ink);
  background: #fff;
}

.sortbtn.on {
  background: #fff;
  border-color: var(--cl-line);
  color: var(--cl-ink);
  font-weight: 600;
}

/* ---------- 求购卡片 ---------- */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(348px, 1fr));
  gap: 14px;
}

.want-card {
  display: flex;
  flex-direction: column;
  gap: 9px;
  padding: 18px 20px 16px;
  border: 1px solid var(--cl-line);
  border-radius: 18px;
  background: #fff;
  transition: border-color 0.18s, box-shadow 0.18s, transform 0.18s;
}

.want-card:hover {
  border-color: rgba(0, 166, 126, 0.32);
  box-shadow: 0 14px 34px rgba(15, 27, 22, 0.07);
  transform: translateY(-2px);
}

.wc-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.tag {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 9px;
  border-radius: 999px;
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
  font-size: 11px;
  font-weight: 600;
}

.time {
  font-size: 11.5px;
  color: var(--cl-muted);
}

.wc-title {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.45;
  letter-spacing: -0.2px;
  color: var(--cl-ink);
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
  overflow: hidden;
}

.wc-budget {
  display: flex;
  align-items: baseline;
  gap: 6px;
  font-size: 12px;
  color: var(--cl-muted);
}

.wc-budget b {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--cl-brand-strong);
}

.wc-budget .muted {
  font-size: 11px;
  color: var(--cl-muted-2);
}

.wc-desc {
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--cl-text);
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
  overflow: hidden;
}

.wc-foot {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 6px;
  padding-top: 13px;
  border-top: 1px solid #f1f4f2;
}

.author {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 12px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.author-text {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}

.author-text b {
  font-size: 12px;
  font-weight: 600;
  color: var(--cl-ink);
}

.author-text i {
  font-style: normal;
  font-size: 10.5px;
  color: var(--cl-muted);
}

.place {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11.5px;
  color: var(--cl-muted);
  white-space: nowrap;
}

.place .ico :deep(svg) {
  width: 13px;
  height: 13px;
}

.wc-foot .btn-ghost {
  margin-left: auto;
}

/* ---------- 按钮 ---------- */
.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.18s, transform 0.18s;
}

.btn-primary:hover:not(:disabled) {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 32px;
  padding: 0 13px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12px;
  white-space: nowrap;
  transition: all 0.18s;
}

.btn-ghost:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
  background: var(--cl-brand-mist);
}

.btn-ghost.danger {
  color: #a4553a;
}

.btn-ghost.danger:hover {
  border-color: rgba(164, 85, 58, 0.42);
  background: #fdf3ef;
  color: #8f4526;
}

.ico {
  display: inline-flex;
}

.ico :deep(svg) {
  width: 14px;
  height: 14px;
}

/* ---------- 空状态 ---------- */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 72px 20px;
  border: 1px dashed var(--cl-line);
  border-radius: 20px;
  background: #fff;
}

.empty-ico {
  display: inline-flex;
  color: var(--cl-muted-2);
  margin-bottom: 4px;
}

.empty-ico :deep(svg) {
  width: 34px;
  height: 34px;
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--cl-ink);
}

.empty-desc {
  font-size: 12.5px;
  color: var(--cl-muted);
  margin-bottom: 8px;
}

/* ---------- 发布弹窗 ---------- */
.dialog {
  position: fixed;
  inset: 0;
  z-index: 220;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  /* Teleport 到 body 后脱离了 .cl-app，需要自己补回字体与盒模型 */
  font-family: var(--cl-font);
  color: var(--cl-ink);
}

.dialog,
.dialog * {
  box-sizing: border-box;
}

.dialog-mask {
  position: absolute;
  inset: 0;
  background: rgba(10, 20, 16, 0.55);
  backdrop-filter: blur(3px);
}

.dialog-card {
  position: relative;
  width: min(600px, 100%);
  max-height: min(88vh, 760px);
  overflow: auto;
  border-radius: 22px;
  background: #fff;
  box-shadow: 0 34px 90px rgba(8, 20, 14, 0.35);
  animation: dialogIn 0.22s ease-out;
}

@keyframes dialogIn {
  from {
    opacity: 0;
    transform: translateY(12px) scale(0.99);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

.dialog-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 24px 26px 16px;
  border-bottom: 1px solid var(--cl-line);
}

.dialog-head h2 {
  font-size: 19px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: var(--cl-ink);
}

.dialog-head p {
  margin-top: 5px;
  font-size: 12px;
  color: var(--cl-muted);
}

.icon-btn {
  width: 32px;
  height: 32px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--cl-text);
  transition: background 0.18s;
}

.icon-btn:hover {
  background: var(--cl-surface);
}

.dialog-body {
  padding: 20px 26px 4px;
}

.field {
  display: block;
  margin-bottom: 16px;
  min-width: 0;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.label {
  display: block;
  margin-bottom: 7px;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--cl-text);
}

.label i {
  font-style: normal;
  color: #b4503a;
}

.input {
  width: 100%;
  height: 44px;
  padding: 0 13px;
  border: 1px solid var(--cl-line);
  border-radius: 10px;
  background: #fff;
  font-size: 13.5px;
  color: var(--cl-ink);
  transition: border-color 0.18s, box-shadow 0.18s;
}

.input::placeholder {
  color: var(--cl-muted);
}

.input:focus {
  outline: none;
  border-color: rgba(0, 166, 126, 0.55);
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
}

.input.err {
  border-color: rgba(190, 80, 60, 0.6);
  box-shadow: 0 0 0 3px rgba(190, 80, 60, 0.08);
}

.textarea {
  height: auto;
  padding: 11px 13px;
  line-height: 1.7;
  resize: vertical;
  font-family: inherit;
}

.select-wrap {
  position: relative;
}

.select {
  appearance: none;
  padding-right: 32px;
  cursor: pointer;
}

.select-arrow {
  position: absolute;
  right: 14px;
  top: 50%;
  margin-top: -2px;
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid var(--cl-muted);
  pointer-events: none;
}

.price-wrap {
  position: relative;
}

.price-unit {
  position: absolute;
  left: 13px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 13px;
  color: var(--cl-muted);
}

.price-input {
  padding-left: 28px;
}

.msg {
  display: block;
  margin-top: 6px;
  font-size: 11.5px;
  font-style: normal;
  color: #b4503a;
}

.dialog-foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 26px 22px;
  border-top: 1px solid var(--cl-line);
}

.dialog-foot .btn-ghost {
  height: 40px;
  padding: 0 20px;
  font-size: 13px;
}

/* ---------- 响应式 ---------- */
@media (max-width: 1100px) {
  .want-top-inner {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 760px) {
  .want-title {
    font-size: 26px;
  }
  .grid {
    grid-template-columns: 1fr;
  }
  .field-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
  .dialog-head,
  .dialog-body,
  .dialog-foot {
    padding-left: 18px;
    padding-right: 18px;
  }
}

/* 键盘焦点可见 */
.want-page :focus-visible,
.dialog :focus-visible {
  outline: 2px solid var(--cl-brand);
  outline-offset: 2px;
}

@media (prefers-reduced-motion: reduce) {
  .want-card,
  .btn-primary,
  .chip,
  .sortbtn {
    transition: none;
  }
  .want-card:hover {
    transform: none;
  }
}
</style>
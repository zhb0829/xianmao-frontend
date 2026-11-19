<template>
  <div class="cl-app admin-page">
    <site-header active="account" />

    <main class="cl-wrap admin-main">
      <header class="admin-head">
        <p class="eyebrow">ADMIN · 分类管理</p>
        <h1>商品分类管理</h1>
        <p class="sub">分类用于发布页下拉与商品市场筛选；已有商品的分类不能直接删除。</p>
      </header>

      <section v-if="!isAdmin" class="guard">
        <h2>{{ isLogged ? '需要管理员权限' : '请先登录' }}</h2>
        <p>
          {{ isLogged
            ? '当前账号不是管理员。可退出后用 admin / 123456 登录，再进入分类管理。'
            : '登录后可以使用后台管理功能。' }}
        </p>
        <button class="btn-primary" @click="goLogin">去登录</button>
      </section>

      <template v-else>
        <section class="add-card">
          <input
            v-model="newName"
            class="add-input"
            maxlength="12"
            placeholder="输入新分类名称，例如：乐器"
            @keyup.enter="add"
          />
          <button class="btn-primary" :disabled="!newName.trim() || adding" @click="add">
            {{ adding ? '添加中…' : '添加分类' }}
          </button>
        </section>

        <p v-if="error" class="error">{{ error }}</p>

        <section v-if="loading" class="state">正在加载分类…</section>

        <section v-else class="table">
          <div class="thead">
            <span>分类名称</span>
            <span>商品数</span>
            <span>操作</span>
          </div>

          <div v-for="c in list" :key="c.name" class="tr">
            <div class="td name-cell">
              <input
                v-if="editing === c.name"
                v-model="editingName"
                class="edit-input"
                maxlength="12"
                @keyup.enter="saveEdit(c)"
              />
              <span v-else class="cname">{{ c.name }}</span>
            </div>

            <div class="td count-cell">{{ c.count }} 件</div>

            <div class="ops">
              <template v-if="editing === c.name">
                <button class="op ok" :disabled="!editingName.trim() || saving" @click="saveEdit(c)">保存</button>
                <button class="op" @click="cancelEdit">取消</button>
              </template>
              <template v-else>
                <button class="op" @click="startEdit(c)">改名</button>
                <button class="op danger" :disabled="c.count > 0" @click="remove(c)">删除</button>
              </template>
            </div>
          </div>

          <p v-if="!list.length" class="empty">还没有分类，先添加一个吧</p>
        </section>

        <p class="tip">提示：分类下有商品时「删除」不可用，需要先把商品改到其他分类。</p>
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
  name: 'adminCategoryPage',
  components: { siteHeader, siteFooter },
  data() {
    return {
      list: [],
      loading: true,
      error: '',
      newName: '',
      adding: false,
      editing: '',
      editingName: '',
      saving: false,
    }
  },
  computed: {
    isLogged() {
      return useUserStore().isLogged
    },
    isAdmin() {
      return useUserStore().isAdmin
    },
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      this.error = ''
      this.$http({ method: 'get', url: '/category/list' })
        .then(res => {
          this.list = Array.isArray(res.data) ? res.data : []
          this.loading = false
        })
        .catch(err => {
          this.error = (err && err.friendly) || '分类加载失败'
          this.loading = false
        })
    },
    add() {
      const name = this.newName.trim()
      if (!name || this.adding) return
      this.adding = true
      this.$http({ method: 'post', url: '/category/add', params: { name } })
        .then(() => {
          this.adding = false
          this.newName = ''
          this.$emit('showmsg', { info: '分类已添加', colorType: 1 })
          this.load()
        })
        .catch(err => {
          this.adding = false
          this.$emit('showmsg', { info: (err && err.friendly) || '添加失败', colorType: 3 })
        })
    },
    startEdit(c) {
      this.editing = c.name
      this.editingName = c.name
    },
    cancelEdit() {
      this.editing = ''
      this.editingName = ''
    },
    saveEdit(c) {
      const newName = this.editingName.trim()
      if (!newName || this.saving) return
      if (newName === c.name) {
        this.cancelEdit()
        return
      }
      this.saving = true
      this.$http({
        method: 'post',
        url: '/category/update',
        params: { oldName: c.name, newName },
      })
        .then(() => {
          this.saving = false
          this.cancelEdit()
          this.$emit('showmsg', { info: '分类已改名，商品已同步', colorType: 1 })
          this.load()
        })
        .catch(err => {
          this.saving = false
          this.$emit('showmsg', { info: (err && err.friendly) || '改名失败', colorType: 3 })
        })
    },
    remove(c) {
      if (c.count > 0) return
      if (!window.confirm(`确定删除分类「${c.name}」吗？`)) return
      this.$http({ method: 'post', url: '/category/delete', params: { name: c.name } })
        .then(() => {
          this.$emit('showmsg', { info: '分类已删除', colorType: 1 })
          this.load()
        })
        .catch(err => {
          this.$emit('showmsg', { info: (err && err.friendly) || '删除失败', colorType: 3 })
        })
    },
    goLogin() {
      this.$router.push({ path: '/login', query: { redirect: '/admin/category' } })
    },
  },
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.admin-main {
  max-width: 920px;
  margin: 0 auto;
  padding: 36px 20px 72px;
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

.sub {
  margin: 0;
  max-width: 640px;
  font-size: 13.5px;
  line-height: 1.7;
  color: var(--cl-text-2);
}

.guard {
  margin-top: 24px;
  padding: 40px 24px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid var(--cl-line);
  text-align: center;
}

.guard h2 {
  margin: 0 0 10px;
  font-size: 18px;
  color: var(--cl-ink);
}

.guard p {
  margin: 0 0 18px;
  font-size: 13.5px;
  color: var(--cl-muted);
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

.btn-primary:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.add-card {
  display: flex;
  gap: 12px;
  margin-top: 22px;
  padding: 16px 18px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid var(--cl-line);
}

.add-input {
  flex: 1;
  height: 40px;
  padding: 0 14px;
  border-radius: 12px;
  border: 1px solid var(--cl-line);
  font-size: 13.5px;
  outline: none;
}

.add-input:focus {
  border-color: var(--cl-brand);
}

.error {
  margin: 12px 0 0;
  font-size: 12.5px;
  color: #b4453a;
}

.state {
  padding: 40px 0;
  text-align: center;
  font-size: 13.5px;
  color: var(--cl-muted);
}

.table {
  margin-top: 18px;
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--cl-line);
}

.thead,
.tr {
  display: grid;
  grid-template-columns: minmax(0, 2fr) 120px minmax(0, 1.4fr);
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
}

.thead {
  background: var(--cl-surface-alt, #f2f7f4);
  font-size: 12.5px;
  font-weight: 600;
  color: var(--cl-text-2);
}

.tr {
  border-top: 1px solid var(--cl-line);
  font-size: 13.5px;
}

.cname {
  color: var(--cl-ink);
  font-weight: 500;
}

.name-cell,
.count-cell {
  min-width: 0;
}

.count-cell {
  color: var(--cl-muted);
  font-size: 13px;
}

.edit-input {
  width: 100%;
  box-sizing: border-box;
  height: 34px;
  padding: 0 10px;
  border-radius: 10px;
  border: 1px solid var(--cl-brand);
  font-size: 13px;
  outline: none;
}

.ops {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.op {
  padding: 6px 12px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12.5px;
  cursor: pointer;
}

.op.ok {
  border-color: transparent;
  background: var(--cl-brand);
  color: #fff;
}

.op.danger {
  color: #b4453a;
  border-color: #f0d6d2;
}

.op:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.empty {
  padding: 30px;
  text-align: center;
  font-size: 13px;
  color: var(--cl-muted);
}

.tip {
  margin: 14px 0 0;
  font-size: 12.5px;
  color: var(--cl-muted);
}

@media (max-width: 640px) {
  .thead,
  .tr {
    grid-template-columns: minmax(0, 1.6fr) 76px minmax(0, 1.6fr);
    padding: 10px 12px;
  }
}
</style>
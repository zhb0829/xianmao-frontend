<template>
  <div class="cl-app pub-page">
    <site-header active="account" />

    <main class="cl-wrap pub-main">
      <header class="pub-head">
        <p class="eyebrow">SELL · {{ isEdit ? '编辑' : '发布' }}</p>
        <h1 class="pub-title">{{ isEdit ? '编辑闲置' : '发布闲置' }}</h1>
        <p class="pub-sub">
          {{ isEdit
            ? '修改商品信息或图片，保存后重新进入审核队列，审核通过即恢复展示。'
            : '两步填完：商品信息 + 轮播图。提交后进入管理员审核队列，审核通过即出现在商品市场。' }}
        </p>
      </header>

      <section v-if="isLogged" class="pub-card">
        <publish-form :editId="editId" @done="onDone" @refresh="onRefresh" @showmsg="passMsg" />
      </section>

      <section v-else class="pub-need-login">
        <h2>发布闲置需要先登录</h2>
        <p>登录后可以管理自己发布的商品、收藏与订单。</p>
        <button class="btn-primary" @click="toLogin">去登录</button>
      </section>
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import publishForm from '../components/publish.vue'
import { useUserStore } from '../stores/user'

export default {
  name: 'publishPage',
  components: { siteHeader, siteFooter, publishForm },
  data() {
    return {
      isLogged: false,
      editId: 0,
    }
  },
  computed: {
    isEdit() {
      return Number(this.editId) > 0
    },
  },
  created() {
    this.isLogged = useUserStore().isLogged
    this.editId = Number(this.$route.query.edit) || 0
  },
  watch: {
    // 从「我发布的」连续编辑不同商品时，刷新回填
    '$route.query.edit'(value) {
      this.editId = Number(value) || 0
    },
  },
  methods: {
    toLogin() {
      this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
    },
    // 发布/编辑成功：回到「我发布的」，能立刻看到刚提交的商品
    onDone() {
      this.$emit('showmsg', {
        info: this.isEdit ? '修改已保存，等待重新审核' : '发布成功，等待管理员审核',
        colorType: 1,
      })
      this.$router.push({ path: '/customer', query: { tab: 9 } })
    },
    onRefresh() {
      this.$emit('showmsg', {
        info: this.isEdit ? '修改已提交，可在「我发布的」查看状态' : '商品已提交，可在「我发布的」查看审核状态',
        colorType: 1,
      })
    },
    passMsg(msg) {
      this.$emit('showmsg', msg)
    },
  },
}
</script>

<style scoped>
.pub-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.pub-main {
  max-width: 960px;
  margin: 0 auto;
  padding: 48px 24px 88px;
}

.pub-head {
  margin-bottom: 26px;
}

.pub-head .eyebrow {
  font-size: 12px;
  letter-spacing: 0.16em;
  color: var(--cl-muted);
  text-transform: uppercase;
}

.pub-title {
  margin: 10px 0 10px;
  font-size: clamp(26px, 3vw, 36px);
  color: var(--cl-ink);
}

.pub-sub {
  margin: 0;
  max-width: 640px;
  font-size: 14px;
  line-height: 1.8;
  color: var(--cl-text-2);
}

.pub-card {
  padding: 26px;
  border-radius: 22px;
  background: #fff;
  border: 1px solid var(--cl-line);
  box-shadow: 0 20px 50px rgba(9, 24, 17, 0.08);
}

.pub-need-login {
  padding: 56px 24px;
  border-radius: 22px;
  background: #fff;
  border: 1px solid var(--cl-line);
  text-align: center;
}

.pub-need-login h2 {
  margin: 0 0 10px;
  font-size: 20px;
  color: var(--cl-ink);
}

.pub-need-login p {
  margin: 0 0 20px;
  color: var(--cl-text-2);
  font-size: 14px;
}

.btn-primary {
  padding: 11px 26px;
  border-radius: 999px;
  border: none;
  background: var(--cl-brand);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}
</style>
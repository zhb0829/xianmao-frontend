<template>
  <div class="cl-app auth-page">
    <site-header active="account" />

    <main class="cl-wrap auth-main">
      <section class="auth-side">
        <p class="eyebrow">ACCOUNT · 账号</p>
        <h1 class="auth-title">登录后，闲置才有下文</h1>
        <p class="auth-lede">
          收藏商品、发布闲置、查看订单与留言，都从这一个账号开始。真实后端用 JWT，演示模式用 Mock 账号。
        </p>

        <ul class="auth-points">
          <li v-for="p in points" :key="p">
            <span class="dot" aria-hidden="true"></span>{{ p }}
          </li>
        </ul>

        <div class="auth-demo">
          <p class="demo-title">演示账号</p>
          <p><code>demo</code> / <code>123456</code>　普通用户</p>
          <p><code>admin</code> / <code>123456</code>　管理员（多出审核入口）</p>
        </div>
      </section>

      <section class="auth-card">
        <login-box @login="onLogin" @showmsg="passMsg" />
      </section>
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import loginBox from '../components/login.vue'

export default {
  name: 'loginPage',
  components: { siteHeader, siteFooter, loginBox },
  data() {
    return {
      points: [
        '登录态由 Pinia 统一管理，刷新页面不丢失',
        '普通用户与管理员同一入口，按权限展示功能',
        '注册、验证码、登录失败提示都在同一表单内完成',
      ],
    }
  },
  methods: {
    onLogin() {
      this.$emit('showmsg', { info: '登录成功', colorType: 1 })
      const redirect = this.$route.query.redirect
      const target = typeof redirect === 'string' && redirect ? redirect : '/customer'
      this.$router.replace(target)
    },
    passMsg(msg) {
      this.$emit('showmsg', msg)
    },
  },
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: var(--cl-surface);
}

.auth-main {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 0.95fr);
  gap: 48px;
  align-items: center;
  padding: 64px 24px 88px;
  min-height: calc(100vh - 260px);
}

.auth-side {
  max-width: 520px;
}

.auth-side .eyebrow {
  font-size: 12px;
  letter-spacing: 0.16em;
  color: var(--cl-muted);
  text-transform: uppercase;
}

.auth-title {
  margin: 12px 0 14px;
  font-size: clamp(28px, 3.4vw, 42px);
  line-height: 1.2;
  letter-spacing: -0.01em;
  color: var(--cl-ink);
}

.auth-lede {
  margin: 0 0 26px;
  font-size: 15px;
  line-height: 1.8;
  color: var(--cl-text-2);
}

.auth-points {
  list-style: none;
  margin: 0 0 26px;
  padding: 0;
  display: grid;
  gap: 12px;
}

.auth-points li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--cl-text);
}

.auth-points .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--cl-brand);
  flex: none;
}

.auth-demo {
  padding: 16px 18px;
  border-radius: 14px;
  background: var(--cl-surface-alt, #f2f4f3);
  border: 1px solid var(--cl-line);
  font-size: 13px;
  color: var(--cl-text-2);
}

.auth-demo .demo-title {
  margin: 0 0 8px;
  font-weight: 600;
  color: var(--cl-ink);
}

.auth-demo p {
  margin: 4px 0;
}

.auth-demo code {
  padding: 1px 6px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid var(--cl-line);
  font-size: 12px;
}

.auth-card {
  padding: 30px 28px;
  border-radius: 22px;
  background: #fff;
  border: 1px solid var(--cl-line);
  box-shadow: 0 24px 60px rgba(9, 24, 17, 0.1);
}

@media (max-width: 900px) {
  .auth-main {
    grid-template-columns: 1fr;
    gap: 28px;
    padding-top: 36px;
  }
}
</style>
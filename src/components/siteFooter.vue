<template>
  <footer class="cl-footer">
    <div class="cl-wrap footer-main">
      <div class="brand-col">
        <a class="brand" @click="go('/welcome')">
          <span class="brand-mark">
            <img :src="ico.refresh" alt="" />
          </span>
          <b>闲猫二手</b>
        </a>
        <p class="brand-desc">
          面向在校学生的二手闲置交易平台。让每一件物品，在校园里多停留一段时间。
        </p>
        <div class="socials">
          <button class="social" title="校园小程序" @click="go('/shop')">
            <img :src="ico.store" alt="" />
          </button>
          <button class="social" title="拍照识物发布" @click="go('/customer?tab=2')">
            <img :src="ico.camera" alt="" />
          </button>
          <button class="social" title="校内消息与反馈" @click="go('/customer')">
            <img :src="ico.message" alt="" />
          </button>
        </div>
      </div>

      <nav class="link-cols">
        <div class="col" v-for="col in columns" :key="col.title">
          <p class="col-title">{{ col.title }}</p>
          <a
            v-for="link in col.links"
            :key="link.name"
            class="col-link"
            :href="link.href || 'javascript:void(0)'"
            @click="onLink(link)"
          >
            {{ link.name }}
          </a>
        </div>
      </nav>
    </div>

    <div class="cl-wrap footer-bottom">
      <p>© 2026 闲猫二手 集美大小诚毅学院 · 仅限在校学生使用</p>
      <div class="bottom-right">
        <span>简体中文</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const ico = {
  refresh: new URL('../assets/cl/ico/refreshcw0.svg', import.meta.url).href,
  store: new URL('../assets/cl/ico/store.svg', import.meta.url).href,
  camera: new URL('../assets/cl/ico/camera.svg', import.meta.url).href,
  message: new URL('../assets/cl/ico/messagecircle.svg', import.meta.url).href,
}

const columns = [
  {
    title: '逛二手',
    links: [
      { name: '教材书籍', to: { path: '/shop', query: { category: '学习用品' } } },
      { name: '数码电子', to: { path: '/shop', query: { category: '电子产品' } } },
      { name: '代步工具', to: { path: '/shop', query: { category: '运动户外' } } },
      { name: '宿舍电器', to: { path: '/shop', query: { category: '生活用品' } } },
    ],
  },
  {
    title: '交易保障',
    links: [
      { name: '验货宝说明', anchor: 'cl-guarantee' },
      { name: '校内自提规则', anchor: 'cl-guarantee' },
      { name: '退款与售后', anchor: 'cl-guarantee' },
      { name: '举报与申诉', to: { path: '/customer' } },
    ],
  },
  {
    title: '关于我们',
    links: [
      { name: '平台介绍', anchor: 'cl-about' },
      { name: '校园合伙人', anchor: 'cl-about' },
      { name: '隐私政策', anchor: 'cl-about' },
      { name: '服务协议', anchor: 'cl-about' },
    ],
  },
  {
    title: '联系我们',
    links: [
      { name: '客服邮箱 help@campusloop.edu', href: 'mailto:help@campusloop.edu' },
      { name: '工作日 9:00 - 21:00' },
      { name: '合作入驻', to: { path: '/customer' } },
      { name: '意见反馈', to: { path: '/customer' } },
    ],
  },
]

function go(path) {
  router.push(path)
}

function onLink(link) {
  if (link.href) return
  if (link.to) {
    router.push(link.to)
    return
  }
  if (link.anchor) {
    const el = document.getElementById(link.anchor)
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
</script>

<style scoped>
.cl-footer {
  background: var(--cl-ink);
  color: #fff;
}

.footer-main {
  display: flex;
  align-items: flex-start;
  gap: 40px;
  padding-top: 40px;
  padding-bottom: 32px;
}

.brand-col {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-mark {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: var(--cl-brand);
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-mark img {
  width: 16px;
  height: 16px;
}

.brand b {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.brand-desc {
  font-size: 12px;
  line-height: 1.7;
  color: var(--cl-muted-2);
  max-width: 280px;
}

.socials {
  display: flex;
  gap: 10px;
  margin-top: 2px;
}

.social {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: var(--cl-ink-2);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.18s, transform 0.18s;
}

.social img {
  width: 15px;
  height: 15px;
}

.social:hover {
  background: #26362f;
  transform: translateY(-1px);
}

.link-cols {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 24px;
}

.col {
  display: flex;
  flex-direction: column;
  gap: 11px;
}

.col-title {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.col-link {
  font-size: 12px;
  color: var(--cl-muted-2);
  line-height: 1.4;
  transition: color 0.18s;
}

.col-link:hover {
  color: var(--cl-on-brand);
}

.footer-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 28px;
  font-size: 11px;
  color: #6e7c78;
}

.bottom-right {
  display: flex;
  gap: 20px;
}

@media (max-width: 980px) {
  .footer-main {
    flex-direction: column;
    gap: 28px;
  }
  .link-cols {
    width: 100%;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 26px 20px;
  }
}

@media (max-width: 560px) {
  .link-cols {
    grid-template-columns: 1fr;
  }
  .footer-bottom {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
<template>
  <header class="cl-header">
    <!-- 顶部公告条 -->
    <div class="ticker">
      <div class="cl-wrap ticker-inner">
        <p class="ticker-note">校园二手 · 让闲置在同学之间继续流通</p>
        <nav class="ticker-links">
          <a @click="go('/customer?tab=7')">我的订单</a>
          <a @click="go('/shop')">全部商品</a>
          <a @click="scrollToGuarantee">交易保障</a>
        </nav>
      </div>
    </div>

    <!-- 主导航 -->
    <div class="navbar">
      <div class="cl-wrap nav-inner">
        <a class="brand" @click="go('/welcome')" aria-label="闲猫二手首页">
          <span class="brand-mark">
            <img :src="ico.refresh" alt="" />
          </span>
          <span class="brand-words">
            <b>闲猫二手</b>
            <i>CAMPUS LOOP</i>
          </span>
        </a>

        <nav class="links" :class="{ open: menuOpen }">
          <a
            v-for="item in links"
            :key="item.key"
            class="link"
            :class="{ active: item.key === active }"
            :title="item.soon ? `${item.name}即将上线` : ''"
            @click="navigate(item)"
          >
            {{ item.name }}
            <i v-if="item.soon" class="soon-tag">即将上线</i>
          </a>
        </nav>

        <form class="search" @submit.prevent="submitSearch">
          <img class="search-ico" :src="ico.search" alt="" />
          <input
            v-model="keyword"
            class="search-input"
            type="text"
            placeholder="搜索教材、数码、单车… 试试「高数 教材」"
            aria-label="搜索商品"
          />
          <button class="search-btn" type="submit">搜索</button>
        </form>

        <button class="publish" @click="go('/customer?tab=2')">
          <img :src="ico.plus" alt="" />
          <span>发布闲置</span>
        </button>

        <button class="avatar" @click="go('/customer')" aria-label="个人中心">
          <img :src="ico.user" alt="" />
        </button>

        <button class="burger" :class="{ open: menuOpen }" @click="menuOpen = !menuOpen" aria-label="菜单">
          <span></span><span></span><span></span>
        </button>
      </div>
    </div>

    <!-- 轻量提示：未上线入口点击后的反馈 -->
    <Transition name="cl-toast">
      <div v-if="notice" class="cl-toast" role="status">{{ notice }}</div>
    </Transition>
  </header>
</template>

<script setup>
import { onBeforeUnmount, ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  active: { type: String, default: 'home' },
})

const router = useRouter()
const keyword = ref('')
const menuOpen = ref(false)
// 未上线入口的点击反馈
const notice = ref('')
let noticeTimer = null

const ico = {
  refresh: new URL('../assets/cl/ico/refreshcw0.svg', import.meta.url).href,
  search: new URL('../assets/cl/ico/search.svg', import.meta.url).href,
  plus: new URL('../assets/cl/ico/plus.svg', import.meta.url).href,
  user: new URL('../assets/cl/ico/userround.svg', import.meta.url).href,
}

// 导航项：to 为对应路由；soon 用于暂无独立页面的入口（点击给出提示而不是空跳转）
const links = [
  { key: 'home', name: '首页', to: '/welcome' },
  { key: 'shop', name: '全部商品', to: '/shop' },
  { key: 'want', name: '求购专区', to: '/want' },
  { key: 'like', name: '我的收藏', to: '/customer?tab=6' },
  { key: 'circle', name: '校园圈', to: '/circle' },
]

function showNotice(text) {
  notice.value = text
  if (noticeTimer) clearTimeout(noticeTimer)
  noticeTimer = setTimeout(() => {
    notice.value = ''
  }, 2600)
}

function go(path) {
  menuOpen.value = false
  router.push(path)
}

function navigate(item) {
  menuOpen.value = false
  if (item.soon) {
    showNotice(item.soon)
    return
  }
  if (item.key === props.active) return
  router.push(item.to)
}

function submitSearch() {
  const q = keyword.value.trim()
  menuOpen.value = false
  router.push({ path: '/shop', query: q ? { keyword: q } : {} })
}

function scrollToGuarantee() {
  menuOpen.value = false
  const el = document.getElementById('cl-guarantee')
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onBeforeUnmount(() => {
  if (noticeTimer) clearTimeout(noticeTimer)
})
</script>

<style scoped>
.cl-header {
  position: sticky;
  top: 0;
  z-index: 60;
  background: var(--cl-white);
}

/* ---------- 公告条 ---------- */
.ticker {
  background: var(--cl-ink);
}

.ticker-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  height: 33px;
}

.ticker-note {
  font-size: 12px;
  letter-spacing: 0.2px;
  color: var(--cl-on-dark-soft);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ticker-links {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}

.ticker-links a {
  font-size: 12px;
  color: var(--cl-on-dark-soft);
  transition: color 0.18s;
}

.ticker-links a:hover {
  color: var(--cl-on-brand);
}

/* ---------- 主导航 ---------- */
.navbar {
  border-bottom: 1px solid var(--cl-line);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: saturate(180%) blur(12px);
}

.nav-inner {
  display: flex;
  align-items: center;
  gap: 24px;
  height: 76px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  background: var(--cl-brand);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.4s ease;
}

.brand:hover .brand-mark {
  transform: rotate(90deg);
}

.brand-mark img {
  width: 20px;
  height: 20px;
}

.brand-words {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.brand-words b {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.3px;
  line-height: 1.1;
  color: var(--cl-ink);
}

.brand-words i {
  font-style: normal;
  font-size: 10px;
  letter-spacing: 0.6px;
  color: var(--cl-muted);
  line-height: 1.1;
}

.links {
  display: flex;
  align-items: center;
  gap: 28px;
  flex-shrink: 0;
}

.link {
  position: relative;
  font-size: 14px;
  color: var(--cl-text);
  padding: 4px 0;
  transition: color 0.18s;
  white-space: nowrap;
}

.link::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--cl-brand);
  transform: translate(-50%, 4px) scale(0);
  transition: transform 0.22s ease;
}

.link:hover {
  color: var(--cl-ink);
}

.link.active {
  color: var(--cl-brand);
  font-weight: 600;
}

.link.active::after {
  transform: translate(-50%, -2px) scale(1);
}

/* 未上线入口标记 */
.soon-tag {
  margin-left: 4px;
  padding: 1px 4px;
  border-radius: 4px;
  background: var(--cl-surface);
  color: var(--cl-muted);
  font-size: 9.5px;
  font-style: normal;
  font-weight: 500;
  letter-spacing: 0.2px;
  vertical-align: 1px;
  white-space: nowrap;
}

.link:hover .soon-tag {
  background: #eef2f0;
  color: var(--cl-text);
}

/* 未上线入口的点击反馈 */
.cl-toast {
  position: fixed;
  left: 50%;
  bottom: 34px;
  z-index: 200;
  transform: translateX(-50%);
  max-width: min(88vw, 460px);
  padding: 11px 20px;
  border-radius: 999px;
  background: rgba(15, 27, 22, 0.94);
  color: #e8fff7;
  font-size: 13px;
  letter-spacing: 0.2px;
  text-align: center;
  box-shadow: 0 14px 36px rgba(6, 20, 13, 0.3);
  pointer-events: none;
}

.cl-toast-enter-active,
.cl-toast-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.cl-toast-enter-from,
.cl-toast-leave-to {
  opacity: 0;
  transform: translate(-50%, 10px);
}

/* 搜索 */
.search {
  flex: 1 1 220px;
  min-width: 160px;
  height: 42px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 5px 0 14px;
  border: 1px solid var(--cl-line);
  border-radius: var(--cl-radius-m);
  background: var(--cl-surface);
  transition: border-color 0.18s, background 0.18s, box-shadow 0.18s;
}

.search:focus-within {
  border-color: rgba(0, 166, 126, 0.55);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
}

.search-ico {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  opacity: 0.75;
}

.search-input {
  flex: 1 1 auto;
  min-width: 0;
  font-size: 13px;
}

.search-input::placeholder {
  color: var(--cl-muted);
}

.search-btn {
  flex-shrink: 0;
  height: 30px;
  padding: 0 16px;
  border-radius: var(--cl-radius-s);
  background: var(--cl-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.18s;
}

.search-btn:hover {
  background: var(--cl-brand-strong);
}

/* 发布 */
.publish {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 38px;
  padding: 0 18px;
  border-radius: 9px;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
  transition: background 0.18s, transform 0.18s;
}

.publish img {
  width: 15px;
  height: 15px;
}

.publish:hover {
  background: #d8f3e8;
  transform: translateY(-1px);
}

.avatar {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  border-radius: 50%;
  border: 1px solid var(--cl-line);
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.18s;
}

.avatar:hover {
  border-color: rgba(0, 166, 126, 0.5);
}

.avatar img {
  width: 18px;
  height: 18px;
}

.burger {
  display: none;
  width: 38px;
  height: 38px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.burger span {
  width: 16px;
  height: 1.6px;
  border-radius: 2px;
  background: var(--cl-ink);
  transition: transform 0.2s, opacity 0.2s;
}

.burger.open span:nth-child(1) {
  transform: translateY(5.6px) rotate(45deg);
}

.burger.open span:nth-child(2) {
  opacity: 0;
}

.burger.open span:nth-child(3) {
  transform: translateY(-5.6px) rotate(-45deg);
}

/* ---------- 响应式 ---------- */
@media (max-width: 1180px) {
  .links {
    gap: 20px;
  }
  .search {
    flex-basis: 160px;
  }
}

@media (max-width: 1024px) {
  .links {
    display: none;
  }
  .links.open {
    display: flex;
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
    padding: 10px 20px 18px;
    background: #fff;
    border-bottom: 1px solid var(--cl-line);
    box-shadow: 0 18px 40px rgba(15, 27, 22, 0.08);
  }
  .links.open .link {
    padding: 10px 0;
    font-size: 15px;
  }
  .burger {
    display: flex;
  }
  .nav-inner {
    gap: 14px;
    position: relative;
  }
}

@media (max-width: 820px) {
  .ticker-links a:nth-child(2) {
    display: none;
  }
  .search {
    display: none;
  }
  .publish span {
    display: none;
  }
  .publish {
    padding: 0 11px;
  }
}

@media (max-width: 620px) {
  .ticker-links {
    display: none;
  }
  .ticker-inner {
    justify-content: flex-start;
  }
  .nav-inner {
    gap: 10px;
  }
}

@media (max-width: 560px) {
  .ticker-note {
    font-size: 11px;
  }
  .brand-words i {
    display: none;
  }
  .nav-inner {
    height: 66px;
  }
}
</style>
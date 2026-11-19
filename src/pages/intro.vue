<template>
  <div class="cl-app splash" :class="{ 'is-leaving': leaving }">
    <span class="glow glow-a" aria-hidden="true"></span>
    <span class="glow glow-b" aria-hidden="true"></span>
    <span class="glow glow-c" aria-hidden="true"></span>

    <!-- 右侧主视觉：同一张插画，按容器裁切右半部分，左侧柔和淡出与背景融合 -->
    <div class="splash-art" aria-hidden="true">
      <div class="art-float">
        <img :src="art" alt="" draggable="false" decoding="async" fetchpriority="high" />
      </div>
    </div>

    <main class="splash-main">
      <div class="brand rise" style="--d: 60ms">
        <span class="brand-mark">
          <svg class="brand-orbit" viewBox="0 0 120 120" aria-hidden="true">
            <defs>
              <linearGradient id="orbitGrad" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0%" stop-color="#9becd0" stop-opacity="0.95" />
                <stop offset="55%" stop-color="#4dd6a8" stop-opacity="0.7" />
                <stop offset="100%" stop-color="#127f5f" stop-opacity="0.35" />
              </linearGradient>
              <linearGradient id="catGrad" x1="0.15" y1="0" x2="0.85" y2="1">
                <stop offset="0%" stop-color="#4bdbaa" />
                <stop offset="58%" stop-color="#18b183" />
                <stop offset="100%" stop-color="#0d8b66" />
              </linearGradient>
            </defs>
            <ellipse
              cx="60"
              cy="60"
              rx="55"
              ry="17"
              transform="rotate(-13 60 60)"
              fill="none"
              stroke="url(#orbitGrad)"
              stroke-width="4"
              stroke-linecap="round"
              stroke-dasharray="118 62"
              class="orbit-line"
            />
          </svg>
          <svg class="brand-cat" viewBox="0 0 96 96" aria-hidden="true">
            <path d="M27 36 21.5 14 44 26.5Z" fill="url(#catGrad)" />
            <path d="M69 36 74.5 14 52 26.5Z" fill="url(#catGrad)" />
            <path d="M29.5 33 26 20.2 39.4 27.6Z" fill="#e9fff6" opacity="0.9" />
            <path d="M66.5 33 70 20.2 56.6 27.6Z" fill="#e9fff6" opacity="0.9" />
            <path
              d="M48 24c16 0 27.5 11.3 27.5 24.6C75.5 61 64 70 48 70S20.5 61 20.5 48.6C20.5 35.3 32 24 48 24Z"
              fill="url(#catGrad)"
            />
            <path
              d="M34.5 47.4c2.9-4.6 7.5-4.6 10.4 0"
              fill="none"
              stroke="#f2fffb"
              stroke-width="3.8"
              stroke-linecap="round"
            />
            <path
              d="M51.6 47.4c2.9-4.6 7.5-4.6 10.4 0"
              fill="none"
              stroke="#f2fffb"
              stroke-width="3.8"
              stroke-linecap="round"
            />
            <path
              d="M44 57c2.4 2.6 5.6 2.6 8 0"
              fill="none"
              stroke="#f2fffb"
              stroke-width="2.8"
              stroke-linecap="round"
              opacity="0.92"
            />
          </svg>
        </span>
        <span class="brand-words">
          <b>闲猫</b>
          <i>XIANMAO · CAMPUS LOOP</i>
        </span>
      </div>

      <h1 class="title rise" style="--d: 170ms">闲猫二手交易平台</h1>

      <p class="tagline rise" style="--d: 300ms">
        <span class="rule rule-l" aria-hidden="true"></span>
        <span class="tagline-text">校园闲置 · 轻松交易 · 绿色生活</span>
        <span class="rule rule-r" aria-hidden="true"></span>
      </p>

      <div class="actions rise" style="--d: 430ms">
        <span class="enter-wrap">
          <span class="enter-glow" aria-hidden="true"></span>
          <button class="enter" type="button" :disabled="leaving" @click="enter">
            <span class="enter-label">进入详情</span>
            <svg class="enter-arrow" viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M9.5 5.5 16 12l-6.5 6.5"
                fill="none"
                stroke="currentColor"
                stroke-width="2.2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </button>
        </span>
      </div>

      <ul class="trust rise" style="--d: 560ms">
        <li>校内实名认证</li>
        <li>当面验货交易</li>
        <li>发布零佣金</li>
      </ul>
    </main>

    <footer class="splash-foot rise" style="--d: 700ms">
      <span>© 2026 闲猫 · 校园二手交易平台</span>
      <span class="foot-dot" aria-hidden="true"></span>
      <span>Web课设</span>
    </footer>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const art = new URL('../assets/splash/splash-cat-side.jpg', import.meta.url).href

const leaving = ref(false)

const reduceMotion = () =>
  typeof window !== 'undefined' &&
  window.matchMedia('(prefers-reduced-motion: reduce)').matches

/** 进入网站首页：先播放淡出过渡，再跳转（减少动效偏好下直接跳转） */
function enter() {
  if (leaving.value) return
  leaving.value = true

  if (reduceMotion()) {
    router.push('/welcome')
    return
  }

  window.setTimeout(() => {
    router.push('/welcome')
  }, 420)
}

/* 鼠标视差：主视觉与光斑做轻微反向位移，营造纵深 */
let rafId = 0
let pointerEnabled = false

function onPointerMove(event) {
  if (rafId) return
  rafId = window.requestAnimationFrame(() => {
    rafId = 0
    const root = document.documentElement
    root.style.setProperty('--splash-mx', (event.clientX / window.innerWidth - 0.5).toFixed(3))
    root.style.setProperty('--splash-my', (event.clientY / window.innerHeight - 0.5).toFixed(3))
  })
}

function clearPointerVars() {
  const root = document.documentElement
  root.style.removeProperty('--splash-mx')
  root.style.removeProperty('--splash-my')
}

onMounted(() => {
  pointerEnabled = !reduceMotion() && window.matchMedia('(pointer: fine)').matches
  if (pointerEnabled) {
    window.addEventListener('pointermove', onPointerMove, { passive: true })
  }
})

onBeforeUnmount(() => {
  if (pointerEnabled) {
    window.removeEventListener('pointermove', onPointerMove)
  }
  if (rafId) {
    window.cancelAnimationFrame(rafId)
    rafId = 0
  }
  clearPointerVars()
})
</script>

<style scoped>
/* ==========================================================================
   项目欢迎页：由主视觉插画还原的沉浸式入场页
   左：品牌文案 + 进入详情；右：插画右半部分（猫 + 校园）
   ========================================================================== */

.splash {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 100vh;
  min-height: 100dvh;
  padding: clamp(26px, 4.6vw, 54px) 0 clamp(20px, 3.4vw, 38px);
  overflow: hidden;
  isolation: isolate;
  background:
    radial-gradient(120% 88% at 6% -6%, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0) 44%),
    radial-gradient(72% 62% at 88% 4%, rgba(133, 231, 186, 0.5) 0%, rgba(133, 231, 186, 0) 62%),
    radial-gradient(90% 70% at 4% 104%, rgba(255, 255, 255, 0.55) 0%, rgba(255, 255, 255, 0) 58%),
    linear-gradient(146deg, #f4fef9 0%, #e3f9ee 28%, #d2f2e3 60%, #c4ebd8 100%);
}

/* 背景光斑：极慢呼吸，作为“绿色生活”的空气感 */
.glow {
  position: absolute;
  z-index: 0;
  display: block;
  border-radius: 50%;
  pointer-events: none;
  filter: blur(70px);
  transform: translate3d(calc(var(--splash-mx, 0) * 26px), calc(var(--splash-my, 0) * 18px), 0);
  transition: transform 0.5s cubic-bezier(0.2, 0.7, 0.2, 1);
}

.glow-a {
  top: -8%;
  left: 18%;
  width: 38vw;
  height: 38vw;
  min-width: 260px;
  min-height: 260px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.85) 0%, rgba(255, 255, 255, 0) 68%);
  animation: breathe 11s ease-in-out infinite;
}

.glow-b {
  right: 12%;
  bottom: -12%;
  width: 44vw;
  height: 44vw;
  min-width: 320px;
  min-height: 320px;
  background: radial-gradient(circle, rgba(120, 226, 178, 0.55) 0%, rgba(120, 226, 178, 0) 68%);
  animation: breathe 13s ease-in-out infinite 1.4s;
}

.glow-c {
  left: -6%;
  bottom: 4%;
  width: 26vw;
  height: 26vw;
  min-width: 200px;
  min-height: 200px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.6) 0%, rgba(255, 255, 255, 0) 70%);
  animation: breathe 9s ease-in-out infinite 0.8s;
}

/* ============ 右侧主视觉 ============ */
.splash-art {
  position: absolute;
  z-index: 1;
  top: 0;
  right: 0;
  width: 58%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
  -Webkit-mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 1) 9%);
  mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 1) 9%);
  transform: translate3d(calc(var(--splash-mx, 0) * -16px), calc(var(--splash-my, 0) * -10px), 0);
  animation: artIn 1.6s cubic-bezier(0.22, 0.72, 0.2, 1) both;
}

/* 左缘再压一层同色雾化，让插画与页面底色自然接续 */
.splash-art::after {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 10%;
  background: linear-gradient(90deg, rgba(232, 248, 240, 0.78) 0%, rgba(232, 248, 240, 0) 100%);
  pointer-events: none;
}

.art-float {
  position: absolute;
  inset: -16px 0;
  animation: floatSoft 9s ease-in-out infinite;
}

/* 铺满右侧容器：右对齐裁切，插画里的猫头始终完整落在可视区 */
.art-float img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  max-width: none;
  object-fit: cover;
  object-position: 100% 50%;
  user-select: none;
  -Webkit-user-drag: none;
}

/* ============ 左侧文案 ============ */
.splash-main {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  gap: clamp(14px, 1.7vw, 24px);
  width: 100%;
  max-width: min(56%, 860px);
  padding-left: clamp(24px, 6.4vw, 150px);
  padding-right: clamp(20px, 3vw, 40px);
}

/* 品牌行 */
.brand {
  display: inline-flex;
  align-items: center;
  gap: clamp(10px, 1vw, 16px);
}

.brand-mark {
  position: relative;
  display: grid;
  place-items: center;
  width: clamp(54px, 5vw, 74px);
  height: clamp(54px, 5vw, 74px);
  flex-shrink: 0;
}

.brand-orbit,
.brand-cat {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.brand-cat {
  inset: 6% 8% 4%;
  width: 84%;
  height: 90%;
}

.orbit-line {
  animation: orbitFlow 7s linear infinite;
}

.brand-words {
  display: flex;
  flex-direction: column;
  gap: 4px;
  line-height: 1;
}

.brand-words b {
  font-size: clamp(17px, 1.5vw, 22px);
  font-weight: 800;
  letter-spacing: 0.06em;
  color: #10382a;
}

.brand-words i {
  font-size: clamp(9px, 0.72vw, 11px);
  font-style: normal;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: #6d9486;
}

/* 主标题 */
.title {
  margin: 0;
  font-size: clamp(30px, 4.3vw, 78px);
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.014em;
  white-space: nowrap;
  color: #0f3d2c;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.55);
}

/* 副标题：两侧渐变细线，还原设计稿的长破折装饰 */
.tagline {
  display: inline-flex;
  align-items: center;
  gap: clamp(10px, 1.2vw, 20px);
  margin: 0;
  font-size: clamp(13px, 1.28vw, 21px);
  font-weight: 500;
  letter-spacing: 0.16em;
  color: #4e7a68;
  white-space: nowrap;
}

.rule {
  display: block;
  width: clamp(26px, 5vw, 82px);
  height: 1px;
  flex-shrink: 0;
}

.rule-l {
  background: linear-gradient(90deg, rgba(15, 61, 44, 0) 0%, rgba(15, 61, 44, 0.42) 100%);
}

.rule-r {
  background: linear-gradient(90deg, rgba(15, 61, 44, 0.42) 0%, rgba(15, 61, 44, 0) 100%);
}

/* 行动区 */
.actions {
  display: flex;
  align-items: center;
  gap: clamp(14px, 1.6vw, 24px);
  margin-top: clamp(6px, 1vw, 16px);
  flex-wrap: wrap;
}

.enter-wrap {
  position: relative;
  display: inline-flex;
  flex-shrink: 0;
}

/* 呼吸光晕：放在按钮外侧，避免被按钮的 overflow 裁切 */
.enter-glow {
  position: absolute;
  inset: -14px;
  z-index: 0;
  display: block;
  border-radius: 999px;
  background: radial-gradient(58% 100% at 50% 50%, rgba(72, 232, 175, 0.6) 0%, rgba(72, 232, 175, 0) 74%);
  filter: blur(12px);
  pointer-events: none;
  animation: haloPulse 3.8s ease-in-out infinite;
}

.enter {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: clamp(8px, 0.8vw, 13px);
  height: clamp(52px, 4.5vw, 66px);
  padding: 0 clamp(26px, 2.4vw, 38px);
  border: none;
  border-radius: 999px;
  overflow: hidden;
  color: #fff;
  font-family: inherit;
  font-size: clamp(15px, 1.12vw, 18px);
  font-weight: 700;
  letter-spacing: 0.05em;
  cursor: pointer;
  background: linear-gradient(104deg, #8aeac1 0%, #45d9a4 28%, #16b184 68%, #0b936a 100%);
  box-shadow:
    0 20px 42px -16px rgba(9, 139, 101, 0.75),
    0 6px 16px -8px rgba(9, 139, 101, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.52),
    inset 0 -2px 6px rgba(4, 92, 66, 0.22);
  transition:
    transform 0.34s cubic-bezier(0.2, 0.7, 0.2, 1),
    box-shadow 0.34s cubic-bezier(0.2, 0.7, 0.2, 1),
    filter 0.34s ease;
}

/* 悬停扫光 */
.enter::after {
  content: '';
  position: absolute;
  top: -20%;
  bottom: -20%;
  left: -70%;
  width: 46%;
  background: linear-gradient(100deg, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 0.55) 50%, rgba(255, 255, 255, 0) 100%);
  transform: skewX(-18deg);
  pointer-events: none;
}

.enter:hover {
  transform: translateY(-3px) scale(1.012);
  filter: saturate(1.08);
  box-shadow:
    0 28px 56px -16px rgba(9, 139, 101, 0.82),
    0 10px 22px -10px rgba(9, 139, 101, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.6),
    inset 0 -2px 6px rgba(4, 92, 66, 0.22);
}

.enter:hover::after {
  animation: sheen 0.95s cubic-bezier(0.3, 0.7, 0.3, 1);
}

.enter:focus-visible {
  outline: 3px solid rgba(14, 110, 82, 0.55);
  outline-offset: 4px;
}

.enter:active {
  transform: translateY(-1px) scale(0.994);
}

.enter:disabled {
  cursor: default;
  filter: saturate(0.9);
}

.enter-label {
  position: relative;
  z-index: 1;
}

.enter-arrow {
  position: relative;
  z-index: 1;
  width: clamp(17px, 1.3vw, 21px);
  height: clamp(17px, 1.3vw, 21px);
  transition: transform 0.32s cubic-bezier(0.2, 0.7, 0.2, 1);
}

.enter:hover:not(:disabled) .enter-arrow {
  transform: translateX(5px);
}

/* 信任标签 */
.trust {
  display: flex;
  align-items: center;
  gap: clamp(10px, 1.5vw, 22px);
  margin: clamp(2px, 0.6vw, 8px) 0 0;
  padding: 0;
  list-style: none;
  font-size: clamp(11px, 0.92vw, 13px);
  letter-spacing: 0.05em;
  color: #5f8878;
}

.trust li {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  white-space: nowrap;
}

.trust li::before {
  content: '';
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5ce0ab, #13a97c);
  box-shadow: 0 0 0 3px rgba(80, 224, 170, 0.16);
}

/* ============ 页脚 ============ */
.splash-foot {
  position: absolute;
  left: 0;
  bottom: clamp(14px, 2vw, 26px);
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: clamp(24px, 6.4vw, 150px);
  font-size: clamp(10px, 0.8vw, 12px);
  letter-spacing: 0.05em;
  color: #6f9486;
}

.foot-dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.55;
}

/* ============ 动效 ============ */
.rise {
  animation: rise 0.92s cubic-bezier(0.22, 0.72, 0.2, 1) both;
  animation-delay: var(--d, 0ms);
}

@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes artIn {
  from {
    opacity: 0;
    filter: saturate(0.85) blur(6px);
  }
  to {
    opacity: 1;
    filter: saturate(1) blur(0);
  }
}

@keyframes floatSoft {
  0%,
  100% {
    transform: translateY(-6px);
  }
  50% {
    transform: translateY(8px);
  }
}

@keyframes orbitFlow {
  to {
    stroke-dashoffset: -360;
  }
}

@keyframes haloPulse {
  0%,
  100% {
    opacity: 0.35;
    transform: scale(0.96);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.04);
  }
}

@keyframes sheen {
  from {
    left: -70%;
  }
  to {
    left: 130%;
  }
}

@keyframes breathe {
  0%,
  100% {
    opacity: 0.68;
  }
  50% {
    opacity: 1;
  }
}

.is-leaving {
  animation: leave 0.42s cubic-bezier(0.4, 0, 0.6, 1) forwards;
  pointer-events: none;
}

@keyframes leave {
  to {
    opacity: 0;
    transform: scale(1.025);
  }
}

/* ============ 响应式 ============ */
@media (max-width: 1024px) {
  .splash-main {
    max-width: 62%;
    padding-left: clamp(22px, 5vw, 60px);
  }
  .splash-art {
    width: 54%;
  }
  .title {
    font-size: clamp(28px, 4.6vw, 52px);
  }
  .splash-foot {
    padding-left: clamp(22px, 5vw, 60px);
  }
}

/* 平板竖屏 / 手机：插画转为顶部横幅，文案居中落在下方 */
@media (max-width: 860px) {
  .splash {
    justify-content: flex-end;
    min-height: 100vh;
    min-height: 100dvh;
    padding: 52vh 0 clamp(24px, 6vw, 40px);
  }

  .splash-art {
    top: 0;
    right: 0;
    width: 100%;
    height: 56vh;
    -Webkit-mask-image: linear-gradient(180deg, rgba(0, 0, 0, 1) 46%, rgba(0, 0, 0, 0) 100%);
    mask-image: linear-gradient(180deg, rgba(0, 0, 0, 1) 46%, rgba(0, 0, 0, 0) 100%);
    transform: none;
  }

  .art-float {
    inset: 0;
    animation: none;
  }

  .art-float img {
    object-position: 100% 38%;
  }

  .splash-main {
    align-items: center;
    max-width: 100%;
    gap: 14px;
    padding: 0 20px;
    text-align: center;
  }

  .brand {
    gap: 10px;
  }

  .brand-mark {
    width: 52px;
    height: 52px;
  }

  .brand-words b {
    font-size: 17px;
  }

  .brand-words i {
    font-size: 9px;
    letter-spacing: 0.2em;
  }

  .title {
    font-size: clamp(27px, 6.6vw, 44px);
    white-space: nowrap;
  }

  .tagline {
    gap: 10px;
    font-size: clamp(12px, 3.1vw, 16px);
    letter-spacing: 0.1em;
  }

  .rule {
    width: clamp(18px, 6vw, 40px);
  }

  .actions {
    flex-direction: column;
    gap: 12px;
    margin-top: 8px;
  }

  .enter {
    height: 54px;
    width: min(320px, 82vw);
    padding: 0 24px;
    font-size: 16px;
  }

  .trust {
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px 16px;
    font-size: 11px;
  }

  .splash-foot {
    position: static;
    flex-wrap: wrap;
    justify-content: center;
    margin-top: 22px;
    padding-left: 0;
    padding-right: 0;
    font-size: 10px;
  }
}

@media (max-width: 400px) {
  .title {
    font-size: 26px;
  }
  .trust li:nth-child(3) {
    display: none;
  }
}

/* ============ 低动效偏好 ============ */
@media (prefers-reduced-motion: reduce) {
  .glow,
  .enter-glow,
  .splash-art,
  .art-float,
  .orbit-line,
  .rise,
  .is-leaving {
    animation: none !important;
    transition: none !important;
    transform: none;
  }
  .splash-art {
    -Webkit-mask-image: none;
    mask-image: none;
  }
}
</style>
<template>
  <canvas ref="cv" class="particle-canvas" aria-hidden="true"></canvas>
</template>

<script>
const TAU = Math.PI * 2;
// 预生成颜色，避免每帧拼接字符串造成 GC 抖动
const LINK_COLORS = Array.from({ length: 10 }, (_, i) => `rgba(110, 240, 170, ${(i / 10) * 0.34})`);

export default {
  name: "particleBg",
  props: {
    // 每多少像素面积放一个粒子
    density: { type: Number, default: 7000 },
    maxCount: { type: Number, default: 190 },
    linkDistance: { type: Number, default: 150 },
    interactive: { type: Boolean, default: true },
  },
  data() {
    return { parts: [], W: 0, H: 0, raf: 0, reduced: false, coarse: false };
  },
  mounted() {
    this.ctx = this.$refs.cv.getContext("2d");
    this.mouse = { x: -9999, y: -9999 };
    const mq = (q) => window.matchMedia && window.matchMedia(q).matches;
    this.reduced = !!mq("(prefers-reduced-motion: reduce)");
    this.coarse = !!mq("(pointer: coarse)");

    this.resize();
    window.addEventListener("resize", this.resize);
    if (this.interactive && !this.coarse) {
      window.addEventListener("mousemove", this.onMove, { passive: true });
      window.addEventListener("mouseleave", this.onLeave);
    }
    if (this.reduced) {
      // 用户要求减少动效：只画一帧静态星网
      this.draw();
    } else {
      this.raf = requestAnimationFrame(this.loop);
    }
  },
  beforeUnmount() {
    cancelAnimationFrame(this.raf);
    window.removeEventListener("resize", this.resize);
    window.removeEventListener("mousemove", this.onMove);
    window.removeEventListener("mouseleave", this.onLeave);
  },
  methods: {
    resize() {
      const cv = this.$refs.cv;
      if (!cv) return;
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      this.W = cv.clientWidth;
      this.H = cv.clientHeight;
      if (!this.W || !this.H) return;
      cv.width = Math.round(this.W * dpr);
      cv.height = Math.round(this.H * dpr);
      this.ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      this.init();
      if (this.reduced) this.draw();
    },
    init() {
      const scale = this.coarse ? 0.55 : 1;
      const count = Math.max(
        26,
        Math.min(this.maxCount, Math.round((this.W * this.H) / this.density) * scale)
      );
      this.parts = Array.from({ length: count }, () => ({
        x: Math.random() * this.W,
        y: Math.random() * this.H,
        vx: (Math.random() - 0.5) * 0.34,
        vy: (Math.random() - 0.5) * 0.34,
        r: Math.random() * 1.7 + 0.7,
        hue: Math.random() < 0.22 ? 170 : 145,
      }));
    },
    onMove(e) {
      const cv = this.$refs.cv;
      if (!cv) return;
      const rect = cv.getBoundingClientRect();
      this.mouse.x = e.clientX - rect.left;
      this.mouse.y = e.clientY - rect.top;
    },
    onLeave() {
      this.mouse.x = -9999;
      this.mouse.y = -9999;
    },
    update() {
      const { mouse } = this;
      for (const p of this.parts) {
        p.x += p.vx;
        p.y += p.vy;
        if (p.x < 0 || p.x > this.W) p.vx *= -1;
        if (p.y < 0 || p.y > this.H) p.vy *= -1;
        if (!this.coarse) {
          const dx = p.x - mouse.x;
          const dy = p.y - mouse.y;
          const d2 = dx * dx + dy * dy;
          if (d2 < 26000 && d2 > 1) {
            const d = Math.sqrt(d2);
            const f = (1 - d / 161) * 0.9;
            p.x += (dx / d) * f;
            p.y += (dy / d) * f;
          }
        }
      }
    },
    draw() {
      const ctx = this.ctx;
      if (!ctx) return;
      const parts = this.parts;
      ctx.clearRect(0, 0, this.W, this.H);

      // 连线
      const L = this.linkDistance;
      const L2 = L * L;
      ctx.lineWidth = 0.7;
      for (let i = 0; i < parts.length; i++) {
        const a = parts[i];
        for (let j = i + 1; j < parts.length; j++) {
          const b = parts[j];
          const dx = a.x - b.x;
          const dy = a.y - b.y;
          const d2 = dx * dx + dy * dy;
          if (d2 < L2) {
            const idx = Math.min(9, (1 - Math.sqrt(d2) / L) * 10) | 0;
            ctx.strokeStyle = LINK_COLORS[idx];
            ctx.beginPath();
            ctx.moveTo(a.x, a.y);
            ctx.lineTo(b.x, b.y);
            ctx.stroke();
          }
        }
      }

      // 粒子：光晕 + 亮核
      for (const p of parts) {
        const base = p.hue === 170 ? "64, 224, 208" : "110, 240, 170";
        ctx.fillStyle = `rgba(${base}, 0.10)`;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r * 4, 0, TAU);
        ctx.fill();

        ctx.fillStyle = `rgba(${base}, 0.95)`;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, TAU);
        ctx.fill();
      }
    },
    loop() {
      this.update();
      this.draw();
      this.raf = requestAnimationFrame(this.loop);
    },
  },
};
</script>

<style scoped>
.particle-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  display: block;
  z-index: 1;
  pointer-events: none;
}
</style>
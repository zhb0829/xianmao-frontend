<template>
  <div class="exit">
    <div class="exit-card">
      <span class="exit-ico">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9.4 20.6H5.6a2 2 0 0 1-2-2V5.4a2 2 0 0 1 2-2h3.8" />
          <path d="m16.2 16.6 4.6-4.6-4.6-4.6" />
          <path d="M20.8 12H9.4" />
        </svg>
      </span>

      <h3>确定退出登录？</h3>
      <p>退出后需要重新登录，才能查看收藏、订单与发布记录。</p>

      <div class="exit-actions">
        <button class="btn-ghost" @click="$emit('cancel')">继续浏览</button>
        <button class="btn-danger" @click="click_to_exit">确认退出</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'exit',
  methods: {
    click_to_exit() {
      this.$emit('exit', 'exit');
    },
  },
};
</script>

<style scoped>
.exit {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0 46px;
}

.exit-card {
  width: 100%;
  max-width: 420px;
  text-align: center;
  padding: 30px 28px 26px;
  border: 1px solid var(--cl-line);
  border-radius: 20px;
  background: var(--cl-surface);
}

.exit-ico {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #fdf0ea;
  color: #b4503a;
}

.exit-ico :deep(svg) {
  width: 24px;
  height: 24px;
}

.exit-card h3 {
  margin-top: 14px;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: var(--cl-ink);
}

.exit-card p {
  margin-top: 8px;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--cl-muted);
}

.exit-actions {
  display: flex;
  gap: 10px;
  margin-top: 22px;
}

.exit-actions button {
  flex: 1;
  height: 42px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.18s;
}

.btn-ghost {
  border: 1px solid #cfdad5;
  background: #fff;
  color: var(--cl-text);
}

.btn-ghost:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.btn-danger {
  background: #b4503a;
  color: #fff;
}

.btn-danger:hover {
  background: #9b4230;
  transform: translateY(-1px);
}
</style>
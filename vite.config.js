import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 纯前端项目：数据由浏览器内置 Mock（src/mock）接管，无需任何后端与代理配置
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // 忽略各类编辑器临时文件：
    // Windows 下监视正在写入的临时文件会触发 EBUSY 并让 dev server 崩溃
    watch: {
      ignored: [
        '**/.*.tmpdir/**',
        '**/*.tmp',
        '**/*.TMP',
        '**/*~RF*',
        '**/*.swp',
        '**/dist/**',
        '**/node_modules/**',
      ],
    },
  },
})
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { mockServer } from './mock/index.js'

// 默认启用演示 Mock（无需后端即可演示）；
// 需要联调真实后端时设置环境变量 VITE_MOCK=0，请求会走下面的 /api 代理。
const useMock = process.env.VITE_MOCK !== '0'

export default defineConfig({
  plugins: [vue(), ...(useMock ? [mockServer()] : [])],
  server: {
    port: 5173,
    // 忽略 Mock 目录与各类编辑器临时文件：
    // Windows 下监视正在写入的临时文件会触发 EBUSY 并让 dev server 崩溃
    watch: {
      ignored: [
        '**/mock/**',
        '**/.*.tmpdir/**',
        '**/*.tmp',
        '**/*.TMP',
        '**/*~RF*',
        '**/*.swp',
        '**/dist/**',
        '**/node_modules/**',
      ],
    },
    // Mock 模式下由中间件接管 /api，不注册代理，避免两套逻辑互相干扰
    proxy: useMock
      ? {}
      : {
        '/api': {
          target: 'http://localhost:9080',
          changeOrigin: true,
          secure: false,
        },
      },
  },
})
// 收藏状态（Pinia）
// 商品市场、商品详情、个人中心「我的收藏」共用同一份收藏集合，
// 任意一处收藏/取消后其他页面立即一致，避免各组件各自维护 likeStatus。
import { defineStore } from 'pinia'
import http from '../api/request.js'

export const useCollectStore = defineStore('collect', {
  state: () => ({
    ids: [],
    counts: {},
    loaded: false,
  }),

  getters: {
    has: state => id => state.ids.includes(Number(id)),
    total: state => state.ids.length,
    countOf: state => id => Number(state.counts[Number(id)]) || 0,
  },

  actions: {
    // 拉取当前登录用户的收藏 id 列表
    async fetchMine() {
      const res = await http({ method: 'get', url: '/person/liked' })
      const list = Array.isArray(res.data) ? res.data : []
      this.ids = list.map(g => Number(g.id))
      this.loaded = true
      return this.ids
    },

    // 本地同步（页面拿到接口结果后调用，保证 store 与后端一致）
    sync(id, on, count) {
      const key = Number(id)
      const index = this.ids.indexOf(key)
      if (on && index < 0) this.ids.push(key)
      if (!on && index >= 0) this.ids.splice(index, 1)
      if (count !== undefined && count !== null) this.counts[key] = Number(count) || 0
    },

    // 切换收藏：调用后端 collectAdd（返回 true 表示收藏成功），并同步 store
    async toggle(id) {
      const key = Number(id)
      const res = await http({ method: 'get', url: '/shop/collectAdd', params: { commodityId: key } })
      const on = res.data === true
      const old = this.counts[key]
      const next = on ? (old === undefined ? 1 : old + 1) : old === undefined ? 0 : Math.max(0, old - 1)
      this.sync(key, on, next)
      return { on, count: next }
    },

    // 「我的收藏」页取消收藏
    async remove(id) {
      const key = Number(id)
      await http({ method: 'get', url: '/collect/del', params: { commodityId: key } })
      this.sync(key, false)
    },
  },
})
// 用户状态（Pinia）
// 统一存 token、当前用户信息与管理员权限，页面通过 isLogged / isAdmin 判断，
// 不再各自 localStorage.getItem('token')。
import { defineStore } from 'pinia'
import { USER_KEY, getToken, setRequestToken } from '../api/request.js'

const EMPTY_USER = { id: 0, account: '', nickname: '', avatar: '', authority: 0 }

function readUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    const parsed = raw ? JSON.parse(raw) : null
    return parsed && typeof parsed === 'object' ? { ...EMPTY_USER, ...parsed } : { ...EMPTY_USER }
  } catch {
    return { ...EMPTY_USER }
  }
}

function persistUser(user) {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  } catch {
    /* 忽略 */
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    user: readUser(),
  }),

  getters: {
    isLogged: state => !!state.token,
    isAdmin: state => Number(state.user.authority) === 1,
    displayName: state => state.user.nickname || state.user.account || '同学',
    avatar: state => state.user.avatar || '',
  },

  actions: {
    // 登录成功后写入（token / 用户信息 / 权限）
    setLogin(payload) {
      const data = payload || {}
      this.token = data.token || ''
      this.user = {
        id: Number(data.id) || 0,
        account: data.account || '',
        nickname: data.nickname || data.account || '',
        avatar: data.avatar || this.user.avatar || '',
        authority: Number(data.authority) || 0,
      }
      setRequestToken(this.token)
      persistUser(this.user)
    },

    // 资料更新（昵称、头像、手机号、权限变化）
    setProfile(profile) {
      const p = profile || {}
      this.user = {
        ...this.user,
        id: Number(p.id) || this.user.id,
        nickname: p.nickname || this.user.nickname,
        avatar: p.avatar || this.user.avatar,
        authority: p.authority !== undefined ? Number(p.authority) || 0 : this.user.authority,
      }
      persistUser(this.user)
    },

    logout() {
      this.token = ''
      this.user = { ...EMPTY_USER }
      setRequestToken('')
      try {
        localStorage.removeItem(USER_KEY)
      } catch {
        /* 忽略 */
      }
    },

    // 刷新后恢复：token 已在初始化时读取，这里补齐用户资料
    hydrate(profile) {
      if (!this.token) return
      if (profile) this.setProfile(profile)
    },
  },
})
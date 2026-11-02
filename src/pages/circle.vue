<template>
  <div class="cl-app circle-page">
    <site-header active="circle" />

    <main>
      <!-- 页面头 -->
      <section class="circle-top">
        <div class="cl-wrap top-inner">
          <div class="top-text">
            <p class="eyebrow">CIRCLE · 校园圈</p>
            <h1 class="circle-title">校园里的新鲜事</h1>
            <p class="circle-sub">
              出闲置、求推荐、约球、吐槽都可以在这里说 · 仅限在校学生
            </p>
          </div>

          <div class="top-actions">
            <button class="btn-primary" @click="focusComposer">
              <span class="ico" v-html="ICON.plus"></span>
              发布动态
            </button>
          </div>
        </div>
      </section>

      <section class="cl-wrap circle-body">
        <div class="layout">
          <!-- 主列 -->
          <div class="feed-col">
            <!-- 发布框 -->
            <div class="composer-card">
              <div class="composer-row">
                <span class="avatar big" aria-hidden="true">{{ initial(myName) }}</span>
                <textarea
                  ref="composerInput"
                  v-model="draft.text"
                  class="composer-input"
                  rows="3"
                  maxlength="300"
                  :placeholder="hasToken ? '分享点什么…出闲置、找人、约球都行' : '登录后就能发动态'"
                  aria-label="动态内容"
                  @focus="onFocusComposer"
                ></textarea>
              </div>

              <div class="composer-tools">
                <div class="topics" role="group" aria-label="选择话题">
                  <button
                    v-for="t in POST_TOPICS"
                    :key="t"
                    class="topic-chip"
                    :class="{ on: draft.topic === t }"
                    type="button"
                    @click="draft.topic = t"
                  >
                    {{ t }}
                  </button>
                </div>

                <div class="composer-right">
                  <label class="upload-btn" :class="{ disabled: draft.images.length >= 3 }">
                    <span class="ico" v-html="ICON.image"></span>
                    图片
                    <input
                      type="file"
                      accept="image/jpeg,image/png,image/Webp"
                      multiple
                      hidden
                      :disabled="draft.images.length >= 3"
                      @change="onPickImages"
                    />
                  </label>

                  <button class="btn-primary sm" type="button" :disabled="posting" @click="publish">
                    {{ posting ? '发布中…' : '发布' }}
                  </button>
                </div>
              </div>

              <div v-if="draft.images.length" class="thumbs">
                <div class="thumb" v-for="(img, i) in draft.images" :key="i">
                  <img :src="img" alt="" />
                  <button class="thumb-del" type="button" aria-label="移除图片" @click="draft.images.splice(i, 1)">
                    ×
                  </button>
                </div>
                <p class="thumb-hint">{{ draft.images.length }} / 3 张</p>
              </div>

              <p v-if="draftError" class="draft-error" role="alert">{{ draftError }}</p>
            </div>

            <!-- 话题筛选 -->
            <div class="feed-tabs" role="tablist" aria-label="按话题筛选动态">
              <button
                class="tab"
                :class="{ on: !topic }"
                role="tab"
                :aria-selected="!topic"
                @click="setTopic('')"
              >
                全部
              </button>
              <button
                v-for="t in POST_TOPICS"
                :key="t"
                class="tab"
                :class="{ on: topic === t }"
                role="tab"
                :aria-selected="topic === t"
                @click="setTopic(t)"
              >
                {{ t }}
              </button>
            </div>

            <!-- 动态流 -->
            <article class="post" v-for="p in posts" :key="p.id">
              <header class="post-head">
                <span class="avatar" aria-hidden="true">{{ initial(p.author.name) }}</span>
                <div class="post-meta">
                  <p class="post-name">
                    {{ p.author.name }}
                    <span class="topic-tag">{{ p.topic }}</span>
                  </p>
                  <time class="post-time" :datetime="new Date(p.createdAt).toISOString()">
                    {{ timeAgo(p.createdAt) }}
                  </time>
                </div>
                <button
                  v-if="p.owner === ownerKey"
                  class="mini-del"
                  @click="askRemove(p)"
                >
                  {{ pendingRemove === p.id ? '确认删除？' : '删除' }}
                </button>
              </header>

              <p class="post-text">{{ p.text }}</p>

              <div v-if="p.images.length" class="post-images" :class="`n${p.images.length}`">
                <img
                  v-for="(img, i) in p.images"
                  :key="i"
                  :src="img"
                  :alt="`${p.author.name} 发布的第 ${i + 1} 张图片`"
                  loading="lazy"
                />
              </div>

              <footer class="post-foot">
                <button class="action" :class="{ liked: p.liked }" @click="like(p)">
                  <span class="ico" v-html="p.liked ? ICON.heartSolid : ICON.heart"></span>
                  <span>{{ p.likeCount }}</span>
                </button>
                <button class="action" @click="toggleComments(p)">
                  <span class="ico" v-html="ICON.comment"></span>
                  <span>{{ p.comments.length }}</span>
                </button>
              </footer>

              <div v-if="openComments === p.id" class="comments">
                <div class="comment" v-for="c in p.comments" :key="c.id">
                  <b>{{ c.name }}</b>
                  <span>{{ c.text }}</span>
                </div>
                <p v-if="!p.comments.length" class="comment-empty">还没有评论，来说两句</p>

                <form class="comment-form" @submit.prevent="submitComment(p)">
                  <input
                    v-model="commentDraft"
                    class="comment-input"
                    maxlength="100"
                    placeholder="友善发言，最多 100 字"
                    aria-label="评论内容"
                  />
                  <button type="submit" class="btn-ghost" :disabled="!commentDraft.trim()">发送</button>
                </form>
              </div>
            </article>

            <div v-if="!posts.length" class="empty">
              <span class="empty-ico" v-html="ICON.comment"></span>
              <p class="empty-title">这个话题还没有动态</p>
              <p class="empty-desc">做第一个发帖的人吧</p>
              <button class="btn-primary" @click="focusComposer">发一条动态</button>
            </div>
          </div>

          <!-- 右侧栏 -->
          <aside class="side-col">
            <div class="side-card me">
              <span class="avatar xl" aria-hidden="true">{{ initial(myName) }}</span>
              <p class="me-name">{{ myName || '未登录' }}</p>
              <p class="me-desc">{{ myPostCount }} 条动态 · 收到 {{ myLikeCount }} 个赞</p>
              <button class="btn-ghost wide" @click="focusComposer">发一条动态</button>
            </div>

            <div class="side-card">
              <h3>热门话题</h3>
              <div class="topic-list">
                <button
                  v-for="t in topicStats"
                  :key="t.name"
                  class="topic-row"
                  @click="setTopic(t.name)"
                >
                  <span>{{ t.name }}</span>
                  <em>{{ t.count }}</em>
                </button>
              </div>
            </div>

            <div class="side-card">
              <h3>社区约定</h3>
              <ul class="rules">
                <li>只发校内真实信息</li>
                <li>交易走平台，不私下转账</li>
                <li>友善发言，不人身攻击</li>
              </ul>
            </div>
          </aside>
        </div>
      </section>
    </main>

    <site-footer />
  </div>
</template>

<script>
import siteHeader from '../components/siteHeader.vue'
import siteFooter from '../components/siteFooter.vue'
import { fileToCompressedDataUrl } from '../utils/image'
import {
  POST_TOPICS,
  addComment,
  addPost,
  currentOwner,
  hasToken,
  loadPosts,
  removePost,
  timeAgo,
  toggleLike,
  uid,
} from '../api/localStore'

const svg = d =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`

const ICON = {
  plus: svg('<path d="M12 5.5v13M5.5 12h13"/>'),
  image: svg('<rect x="3" y="4.5" width="18" height="15" rx="3"/><circle cx="8.6" cy="10" r="1.6"/><path d="m4 17 5-5 4.5 4.5L16 14l4 4"/>'),
  heart: svg('<path d="M20.4 5.9a4.9 4.9 0 0 0-6.9 0L12 7.4l-1.5-1.5a4.9 4.9 0 1 0-6.9 6.9L12 21l8.4-8.2a4.9 4.9 0 0 0 0-6.9z"/>'),
  heartSolid:
    '<svg viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M20.4 5.9a4.9 4.9 0 0 0-6.9 0L12 7.4l-1.5-1.5a4.9 4.9 0 1 0-6.9 6.9L12 21l8.4-8.2a4.9 4.9 0 0 0 0-6.9z"/></svg>',
  comment: svg('<path d="M20 14.5a2.5 2.5 0 0 1-2.5 2.5H8l-4 3.5V6.5A2.5 2.5 0 0 1 6.5 4h11A2.5 2.5 0 0 1 20 6.5z"/>'),
}

export default {
  name: 'circle',
  components: { siteHeader, siteFooter },
  data() {
    return {
      ICON,
      POST_TOPICS,
      hasToken: false,
      posts: [],
      allPosts: [],
      topic: '',
      ownerKey: '',
      myName: '',
      draft: { text: '', topic: '分享', images: [] },
      draftError: '',
      posting: false,
      openComments: '',
      commentDraft: '',
      pendingRemove: '',
      removeTimer: null,
    }
  },
  computed: {
    myPostCount() {
      return this.allPosts.filter(p => p.owner === this.ownerKey).length
    },
    myLikeCount() {
      return this.allPosts.filter(p => p.liked).length
    },
    topicStats() {
      const map = new Map()
      this.allPosts.forEach(p => {
        map.set(p.topic, (map.get(p.topic) || 0) + 1)
      })
      return [...map.entries()]
        .map(([name, count]) => ({ name, count }))
        .sort((a, b) => b.count - a.count)
    },
  },
  mounted() {
    const q = (this.$route && this.$route.query) || {}
    if (q.topic && POST_TOPICS.includes(q.topic)) this.topic = q.topic
    this.hasToken = hasToken()
    this.ownerKey = currentOwner()
    this.refresh()
    this.loadMyName()
  },
  beforeUnmount() {
    if (this.removeTimer) clearTimeout(this.removeTimer)
  },
  methods: {
    timeAgo,
    initial(name) {
      const n = String(name || '').trim()
      return n ? n.slice(0, 1).toUpperCase() : '同'
    },
    loadMyName() {
      if (!hasToken()) return
      this.$http({ url: '/personal/getUserInfo', method: 'get' })
        .then(res => {
          this.myName = (res.data && res.data.nickname) || ''
        })
        .catch(() => {})
    },
    refresh() {
      this.ownerKey = currentOwner()
      // 侧栏统计要用全量数据，列表按当前话题过滤
      this.allPosts = loadPosts('')
      this.posts = this.topic ? this.allPosts.filter(p => p.topic === this.topic) : this.allPosts
    },
    setTopic(topic) {
      this.topic = topic
      this.refresh()
      const query = topic ? { topic } : {}
      const current = (this.$route && this.$route.query) || {}
      if ((current.topic || '') === (query.topic || '')) return
      this.$router.replace({ path: '/circle', query }).catch(() => {})
    },

    // ---------- 发布 ----------
    focusComposer() {
      if (!hasToken()) {
        this.$emit('showmsg', { info: '登录后才能发布动态', colorType: 3 })
        this.$router.push('/customer')
        return
      }
      const el = this.$refs.composerInput
      if (el) {
        el.focus()
        el.scrollIntoView({ behavior: 'smooth', block: 'center' })
      }
    },
    onFocusComposer() {
      if (!hasToken()) {
        this.$emit('showmsg', { info: '登录后才能发布动态', colorType: 3 })
        this.$router.push('/customer')
      }
    },
    onPickImages(e) {
      const files = Array.from(e.target.files || [])
      if (!files.length) return
      const room = 3 - this.draft.images.length
      if (room <= 0) {
        this.$emit('showmsg', { info: '最多上传 3 张图片', colorType: 3 })
        e.target.value = ''
        return
      }
      const picked = files.slice(0, room)
      Promise.all(
        picked.map(file =>
          fileToCompressedDataUrl(file).catch(err => {
            this.$emit('showmsg', { info: err.message || '图片处理失败', colorType: 3 })
            return null
          })
        )
      ).then(list => {
        this.draft.images = this.draft.images.concat(list.filter(Boolean)).slice(0, 3)
      })

      e.target.value = ''
    },
    publish() {
      if (this.posting) return
      if (!hasToken()) {
        this.$emit('showmsg', { info: '登录后才能发布动态', colorType: 3 })
        this.$router.push('/customer')
        return
      }

      const text = this.draft.text.trim()
      if (!text && !this.draft.images.length) {
        this.draftError = '写点什么，或者至少上传一张图片'
        const el = this.$refs.composerInput
        if (el) el.focus()
        return
      }
      this.draftError = ''

      this.posting = true
      const created = addPost({
        text: text || '分享了一张图片',
        topic: this.draft.topic,
        images: this.draft.images,
        authorName: this.myName || '我',
      })
      this.posting = false

      if (!created) {
        this.draftError = '本地存储空间不足，请先删除一些图片后重试'
        return
      }

      this.draft = { text: '', topic: '分享', images: [] }
      this.refresh()
      this.$emit('showmsg', { info: '已发布到校园圈', colorType: 1 })
    },

    // ---------- 点赞 / 评论 ----------
    like(post) {
      if (!hasToken()) {
        this.$emit('showmsg', { info: '登录后才能点赞', colorType: 3 })
        this.$router.push('/customer')
        return
      }
      const liked = toggleLike(post.id)
      post.liked = liked
      post.likeCount += liked ? 1 : -1
    },
    toggleComments(post) {
      this.openComments = this.openComments === post.id ? '' : post.id
      this.commentDraft = ''
    },
    submitComment(post) {
      if (!hasToken()) {
        this.$emit('showmsg', { info: '登录后才能评论', colorType: 3 })
        this.$router.push('/customer')
        return
      }
      const text = this.commentDraft.trim()
      if (!text) return

      const comment = {
        id: uid('comment'),
        name: this.myName || '我',
        text,
        createdAt: Date.now(),
      }
      if (!addComment(post.id, comment)) {
        this.$emit('showmsg', { info: '本地存储空间不足，评论失败', colorType: 3 })
        return
      }
      post.comments.push(comment)
      this.commentDraft = ''
    },

    // ---------- 删除（二次确认） ----------
    askRemove(post) {
      if (this.pendingRemove === post.id) {
        this.doRemove(post)
        return
      }
      this.pendingRemove = post.id
      this.$emit('showmsg', { info: '再次点击「确认删除？」即可删除', colorType: 2 })
      if (this.removeTimer) clearTimeout(this.removeTimer)
      this.removeTimer = setTimeout(() => {
        this.pendingRemove = ''
      }, 3200)
    },
    doRemove(post) {
      this.pendingRemove = ''
      if (!removePost(post.id)) {
        this.$emit('showmsg', { info: '删除失败，请稍后重试', colorType: 3 })
        return
      }
      this.refresh()
      this.$emit('showmsg', { info: '动态已删除', colorType: 1 })
    },
  },
}
</script>

<style scoped>
.circle-page {
  background: var(--cl-surface);
}

/* ---------- 页面头 ---------- */
.circle-top {
  background: linear-gradient(180deg, #f6faf8 0%, #ffffff 100%);
  border-bottom: 1px solid var(--cl-line);
}

.top-inner {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding-top: 42px;
  padding-bottom: 30px;
}

.eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1.2px;
  color: var(--cl-brand);
}

.circle-title {
  margin-top: 8px;
  font-size: 34px;
  font-weight: 700;
  letter-spacing: -1px;
  line-height: 1.2;
  color: var(--cl-ink);
}

.circle-sub {
  margin-top: 8px;
  font-size: 13px;
  color: var(--cl-muted);
}

.top-actions {
  flex-shrink: 0;
  padding-bottom: 4px;
}

/* ---------- 布局 ---------- */
.circle-body {
  padding-top: 22px;
  padding-bottom: 64px;
}

.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 20px;
  align-items: start;
}

.feed-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.side-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: sticky;
  top: 96px;
}

/* ---------- 发布框 ---------- */
.composer-card {
  padding: 18px 20px;
  border: 1px solid var(--cl-line);
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 10px 28px rgba(15, 27, 22, 0.04);
}

.composer-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.composer-input {
  flex: 1;
  min-width: 0;
  min-height: 92px;
  padding: 8px 12px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: var(--cl-surface);
  font-size: 13.5px;
  line-height: 1.7;
  color: var(--cl-ink);
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.18s, background 0.18s;
}

.composer-input::placeholder {
  color: var(--cl-muted);
}

.composer-input:focus {
  outline: none;
  background: #fff;
  border-color: rgba(0, 166, 126, 0.5);
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
}

.composer-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 12px;
  padding-left: 48px;
}

.topics {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.topic-chip {
  height: 28px;
  padding: 0 11px;
  border-radius: 999px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-muted);
  font-size: 11.5px;
  transition: all 0.18s;
}

.topic-chip:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.topic-chip.on {
  background: var(--cl-brand-soft);
  border-color: rgba(0, 166, 126, 0.35);
  color: var(--cl-brand-strong);
  font-weight: 600;
}

.composer-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 32px;
  padding: 0 12px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.18s;
}

.upload-btn:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.upload-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.upload-btn .ico :deep(svg) {
  width: 14px;
  height: 14px;
}

.thumbs {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-left: 48px;
  flex-wrap: wrap;
}

.thumb {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--cl-line);
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-del {
  position: absolute;
  top: 3px;
  right: 3px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: rgba(15, 27, 22, 0.62);
  color: #fff;
  font-size: 11px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb-hint {
  font-size: 11px;
  color: var(--cl-muted);
}

.draft-error {
  margin-top: 10px;
  padding-left: 48px;
  font-size: 11.5px;
  color: #b4503a;
}

/* ---------- 话题筛选 ---------- */
.feed-tabs {
  display: flex;
  gap: 20px;
  padding: 0 4px;
  border-bottom: 1px solid var(--cl-line);
}

.tab {
  position: relative;
  padding: 0 0 10px;
  font-size: 13px;
  color: var(--cl-muted);
  transition: color 0.18s;
}

.tab:hover {
  color: var(--cl-ink);
}

.tab.on {
  color: var(--cl-brand-strong);
  font-weight: 600;
}

.tab.on::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 2px;
  border-radius: 2px;
  background: var(--cl-brand);
}

/* ---------- 动态 ---------- */
.post {
  padding: 18px 20px 14px;
  border: 1px solid var(--cl-line);
  border-radius: 18px;
  background: #fff;
  transition: border-color 0.18s, box-shadow 0.18s;
}

.post:hover {
  border-color: rgba(0, 166, 126, 0.26);
  box-shadow: 0 12px 30px rgba(15, 27, 22, 0.05);
}

.post-head {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.post-meta {
  flex: 1;
  min-width: 0;
}

.post-name {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
  font-weight: 600;
  color: var(--cl-ink);
}

.topic-tag {
  display: inline-flex;
  align-items: center;
  height: 19px;
  padding: 0 7px;
  border-radius: 5px;
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
  font-size: 10.5px;
  font-weight: 600;
}

.post-time {
  display: block;
  margin-top: 3px;
  font-size: 11px;
  color: var(--cl-muted);
}

.mini-del {
  height: 26px;
  padding: 0 10px;
  border-radius: 7px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: #a4553a;
  font-size: 11.5px;
  white-space: nowrap;
  transition: all 0.18s;
}

.mini-del:hover {
  border-color: rgba(164, 85, 58, 0.42);
  background: #fdf3ef;
}

.post-text {
  margin-top: 10px;
  font-size: 13.5px;
  line-height: 1.75;
  color: var(--cl-ink);
  white-space: pre-wrap;
  word-break: break-word;
}

.post-images {
  display: grid;
  gap: 6px;
  margin-top: 12px;
  overflow: hidden;
  border-radius: 12px;
}

.post-images.n1 {
  grid-template-columns: 1fr;
  max-width: 560px;
}

.post-images.n2 {
  grid-template-columns: 1fr 1fr;
  max-width: 600px;
}

.post-images.n3 {
  grid-template-columns: repeat(3, 1fr);
  max-width: 640px;
}

.post-images img {
  width: 100%;
  height: 190px;
  object-fit: cover;
  display: block;
}

.post-images.n1 img {
  height: 330px;
}

.post-foot {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid #f1f4f2;
}

.action {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 30px;
  padding: 0 10px;
  border-radius: 8px;
  color: var(--cl-muted);
  font-size: 12px;
  transition: background 0.18s, color 0.18s;
}

.action:hover {
  background: var(--cl-surface);
  color: var(--cl-ink);
}

.action.liked {
  color: #c04a5a;
}

.action .ico :deep(svg) {
  width: 15px;
  height: 15px;
}

/* ---------- 评论 ---------- */
.comments {
  margin-top: 10px;
  padding: 12px 14px;
  border-radius: 12px;
  background: var(--cl-surface);
}

.comment {
  display: flex;
  gap: 8px;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--cl-text);
  padding: 3px 0;
}

.comment b {
  flex-shrink: 0;
  font-weight: 600;
  color: var(--cl-ink);
}

.comment-empty {
  font-size: 12px;
  color: var(--cl-muted);
}

.comment-form {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.comment-input {
  flex: 1;
  min-width: 0;
  height: 34px;
  padding: 0 12px;
  border: 1px solid var(--cl-line);
  border-radius: 9px;
  background: #fff;
  font-size: 12.5px;
  color: var(--cl-ink);
}

.comment-input:focus {
  outline: none;
  border-color: rgba(0, 166, 126, 0.55);
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
}

/* ---------- 侧栏 ---------- */
.side-card {
  padding: 18px;
  border: 1px solid var(--cl-line);
  border-radius: 18px;
  background: #fff;
}

.side-card h3 {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--cl-ink);
  margin-bottom: 10px;
}

.me {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 4px;
  background: linear-gradient(180deg, #f9fdfb 0%, #ffffff 100%);
}

.me-name {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 600;
  color: var(--cl-ink);
}

.me-desc {
  font-size: 11.5px;
  color: var(--cl-muted);
}

.me .btn-ghost.wide {
  width: 100%;
  height: 36px;
  margin-top: 10px;
  justify-content: center;
}

.topic-list {
  display: flex;
  flex-direction: column;
}

.topic-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 9px 2px;
  border-bottom: 1px solid #f4f7f5;
  font-size: 12.5px;
  color: var(--cl-text);
  transition: color 0.18s;
}

.topic-row:last-child {
  border-bottom: none;
}

.topic-row:hover {
  color: var(--cl-brand-strong);
}

.topic-row em {
  font-style: normal;
  font-size: 11.5px;
  color: var(--cl-muted);
}

.rules {
  display: flex;
  flex-direction: column;
  gap: 7px;
  padding-left: 16px;
  list-style: disc;
}

.rules li {
  font-size: 12px;
  line-height: 1.6;
  color: var(--cl-text);
}

/* ---------- 头像 ---------- */
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 13px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar.big {
  width: 40px;
  height: 40px;
  font-size: 15px;
}

.avatar.xl {
  width: 56px;
  height: 56px;
  font-size: 20px;
}

/* ---------- 按钮 ---------- */
.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.18s, transform 0.18s;
}

.btn-primary.sm {
  height: 32px;
  padding: 0 16px;
  font-size: 12.5px;
}

.btn-primary:hover:not(:disabled) {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 34px;
  padding: 0 14px;
  border-radius: 9px;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-text);
  font-size: 12px;
  white-space: nowrap;
  transition: all 0.18s;
}

.btn-ghost:hover:not(:disabled) {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
  background: var(--cl-brand-mist);
}

.btn-ghost:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.ico {
  display: inline-flex;
}

.ico :deep(svg) {
  width: 15px;
  height: 15px;
}

/* ---------- 空状态 ---------- */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 7px;
  padding: 64px 20px;
  border: 1px dashed var(--cl-line);
  border-radius: 18px;
  background: #fff;
}

.empty-ico {
  display: inline-flex;
  color: var(--cl-muted-2);
}

.empty-ico :deep(svg) {
  width: 32px;
  height: 32px;
}

.empty-title {
  font-size: 14.5px;
  font-weight: 600;
  color: var(--cl-ink);
}

.empty-desc {
  font-size: 12.5px;
  color: var(--cl-muted);
  margin-bottom: 6px;
}

/* ---------- 响应式 ---------- */
@media (max-width: 1100px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .side-col {
    position: static;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .side-col .side-card {
    flex: 1 1 260px;
  }
  .top-inner {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

@media (max-width: 760px) {
  .circle-title {
    font-size: 26px;
  }
  .composer-tools,
  .thumbs,
  .draft-error {
    padding-left: 0;
  }
  .post-images img {
    height: 140px;
  }
  .post-images.n1 img {
    height: 220px;
  }
}

/* 键盘焦点 */
.circle-page :focus-visible {
  outline: 2px solid var(--cl-brand);
  outline-offset: 2px;
}

@media (prefers-reduced-motion: reduce) {
  .post,
  .btn-primary,
  .action,
  .tab {
    transition: none;
  }
  .post:hover,
  .btn-primary:hover {
    transform: none;
  }
}
</style>
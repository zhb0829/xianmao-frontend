<template>
  <div class="cl-app person-page">
    <site-header :active="tab === 6 ? 'like' : 'account'" />

    <main class="person-main">
      <!-- ================= 页头 ================= -->
      <div class="page-head">
        <div class="cl-wrap head-inner">
          <div class="head-title">
            <span class="head-mark">
              <img :src="ico.refresh" alt="" />
            </span>
            <div class="head-words">
              <h1>个人中心</h1>
              <p>{{ logged ? '管理你的闲置、订单与账户' : '登录后查看你的收藏、订单与闲置' }}</p>
            </div>
          </div>

          <div class="head-actions">
            <button class="head-btn" aria-label="消息" @click="openTab(10)">
              <span v-html="ICON.message"></span>
            </button>
            <button class="head-btn" aria-label="设置" @click="openTab(4)">
              <span v-html="ICON.gear"></span>
            </button>
          </div>
        </div>
      </div>

      <!-- ================= 未登录 ================= -->
      <section v-if="!logged" class="cl-wrap login-area">
        <div class="login-intro">
          <p class="login-eyebrow">MY ACCOUNT · 个人中心</p>
          <h2>登录后，闲置有迹可循</h2>
          <p class="login-desc">
            收藏过的书、正在路上的包裹、发布中的闲置，都收在这一个页面里，随取随看。
          </p>
          <ul class="login-points">
            <li v-for="p in loginPoints" :key="p">
              <span class="dot" aria-hidden="true"></span>{{ p }}
            </li>
          </ul>
        </div>

        <div class="login-card">
          <login @login="login" v-bind="$attrs"></login>
        </div>
      </section>

      <!-- ================= 已登录：账户概览 ================= -->
      <section v-else-if="tab === 1" class="cl-wrap overview">
        <!-- 用户卡 -->
        <div class="profile-card">
          <div class="profile-main">
            <div class="avatar-box">
              <img
                v-if="avatarOk && person.personImg"
                :src="'data:image/Jpeg;base64,' + person.personImg"
                alt="头像"
                @error="avatarOk = false"
              />
              <span v-else class="avatar-letter">{{ initial }}</span>
              <i class="avatar-dot" aria-hidden="true"></i>
            </div>

            <div class="profile-info">
              <div class="name-row">
                <h2 class="profile-name">{{ person.accountName || '同学' }}</h2>
                <span class="verify-pill">
                  <span class="pill-check" v-html="ICON.check"></span>
                  {{ verifyText }}
                </span>
              </div>

              <p class="profile-school">
                <span class="row-ico" v-html="ICON.cap"></span>
                集美大学诚毅学院 · 校园认证用户
              </p>

              <div class="profile-credit">
                <span class="credit-star" v-html="ICON.starSolid"></span>
                <span>信用分 {{ creditScore }}</span>
                <i class="dot-sep"></i>
                <span>平台担保交易</span>
                <span class="credit-badge">{{ creditText }}</span>
              </div>
            </div>
          </div>

          <!-- 数据卡 -->
          <div class="stats-box">
            <div class="stats-row">
              <button class="stat" v-for="s in statCards" :key="s.key" @click="openTab(s.list)">
                <b>{{ s.value }}</b>
                <span>{{ s.label }}</span>
              </button>
            </div>

            <div class="stats-foot">
              <span class="foot-left">
                <span class="foot-ico" v-html="ICON.coins"></span>
                累计成交金额
                <b>¥{{ amountText }}</b>
              </span>
              <button v-if="overview.trade.comment" class="foot-right" @click="openOrders(2)">
                <i class="pending-dot"></i>{{ overview.trade.comment }} 笔待评价
              </button>
            </div>
          </div>

          <!-- 操作 -->
          <div class="profile-actions">
            <button class="btn btn-ghost" @click="openTab(4)">
              <span v-html="ICON.pencil"></span>编辑资料
            </button>
            <button class="btn btn-brand" @click="openTab(2)">
              <span v-html="ICON.plus"></span>发布闲置
            </button>
          </div>
        </div>

        <div class="ov-grid">
          <div class="ov-main">
            <!-- 我的交易 -->
            <div class="card trade-card">
              <div class="card-head">
                <h3>我的交易</h3>
                <button class="link" @click="openOrders(2)">
                  全部记录<span class="chev" v-html="ICON.chevron"></span>
                </button>
              </div>

              <div class="trade-grid">
                <button
                  v-for="t in tradeItems"
                  :key="t.key"
                  class="trade-item"
                  @click="openOrders(2)"
                >
                  <span class="trade-ico" v-html="t.icon"></span>
                  <span class="trade-label">
                    {{ t.label }}
                    <em v-if="t.count">{{ t.count }}</em>
                  </span>
                </button>
              </div>
            </div>

            <!-- 我的闲置 -->
            <div class="card idle-card">
              <div class="card-head">
                <h3>我的闲置</h3>
                <button class="mini-btn" @click="openTab(9)">
                  <span v-html="ICON.layers"></span>批量管理
                </button>
              </div>

              <div class="idle-tabs">
                <button
                  v-for="t in idleTabs"
                  :key="t.key"
                  class="idle-tab"
                  :class="{ on: idleTab === t.key }"
                  @click="idleTab = t.key"
                >
                  {{ t.label }}<em>{{ idleCount(t.key) }}</em>
                </button>
              </div>

              <div class="idle-list">
                <button
                  class="idle-item"
                  v-for="g in idleShown"
                  :key="g.id"
                  @click="gopen({ id: g.id, name: g.tradeName, price: g.price })"
                >
                  <span class="idle-pic">
                    <img v-if="g.pic" :src="g.pic" alt="" />
                    <span v-else class="pic-fallback">{{ String(g.tradeName).slice(0, 1) }}</span>
                  </span>
                  <span class="idle-info">
                    <span class="idle-name">{{ g.tradeName }}</span>
                    <span class="idle-price">
                      <b>¥{{ g.price }}</b>
                      <s v-if="g.originPrice">原价 {{ g.originPrice }}</s>
                    </span>
                    <span class="idle-meta">
                      {{ g.category || '校园闲置' }} · {{ g.timeText || '在售中' }}
                    </span>
                  </span>
                </button>

                <p class="idle-empty" v-if="!idleShown.length">
                  {{ idleTab === 'listing' ? '还没有在售的闲置，发布一件试试' : '这里暂时是空的' }}
                </p>
              </div>

              <button v-if="idleTab === 'listing' && idleCount('listing')" class="idle-more" @click="openTab(9)">
                查看全部 {{ idleCount('listing') }} 件在售闲置
                <span class="chev" v-html="ICON.chevron"></span>
              </button>
            </div>
          </div>

          <div class="ov-side">
            <!-- 常用功能 -->
            <div class="card func-card">
              <div class="card-head">
                <h3>常用功能</h3>
              </div>

              <div class="func-grid">
                <button
                  v-for="f in funcItems"
                  :key="f.key"
                  class="func-item"
                  @click="f.action()"
                >
                  <span class="func-ico" v-html="f.icon"></span>
                  <span class="func-label">
                    {{ f.label }}<em v-if="f.count">{{ f.count }}</em>
                  </span>
                </button>
              </div>
            </div>

            <!-- 设置与服务 -->
            <div class="card service-card">
              <div class="card-head">
                <h3>设置与服务</h3>
              </div>

              <div class="service-list">
                <button
                  v-for="s in serviceItems"
                  :key="s.key"
                  class="service-row"
                  @click="s.action()"
                >
                  <span class="service-ico" v-html="s.icon"></span>
                  <span class="service-label">{{ s.label }}</span>
                  <span v-if="s.note" class="service-note" :class="s.tone">{{ s.note }}</span>
                  <span class="service-arrow" v-html="ICON.chevron"></span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <p class="brand-note">闲猫二手 <i>CAMPUS LOOP</i> · 仅限在校学生使用 · 发布与成交全程零服务费</p>
      </section>

      <!-- ================= 子页面 ================= -->
      <section v-else class="cl-wrap sub-page">
        <button class="back-btn" @click="backToOverview">
          <span class="chev flip" v-html="ICON.chevron"></span>返回个人中心
        </button>

        <div class="sub-head">
          <h2>{{ tabTitle }}</h2>
          <p>{{ tabDesc }}</p>
        </div>

        <div class="sub-body" :class="{ legacy: tab >= 6 }">
          <status
            v-if="tab >= 6 && tab <= 9"
            ref="statusRef"
            :personData="person"
            :activeList="statusTab"
            @gopene="gopen"
            @refresh="loadAll"
            v-bind="$attrs"
          ></status>

          <messages
            v-else-if="tab === 10"
            @open-goods="gopenById"
            @showmsg="notifyMsg"
          ></messages>

          <publish v-else-if="tab === 2" @refresh="loadAll" @done="backToOverview" v-bind="$attrs"></publish>

          <manage v-else-if="tab === 3" @exit="exit" v-bind="$attrs"></manage>

          <settings v-else-if="tab === 4" @refresh="loadAll" v-bind="$attrs"></settings>

          <exit v-else-if="tab === 5" @exit="exit" @cancel="backToOverview" v-bind="$attrs"></exit>
        </div>
      </section>
    </main>

    <site-footer />

    <goodsDetail
      v-if="gIsOpen"
      :gData="goods"
      :Type="gType"
      @gclose="gclose"
      v-bind="$attrs"
    ></goodsDetail>
  </div>
</template>

<script>
import status from '../components/status.vue';
import settings from '../components/settings.vue';
import exit from '../components/exit.vue';
import login from '../components/login.vue';
import goodsDetail from '../components/goodsDetail.vue';
import publish from '../components/publish.vue';
import manage from '../components/manage.vue';
import messages from '../components/messages.vue';
import siteHeader from '../components/siteHeader.vue';
import siteFooter from '../components/siteFooter.vue';
import { useUserStore } from '../stores/user';
import { useCollectStore } from '../stores/collect';

// 线性图标（内联 SVG，颜色跟随 currentColor）
const svg = d =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;

const ICON = {
  message: svg('<path d="M20 14.5a2.5 2.5 0 0 1-2.5 2.5H8l-4 3.5V6.5A2.5 2.5 0 0 1 6.5 4h11A2.5 2.5 0 0 1 20 6.5z"/>'),
  gear: svg('<circle cx="12" cy="12" r="3.1"/><path d="M12 2.6v2.6M12 18.8v2.6M2.6 12h2.6M18.8 12h2.6M5.3 5.3l1.9 1.9M16.8 16.8l1.9 1.9M18.7 5.3l-1.9 1.9M7.2 16.8l-1.9 1.9"/>'),
  check: svg('<path d="m4.5 12.5 4.6 4.6L19.5 6.8"/>'),
  pencil: svg('<path d="M12.5 20H20"/><path d="M16.6 3.6a2 2 0 0 1 2.9 2.9L7.4 18.6l-4 1 1-4z"/>'),
  plus: svg('<path d="M12 5.5v13M5.5 12h13"/>'),
  chevron: svg('<path d="m9.5 6 6 6-6 6"/>'),
  coins: svg('<ellipse cx="12" cy="6.6" rx="7.4" ry="3.1"/><path d="M4.6 6.6v4.2c0 1.7 3.3 3.1 7.4 3.1s7.4-1.4 7.4-3.1V6.6"/><path d="M4.6 10.8v4.2c0 1.7 3.3 3.1 7.4 3.1s7.4-1.4 7.4-3.1v-4.2"/>'),
  wallet: svg('<rect x="3" y="6" width="18" height="13" rx="3"/><path d="M3 9.5h18"/><path d="M16.5 14.2h2"/>'),
  users: svg('<path d="M15.4 20v-1.9a4 4 0 0 0-4-4H6.6a4 4 0 0 0-4 4V20"/><circle cx="9" cy="7.4" r="3.6"/><path d="M21.4 20v-1.9a4 4 0 0 0-3-3.9"/><path d="M16 3.9a4 4 0 0 1 0 7.4"/>'),
  package: svg('<path d="M20.4 7.8 12 3.4 3.6 7.8v8.4L12 20.6l8.4-4.4z"/><path d="m3.6 7.8 8.4 4.4 8.4-4.4"/><path d="M12 12.2v8.4"/>'),
  star: svg('<path d="m12 3.6 2.6 5.5 6 .8-4.4 4.2 1.1 5.9-5.3-3-5.3 3 1.1-5.9L3.4 9.9l6-.8z"/>'),
  rotate: svg('<path d="M3.6 12a8.4 8.4 0 1 0 2.7-6.2"/><path d="M3.6 4.2v5.1h5.1"/>'),
  heart: svg('<path d="M20.4 5.9a4.9 4.9 0 0 0-6.9 0L12 7.4l-1.5-1.5a4.9 4.9 0 1 0-6.9 6.9L12 21l8.4-8.2a4.9 4.9 0 0 0 0-6.9z"/>'),
  history: svg('<path d="M3.6 12a8.4 8.4 0 1 0 2.7-6.2"/><path d="M3.6 4.2v5.1h5.1"/><path d="M12 8.4V12l3 1.8"/>'),
  search: svg('<circle cx="11" cy="11" r="6.6"/><path d="m20 20-3.6-3.6"/>'),
  shield: svg('<path d="M12 3.4 5.2 6.2v4.9c0 4.4 2.9 7.8 6.8 9.5 3.9-1.7 6.8-5.1 6.8-9.5V6.2z"/><path d="m9.1 12 2.1 2.1 3.9-4"/>'),
  pin: svg('<path d="M12 20.6s6.6-5.2 6.6-10.6a6.6 6.6 0 1 0-13.2 0c0 5.4 6.6 10.6 6.6 10.6z"/><circle cx="12" cy="9.8" r="2.4"/>'),
  ticket: svg('<path d="M3.4 9.2V7.4a2 2 0 0 1 2-2h13.2a2 2 0 0 1 2 2v1.8a2.8 2.8 0 0 0 0 5.6v1.8a2 2 0 0 1-2 2H5.4a2 2 0 0 1-2-2v-1.8a2.8 2.8 0 0 0 0-5.6z"/><path d="M13 5.4v13.2"/>'),
  gift: svg('<rect x="3.4" y="8.4" width="17.2" height="12" rx="2"/><path d="M12 8.4v12"/><path d="M3.4 12.8h17.2"/><path d="M8.2 8.4a2.4 2.4 0 1 1 0-4.8c1.6 0 3.8 2.4 3.8 4.8"/><path d="M15.8 8.4a2.4 2.4 0 1 0 0-4.8c-1.6 0-3.8 2.4-3.8 4.8"/>'),
  cap: svg('<path d="M2.6 8.2 12 4.2l9.4 4-9.4 4z"/><path d="M6.4 10.2v4c0 1.7 2.5 3.1 5.6 3.1s5.6-1.4 5.6-3.1v-4"/>'),
  bell: svg('<path d="M17.8 8.6a5.8 5.8 0 1 0-11.6 0c0 5.6-2 6.8-2 6.8h15.6s-2-1.2-2-6.8"/><path d="M10.2 20a2 2 0 0 0 3.6 0"/>'),
  headset: svg('<path d="M4.2 14.2v-2a7.8 7.8 0 0 1 15.6 0v2"/><rect x="2.6" y="13.6" width="3.8" height="6" rx="1.9"/><rect x="17.6" y="13.6" width="3.8" height="6" rx="1.9"/>'),
  layers: svg('<path d="m12 3.4 8.4 4.2-8.4 4.2-8.4-4.2z"/><path d="m3.6 12 8.4 4.2 8.4-4.2"/><path d="m3.6 16.4 8.4 4.2 8.4-4.2"/>'),
  logout: svg('<path d="M9.4 20.6H5.6a2 2 0 0 1-2-2V5.4a2 2 0 0 1 2-2h3.8"/><path d="m16.2 16.6 4.6-4.6-4.6-4.6"/><path d="M20.8 12H9.4"/>'),
  starSolid: '<svg viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="m12 3.6 2.6 5.5 6 .8-4.4 4.2 1.1 5.9-5.3-3-5.3 3 1.1-5.9L3.4 9.9l6-.8z"/></svg>',
};

const STATUS = {
  PASS: '通过',
  PENDING: '未审核',
  REJECT: '驳回',
  OFFLINE: '下架',
  SOLD: '已售出',
};

export default {
  name: 'person',
  inheritAttrs: false,
  data() {
    return {
      ico: {
        refresh: new URL('../assets/cl/ico/refreshcw0.svg', import.meta.url).href,
      },
      ICON,
      goods: { id: 0, name: '', price: '' },
      gIsOpen: 0,
      gType: 'browse',
      logged: false,
      // 0 登录 / 1 概览 / 2 发布 / 3 管理 / 4 设置 / 5 退出 / 6~9 列表
      tab: 0,
      // 列表子页对应的 status 内部列表（1 收藏 / 2 买到 / 3 卖出 / 4 发布）
      statusTab: 1,
      wantTab: 0,
      avatarOk: true,
      person: {
        id: '',
        accountName: '',
        personImg: '',
        authority: '',
        authorityCode: 0,
        telephone: '',
        address: '',
        setting: { rank_type: '花销比', rank: 0 },
        details: { liked: 0, bought: 0, sold: 0, published: 0 },
      },
      overview: {
        amount: 0,
        trade: { pay: 0, meet: 0, receive: 0, comment: 0, refund: 0 },
      },
      published: [],
      idleTab: 'listing',
      loginPoints: [
        '一处查看收藏、订单与发布记录',
        '校园身份认证，交易更放心',
        '发布与成交全程零服务费',
      ],
    };
  },
  computed: {
    initial() {
      const n = String(this.person.accountName || '').trim();
      return n ? n.slice(0, 1).toUpperCase() : 'C';
    },
    verifyText() {
      return this.person.authorityCode === 1 ? '管理员已认证' : '学号已认证';
    },
    creditScore() {
      const r = Number(this.person.setting.rank) || 0;
      return r <= 0 ? '待累积' : String(Math.round(r * 100));
    },
    creditText() {
      const r = Number(this.person.setting.rank) || 0;
      if (r >= 0.75) return '信用极好';
      if (r >= 0.6) return '信用良好';
      if (r > 0) return '信用正常';
      return '新用户';
    },
    statCards() {
      return [
        { key: 'listed', label: '在售', value: this.idleCount('listing'), list: 9 },
        { key: 'sold', label: '已售出', value: this.idleCount('sold'), list: 8 },
        { key: 'liked', label: '收藏', value: this.person.details.liked || 0, list: 6 },
      ];
    },
    amountText() {
      return (Number(this.overview.amount) || 0).toLocaleString('zh-CN', { maximumFractionDigits: 0 });
    },
    tradeItems() {
      const t = this.overview.trade;
      return [
        { key: 'pay', label: '待付款', count: t.pay, icon: ICON.wallet },
        { key: 'meet', label: '待见面', count: t.meet, icon: ICON.users },
        { key: 'receive', label: '待收货', count: t.receive, icon: ICON.package },
        { key: 'comment', label: '待评价', count: t.comment, icon: ICON.star },
        { key: 'refund', label: '退款售后', count: t.refund, icon: ICON.rotate },
      ];
    },
    idleTabs() {
      return [
        { key: 'listing', label: '在售' },
        { key: 'sold', label: '已售' },
        { key: 'draft', label: '草稿' },
        { key: 'offline', label: '已下架' },
      ];
    },
    idleShown() {
      return this.published.filter(g => this.matchIdle(g, this.idleTab)).slice(0, 3);
    },
    funcItems() {
      const liked = this.person.details.liked || 0;
      return [
        { key: 'liked', label: '我的收藏', count: liked, icon: ICON.heart, action: () => this.openTab(6) },
        { key: 'follow', label: '我的关注', count: 0, icon: ICON.users, action: () => this.notify('关注功能即将上线') },
        { key: 'history', label: '浏览足迹', count: 0, icon: ICON.history, action: () => this.notify('浏览足迹即将上线') },
        { key: 'want', label: '我的求购', count: 0, icon: ICON.search, action: () => this.notify('求购专区即将上线') },
        { key: 'inspect', label: '验货宝', count: 0, icon: ICON.shield, action: () => this.notify('验货宝：平台代为验货，成交更有保障') },
        { key: 'pickup', label: '校内自提点', count: 0, icon: ICON.pin, action: () => this.notify('校内共 3 个自提点，可在商品详情查看') },
        { key: 'coupon', label: '优惠券', count: 0, icon: ICON.ticket, action: () => this.notify('暂无可用优惠券') },
        { key: 'invite', label: '邀请同学', count: 0, icon: ICON.gift, action: () => this.notify('邀请同学入驻，双方各得 5 元券') },
      ];
    },
    serviceItems() {
      const items = [
        {
          key: 'security',
          label: '账号与安全',
          note: this.person.telephone ? '已绑定手机' : '未绑定手机',
          icon: ICON.users,
          action: () => this.openTab(4),
        },
        {
          key: 'verify',
          label: '校园身份认证',
          note: '已认证',
          tone: 'ok',
          icon: ICON.cap,
          action: () => this.notify('已完成学号认证，可正常发布与交易'),
        },
        {
          key: 'notice',
          label: '消息通知',
          note: '已开启',
          icon: ICON.bell,
          action: () => this.openTab(10),
        },
        {
          key: 'address',
          label: '收货与自提地址',
          note: this.person.address ? '已设置地址' : '未设置',
          icon: ICON.pin,
          action: () => this.openTab(4),
        },
        {
          key: 'help',
          label: '帮助与客服',
          note: '在线',
          tone: 'warm',
          icon: ICON.headset,
          action: () => this.notify('客服工作时间 9:00 - 21:00'),
        },
        { key: 'setting', label: '设置', note: '', icon: ICON.gear, action: () => this.openTab(4) },
      ];

      if (this.person.authorityCode === 1) {
        items.push({
          key: 'manage',
          label: '管理后台',
          note: '管理员',
          tone: 'ok',
          icon: ICON.layers,
          action: () => this.openTab(3),
        });
      }

      items.push({
        key: 'logout',
        label: '退出登录',
        note: '',
        icon: ICON.logout,
        action: () => this.openTab(5),
      });

      return items;
    },
    tabTitle() {
      const map = {
        2: '发布闲置',
        3: '管理后台',
        4: '账号设置',
        5: '退出登录',
        6: '我的收藏',
        7: '我买到的',
        8: '我卖出的',
        9: '我发布的',
        10: '消息留言',
      };
      return map[this.tab] || '个人中心';
    },
    tabDesc() {
      const map = {
        2: '拍照即可发布，同校同学最快 3 小时成交',
        3: '审核商品、下架违规内容、查看分类占比',
        4: '修改昵称、头像、收货地址与登录密码',
        5: '退出后需要重新登录才能查看个人数据',
        6: '你收藏过的闲置都在这里',
        7: '待付款、待收货与已完成订单',
        8: '已经卖出的闲置与订单状态',
        9: '在售、已售、草稿与已下架的闲置',
        10: '买家与卖家给你的留言，都在这里',
      };
      return map[this.tab] || '';
    },
  },
  mounted() {
    this.wantTab = Number(this.$route && this.$route.query ? this.$route.query.tab : 0) || 0;
    // 直达列表子页（/customer?tab=6~9）时同步 status 的当前列表
    if (this.wantTab >= 6) this.statusTab = this.wantTab - 5;
    this.tokenVerify();
  },
  watch: {
    tab(newVal) {
      // 进入列表子页后，同步 status 内部当前列表（含 /customer?tab=6 直达）
      if (newVal >= 6 && newVal <= 9) {
        this.$nextTick(() => {
          const ref = this.$refs.statusRef;
          if (ref && typeof ref.switchList === 'function') ref.switchList(this.statusTab);
        });
      }
    },
    // 回调参数顺序为 (新值, 旧值)，此前写反导致刷新后停在登录页
    logged(isLogged) {
      this.tab = isLogged ? this.wantTab || 1 : 0;
    },
    // 页头导航会以 /customer?tab=x 的方式进入子页（组件被复用，mounted 不会再跑）
    '$route.query.tab'(value) {
      if (!this.logged) return;
      const t = Number(value) || 0;
      if (t >= 2 && t <= 10) {
        if (t >= 6 && t <= 9) this.statusTab = t - 5;
        this.tab = t;
      } else {
        this.tab = 1;
      }
    },
  },
  methods: {
    // ---------- 登录态 ----------
    tokenVerify() {
      // 登录态统一从 Pinia 读取，localStorage 只作为持久化介质
      const userStore = useUserStore();
      if (userStore.isLogged) {
        this.logged = true;
        this.tab = this.wantTab || 1;
        this.loadAll();
      } else {
        this.logged = false;
        this.tab = 0;
        this.$emit('showmsg', { info: '请先登录', colorType: 3 });
      }
    },
    login(res) {
      this.logged = true;
      this.tab = this.wantTab || 1;
      if (res && res.authority != undefined) {
        this.person.authorityCode = Number(res.authority) || 0;
        this.person.authority = this.person.authorityCode === 1 ? '管理用户' : '普通用户';
      }
      this.loadAll();
      // 登录后同步收藏集合，市场/详情/收藏页共用
      useCollectStore().fetchMine().catch(() => {});
    },
    exit(res) {
      if (res === 'exit') {
        this.logged = false;
        this.tab = 0;
        this.person.authorityCode = 0;
        this.person.authority = '';
        useUserStore().logout();
        useCollectStore().$reset();
        this.$emit('showmsg', { info: '已退出登录', colorType: 1 });
      }
    },

    // ---------- 数据 ----------
    loadAll() {
      this.loadUserInfo();
      this.loadPersonData();
      this.loadPublished();
      this.loadOrders();
    },
    loadUserInfo() {
      this.$http({ url: '/personal/getUserInfo', method: 'get' })
        .then(res => {
          const p = res.data || {};
          this.person.id = p.id;
          this.person.accountName = p.nickname;
          this.person.personImg = p.avatar || '';
          this.person.telephone = p.telephone || '';
          this.person.address = p.address || '';
          this.person.authorityCode = Number(p.authority) || 0;
          this.person.authority = this.person.authorityCode === 1 ? '管理用户' : '普通用户';
          this.avatarOk = true;
          // 同步到 Pinia，页面头部等位置可共用昵称/头像/权限
          useUserStore().setProfile(p);
        })
        .catch(() => {
          this.logged = false;
          this.tab = 0;
          this.person.authorityCode = 0;
          useUserStore().logout();
          this.$emit('showmsg', { info: '登录已失效，请重新登录', colorType: 3 });
        });
    },
    loadPersonData() {
      this.$http({ method: 'get', url: '/person/details', params: { userid: this.person.id } })
        .then(res => {
          const item = res.data || {};
          this.person.details.liked = Number(item.liked) || 0;
          this.person.details.bought = Number(item.bought) || 0;
          this.person.details.sold = Number(item.sold) || 0;
          this.person.details.published = Number(item.published) || 0;
          this.person.setting.rank = Number(item.rank) || 0;
        })
        .catch(() => {
          this.$emit('showmsg', { info: '个人数据加载失败', colorType: 3 });
        });
    },
    loadPublished() {
      this.$http({ method: 'get', url: '/person/published' })
        .then(res => {
          const list = Array.isArray(res.data) ? res.data : [];
          this.published = list.map(item => this.normalizeGoods(item));
          this.published.slice(0, 6).forEach(g => this.fetchPhoto(g));
        })
        .catch(() => {
          this.published = [];
        });
    },
    loadOrders() {
      const pickList = res => (Array.isArray(res.data) ? res.data : []);
      Promise.all([
        this.$http({ method: 'get', url: '/person/bought' }).then(pickList).catch(() => []),
        this.$http({ method: 'get', url: '/person/sold' }).then(pickList).catch(() => []),
      ]).then(([bought, sold]) => this.computeTrade(bought, sold));
    },
    computeTrade(bought, sold) {
      const t = { pay: 0, meet: 0, receive: 0, comment: 0, refund: 0 };
      let amount = 0;

      (bought || []).forEach(o => {
        const orderStatus = String(o.orderStatus || '');
        const logistics = String(o.logisticsStatus || '');
        if (orderStatus === '未支付') t.pay += 1;
        if (orderStatus === '已支付' && logistics === '未发货') t.meet += 1;
        if (logistics === '已发货') t.receive += 1;
        if (orderStatus === '已完成') t.comment += 1;
      });

      (sold || []).forEach(o => {
        const orderStatus = String(o.orderStatus || '');
        if (orderStatus === '已支付' || orderStatus === '已完成') {
          amount += Number(o.price) || 0;
        }
      });

      this.overview.trade = t;
      this.overview.amount = amount;
    },
    normalizeGoods(item) {
      const status = String(item.status || '');
      return {
        id: item.id,
        tradeName: item.tradeName || item.name || '未命名商品',
        price: Number(item.price) || 0,
        category: item.category || '',
        status,
        timeText: this.timeTextOf(item.time),
        pic: item.firstPhoto ? 'data:image/Jpeg;base64,' + item.firstPhoto : '',
        originPrice: Number(item.originPrice) || 0,
      };
    },
    timeTextOf(time) {
      if (!time) return '';
      const d = new Date(String(time).replace(/-/g, '/'));
      if (Number.isNaN(d.getTime())) return '';
      const pad = n => String(n).padStart(2, '0');
      return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} 发布`;
    },
    fetchPhoto(g) {
      if (!g || g.pic) return;
      this.$http({ method: 'get', url: 'commodity/getFirstPhoto', params: { commodityId: g.id } })
        .then(res => {
          const b64 = res.data;
          if (typeof b64 === 'string' && b64.length > 20) {
            g.pic = 'data:image/Jpeg;base64,' + b64;
          }
        })
        .catch(() => {});
    },
    matchIdle(g, key) {
      const s = String(g.status || '');
      if (key === 'listing') return s === STATUS.PASS || s === '';
      if (key === 'sold') return s === STATUS.SOLD;
      if (key === 'draft') return s === STATUS.PENDING || s === STATUS.REJECT;
      if (key === 'offline') return s === STATUS.OFFLINE;
      return false;
    },
    idleCount(key) {
      return this.published.filter(g => this.matchIdle(g, key)).length;
    },

    // ---------- 导航 ----------
    // 让地址栏与当前子页保持一致：页头再次点「我的收藏」等入口才能生效
    syncRoute(id) {
      const target = id >= 2 ? String(id) : '';
      const current = String((this.$route.query && this.$route.query.tab) || '');
      if (target === current) return;
      const query = { ...(this.$route.query || {}) };
      if (target) query.tab = target;
      else delete query.tab;
      this.$router.replace({ path: '/customer', query }).catch(() => {});
    },
    openTab(id) {
      if (!this.logged) {
        this.tab = 0;
        this.notify('请先登录');
        return;
      }
      if (id === 3 && this.person.authorityCode !== 1) {
        this.notify('需要管理员权限');
        return;
      }
      if (id >= 6 && id <= 9) this.statusTab = id - 5;
      this.tab = id;
      this.wantTab = 0;
      this.syncRoute(id);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    openOrders(list) {
      if (!this.logged) {
        this.notify('请先登录');
        return;
      }
      this.statusTab = list || 2;
      if (this.tab === 7) {
        const ref = this.$refs.statusRef;
        if (ref && typeof ref.switchList === 'function') ref.switchList(this.statusTab);
      } else {
        this.tab = 7;
      }
      this.syncRoute(7);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    backToOverview() {
      this.tab = 1;
      this.syncRoute(1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    notify(info) {
      this.$emit('showmsg', { info, colorType: 1 });
    },
    // 消息留言里点「查看商品」→ 独立商品详情页
    gopenById(id) {
      this.$router.push(`/goods/${id}`);
    },
    notifyMsg(msg) {
      this.$emit('showmsg', msg);
    },

    // ---------- 商品详情 ----------
    gopen(data) {
      this.goods.id = data.id;
      this.goods.name = data.name;
      this.goods.price = data.price;
      this.gIsOpen = 1;
    },
    gclose() {
      this.gIsOpen = 0;
    },
  },
  components: {
    status,
    settings,
    exit,
    login,
    publish,
    manage,
    messages,
    siteHeader,
    siteFooter,
    goodsDetail,
  },
};
</script>

<style scoped>
.person-page {
  background: var(--cl-surface);
}

.person-main {
  min-height: 62vh;
}

/* ================= 页头 ================= */
.page-head {
  background: #fff;
  border-bottom: 1px solid var(--cl-line);
}

.head-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  height: 86px;
}

.head-title {
  display: flex;
  align-items: center;
  gap: 13px;
  min-width: 0;
}

.head-mark {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--cl-brand);
  display: flex;
  align-items: center;
  justify-content: center;
}

.head-mark img {
  width: 19px;
  height: 19px;
  filter: brightness(0) invert(1);
}

.head-words h1 {
  font-size: 21px;
  font-weight: 700;
  letter-spacing: -0.5px;
  line-height: 1.2;
  color: var(--cl-ink);
}

.head-words p {
  margin-top: 3px;
  font-size: 12px;
  color: var(--cl-muted);
}

.head-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.head-btn {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  border: 1px solid var(--cl-line);
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--cl-ink-2);
  transition: border-color 0.18s, background 0.18s, color 0.18s;
}

.head-btn:hover {
  border-color: rgba(0, 166, 126, 0.45);
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
}

.head-btn :deep(svg) {
  width: 17px;
  height: 17px;
}

/* ================= 未登录 ================= */
.login-area {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  gap: 48px;
  align-items: center;
  padding-top: 56px;
  padding-bottom: 72px;
}

.login-eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1.6px;
  color: var(--cl-brand);
}

.login-intro h2 {
  margin-top: 12px;
  font-size: 34px;
  line-height: 1.2;
  font-weight: 700;
  letter-spacing: -1.2px;
  color: var(--cl-ink);
}

.login-desc {
  margin-top: 14px;
  max-width: 430px;
  font-size: 14px;
  line-height: 1.75;
  color: var(--cl-text);
}

.login-points {
  margin-top: 22px;
  display: flex;
  flex-direction: column;
  gap: 11px;
}

.login-points li {
  display: flex;
  align-items: center;
  gap: 9px;
  font-size: 13px;
  color: var(--cl-text);
}

.login-points .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--cl-brand);
}

.login-card {
  padding: 26px 28px 30px;
  border: 1px solid var(--cl-line);
  border-radius: 22px;
  background: #fff;
  box-shadow: 0 22px 60px rgba(15, 27, 22, 0.08);
}

/* ================= 概览 ================= */
.overview {
  padding-top: 20px;
  padding-bottom: 56px;
}

/* 用户卡 */
.profile-card {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 396px;
  gap: 26px;
  padding: 24px 26px 20px;
  border: 1px solid var(--cl-line);
  border-radius: 22px;
  background: #fff;
  box-shadow: 0 16px 44px rgba(15, 27, 22, 0.05);
}

.profile-main {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.avatar-box {
  position: relative;
  flex-shrink: 0;
  width: 76px;
  height: 76px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--cl-brand-soft);
  border: 1px solid rgba(0, 166, 126, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-letter {
  font-size: 28px;
  font-weight: 700;
  color: var(--cl-brand-strong);
}

.avatar-dot {
  position: absolute;
  right: 3px;
  bottom: 3px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--cl-brand);
  border: 2px solid #fff;
}

.profile-info {
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 9px;
  flex-wrap: wrap;
}

.profile-name {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.6px;
  line-height: 1.25;
  color: var(--cl-ink);
}

.verify-pill {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  height: 21px;
  padding: 0 8px;
  border: 1px solid rgba(0, 166, 126, 0.45);
  border-radius: 999px;
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
  font-size: 10.5px;
  font-weight: 600;
}

.pill-check :deep(svg) {
  width: 11px;
  height: 11px;
}

.profile-school {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 9px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.row-ico {
  display: inline-flex;
  color: var(--cl-muted-2);
}

.row-ico :deep(svg) {
  width: 14px;
  height: 14px;
}

.profile-credit {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 9px;
  font-size: 12px;
  color: var(--cl-text);
  flex-wrap: wrap;
}

.credit-star {
  display: inline-flex;
  color: #d69a2d;
}

.credit-star :deep(svg) {
  width: 13px;
  height: 13px;
}

.dot-sep {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--cl-muted-2);
}

.credit-badge {
  height: 19px;
  padding: 0 7px;
  border-radius: 5px;
  background: #fff4e0;
  color: #b4701f;
  font-size: 10.5px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
}

/* 数据卡 */
.stats-box {
  border: 1px solid var(--cl-line);
  border-radius: 16px;
  background: var(--cl-surface);
  padding: 15px 18px 13px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 6px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1px;
  padding: 2px 0;
  text-align: left;
  transition: transform 0.18s;
}

.stat:hover {
  transform: translateY(-1px);
}

.stat b {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.6px;
  line-height: 1.15;
  color: var(--cl-ink);
}

.stat span {
  font-size: 11.5px;
  color: var(--cl-muted);
}

.stat:nth-child(3) b {
  color: var(--cl-brand);
}

.stats-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: 11px;
  padding-top: 10px;
  border-top: 1px solid var(--cl-line);
  font-size: 11.5px;
  color: var(--cl-text);
}

.foot-left {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.foot-ico {
  display: inline-flex;
  color: var(--cl-muted-2);
}

.foot-ico :deep(svg) {
  width: 13px;
  height: 13px;
}

.foot-left b {
  color: var(--cl-ink);
  font-weight: 700;
}

.foot-right {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11.5px;
  font-weight: 600;
  color: #b4701f;
}

.pending-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #d69a2d;
}

/* 操作 */
.profile-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid var(--cl-line);
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  padding: 0 22px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.18s, border-color 0.18s, transform 0.18s;
}

.btn :deep(svg) {
  width: 15px;
  height: 15px;
}

.btn-ghost {
  border: 1px solid #cfdad5;
  background: #fff;
  color: var(--cl-text);
}

.btn-ghost:hover {
  border-color: rgba(0, 166, 126, 0.5);
  color: var(--cl-brand-strong);
  background: var(--cl-brand-mist);
}

.btn-brand {
  background: var(--cl-brand);
  color: #fff;
}

.btn-brand:hover {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
}

/* 两栏 */
.ov-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 356px;
  gap: 20px;
  margin-top: 20px;
  align-items: start;
}

.ov-main,
.ov-side {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

.card {
  padding: 20px 22px;
  border: 1px solid var(--cl-line);
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 12px 32px rgba(15, 27, 22, 0.04);
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.card-head h3 {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: -0.2px;
  color: var(--cl-ink);
}

.link {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: var(--cl-muted);
  transition: color 0.18s;
}

.link:hover {
  color: var(--cl-brand-strong);
}

.chev {
  display: inline-flex;
}

.chev :deep(svg) {
  width: 14px;
  height: 14px;
}

.chev.flip :deep(svg) {
  transform: rotate(180deg);
}

/* 我的交易 */
.trade-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 6px;
}

.trade-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 9px;
  padding: 13px 4px 11px;
  border-radius: 14px;
  transition: background 0.18s;
}

.trade-item:hover {
  background: var(--cl-brand-mist);
}

.trade-ico {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: var(--cl-surface);
  color: var(--cl-ink-2);
  transition: background 0.18s, color 0.18s;
}

.trade-ico :deep(svg) {
  width: 19px;
  height: 19px;
}

.trade-item:hover .trade-ico {
  background: #fff;
  color: var(--cl-brand-strong);
}

.trade-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--cl-text);
}

.trade-label em {
  font-style: normal;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: #fff4e0;
  color: #b4701f;
  font-size: 10px;
  font-weight: 600;
}

/* 我的闲置 */
.mini-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 27px;
  padding: 0 11px;
  border-radius: 999px;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 11.5px;
  font-weight: 600;
  transition: background 0.18s;
}

.mini-btn:hover {
  background: #d5f1e7;
}

.mini-btn :deep(svg) {
  width: 13px;
  height: 13px;
}

.idle-tabs {
  display: flex;
  gap: 22px;
  border-bottom: 1px solid var(--cl-line);
}

.idle-tab {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 0 0 10px;
  font-size: 13px;
  color: var(--cl-muted);
  transition: color 0.18s;
}

.idle-tab em {
  font-style: normal;
  font-size: 11px;
  color: var(--cl-muted-2);
}

.idle-tab.on {
  color: var(--cl-brand-strong);
  font-weight: 600;
}

.idle-tab.on em {
  color: var(--cl-brand);
}

.idle-tab.on::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 2px;
  border-radius: 2px;
  background: var(--cl-brand);
}

.idle-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(212px, 1fr));
  gap: 6px;
  padding-top: 10px;
}

.idle-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 14px;
  text-align: left;
  min-width: 0;
  transition: background 0.18s;
}

.idle-item:hover {
  background: var(--cl-surface);
}

.idle-pic {
  flex-shrink: 0;
  width: 54px;
  height: 54px;
  border-radius: 10px;
  overflow: hidden;
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.idle-pic img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pic-fallback {
  font-size: 16px;
  font-weight: 600;
  color: var(--cl-muted-2);
}

.idle-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.idle-name {
  font-size: 12.5px;
  line-height: 1.35;
  color: var(--cl-ink);
  overflow: hidden;
  display: -Webkit-box;
  -Webkit-line-clamp: 2;
  -Webkit-box-orient: vertical;
}

.idle-price {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.idle-price b {
  font-size: 14px;
  font-weight: 700;
  color: var(--cl-brand-strong);
}

.idle-price s {
  font-size: 10.5px;
  color: var(--cl-muted-2);
}

.idle-meta {
  font-size: 10.5px;
  color: var(--cl-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.idle-empty {
  grid-column: 1 / -1;
  padding: 26px 0;
  text-align: center;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.idle-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 100%;
  height: 40px;
  margin-top: 10px;
  border-radius: 12px;
  background: var(--cl-surface);
  color: var(--cl-text);
  font-size: 12.5px;
  transition: background 0.18s, color 0.18s;
}

.idle-more:hover {
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
}

/* 常用功能 */
.func-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px 4px;
}

.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 7px;
  padding: 10px 2px;
  border-radius: 12px;
  transition: background 0.18s;
}

.func-item:hover {
  background: var(--cl-surface);
}

.func-ico {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--cl-brand-mist);
  color: var(--cl-brand-strong);
}

.func-ico :deep(svg) {
  width: 18px;
  height: 18px;
}

.func-label {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  color: var(--cl-text);
  white-space: nowrap;
}

.func-label em {
  font-style: normal;
  font-size: 9.5px;
  color: var(--cl-muted);
}

/* 设置与服务 */
.service-list {
  display: flex;
  flex-direction: column;
}

.service-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 2px;
  border-bottom: 1px solid #f1f4f2;
  text-align: left;
}

.service-row:last-child {
  border-bottom: none;
}

.service-ico {
  display: inline-flex;
  flex-shrink: 0;
  color: var(--cl-muted);
}

.service-ico :deep(svg) {
  width: 17px;
  height: 17px;
}

.service-label {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  color: var(--cl-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.service-note {
  font-size: 11.5px;
  color: var(--cl-muted);
  flex-shrink: 0;
}

.service-note.ok {
  color: var(--cl-brand);
}

.service-note.warm {
  padding: 2px 7px;
  border-radius: 5px;
  background: #fff4e0;
  color: #b4701f;
  font-weight: 600;
}

.service-arrow {
  display: inline-flex;
  flex-shrink: 0;
  color: #c4cec9;
}

.service-arrow :deep(svg) {
  width: 14px;
  height: 14px;
}

/* 品牌说明 */
.brand-note {
  margin-top: 22px;
  text-align: center;
  font-size: 11.5px;
  letter-spacing: 0.2px;
  color: var(--cl-muted);
}

.brand-note i {
  font-style: normal;
  font-size: 10px;
  letter-spacing: 0.8px;
}

/* ================= 子页面 ================= */
.sub-page {
  padding-top: 22px;
  padding-bottom: 56px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12.5px;
  color: var(--cl-muted);
  transition: color 0.18s;
}

.back-btn:hover {
  color: var(--cl-brand-strong);
}

.sub-head h2 {
  margin-top: 12px;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.7px;
  color: var(--cl-ink);
}

.sub-head p {
  margin-top: 6px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.sub-body {
  margin-top: 18px;
  padding: 24px;
  border: 1px solid var(--cl-line);
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 12px 32px rgba(15, 27, 22, 0.04);
}

.sub-body.legacy {
  padding: 20px 22px 24px;
}

/* ================= 响应式 ================= */
@media (max-width: 1180px) {
  .profile-card {
    grid-template-columns: minmax(0, 1fr) 340px;
  }
  .ov-grid {
    grid-template-columns: minmax(0, 1fr) 320px;
  }
  .idle-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .login-area {
    grid-template-columns: 1fr;
    gap: 28px;
    padding-top: 36px;
  }
  .profile-card {
    grid-template-columns: 1fr;
    gap: 18px;
  }
  .ov-grid {
    grid-template-columns: 1fr;
  }
  .idle-list {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
  .func-grid {
    grid-template-columns: repeat(8, minmax(0, 1fr));
  }
}

@media (max-width: 820px) {
  .head-inner {
    height: 74px;
  }
  .head-words h1 {
    font-size: 19px;
  }
  .profile-card {
    padding: 18px 16px 16px;
    border-radius: 18px;
  }
  .avatar-box {
    width: 62px;
    height: 62px;
  }
  .profile-name {
    font-size: 19px;
  }
  .stats-box {
    padding: 13px 14px 11px;
  }
  .stat b {
    font-size: 21px;
  }
  .profile-actions {
    padding-top: 12px;
  }
  .btn {
    flex: 1;
    height: 38px;
    padding: 0 12px;
  }
  .card {
    padding: 16px 14px;
    border-radius: 16px;
  }
  .trade-grid {
    gap: 2px;
  }
  .trade-ico {
    width: 38px;
    height: 38px;
    border-radius: 11px;
  }
  .trade-label,
  .func-label {
    font-size: 10.5px;
  }
  .idle-list {
    grid-template-columns: 1fr;
    gap: 2px;
  }
  .idle-tabs {
    gap: 16px;
  }
  .idle-tab {
    font-size: 12px;
  }
  .func-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 4px;
  }
  .service-label {
    font-size: 12.5px;
  }
  .sub-body {
    padding: 16px 14px;
    border-radius: 16px;
  }
  .sub-body.legacy {
    padding: 14px 12px 18px;
  }
  .sub-head h2 {
    font-size: 20px;
  }
}

@media (max-width: 560px) {
  .head-words p {
    display: none;
  }
  .stats-row {
    gap: 2px;
  }
  .stats-foot {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  .login-intro h2 {
    font-size: 26px;
  }
  .login-card {
    padding: 20px 18px 22px;
  }
}
</style>
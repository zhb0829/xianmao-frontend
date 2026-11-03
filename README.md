# 闲猫 (Xianmao) 校园二手交易平台

<div align="center">

![Version](https://img.shields.io/badge/version-2.0-blue)
![Vue](https://img.shields.io/badge/Vue-3.2-4FC08D)
![Vite](https://img.shields.io/badge/Vite-4-646CFF)
![Pinia](https://img.shields.io/badge/Pinia-2-FFD859)
![Backend](https://img.shields.io/badge/backend-none%20(built--in%20Mock)-success)

**校园二手交易平台** — 纯前端 + 浏览器内置 Mock，无需任何后端即可完整演示

</div>

## 项目简介

Web课设大作业，闲猫是一个面向大学生的**校园二手交易平台**，支持商品发布、审核、浏览、购买、收藏、留言、评价、订单管理、商品编辑与上下架等完整交易流程。

本项目是**纯前端项目**：Vue 3 + Vite + Pinia + Vue Router + Axios，所有接口由 `src/mock`（浏览器内置 Mock）提供，共 **47 个端点**；演示图片放在 `public/demo`。**不依赖 Spring Boot、MySQL 或任何服务器**，`npm run dev` 与 `npm run build` 的产物都能直接演示全部功能。

前端共 **13 个独立路由页面**，覆盖任务书选题 8 要求的 7 个页面，并补充了商品市场、卖家主页、订单详情与评价、搜索页、分类管理等扩展页面。

## 项目亮点

前端设计为本人大学初次纯手工完成全套 UI 设计，美观度及动画方面还是有一定可取之处的；数据层用 Mock 完整模拟了发布 → 审核 → 上架 → 下单 → 支付 → 收货 → 评价的全流程，答辩时无需准备数据库。

## 快速启动

### 前置要求

- Node.js 16+（npm 或 pnpm 均可）
- 不需要 JDK / Maven / MySQL

### 开发模式

```bash
cd frontend
npm install
npm run dev
# 浏览器打开 http://localhost:5173
# 首页为项目欢迎页，点击「进入详情」进入网站首页 /welcome
```

### 构建产物演示

```bash
cd frontend
npm run build
npm run preview
# 打开终端里提示的地址（默认 http://localhost:4173）
```

构建产物同样内置 Mock，行为与开发模式一致，可直接在教室电脑上 `preview` 演示。

### 演示账号

| 账号 | 密码 | 身份 | 多出的能力 |
|---|---|---|---|
| `demo` | `123456` | 普通用户 | 发布/编辑/上下架自己的商品、收藏、订单、留言、评价 |
| `admin` | `123456` | 管理员 | 管理后台：审核、下架、分类占比饼图、分类管理 |

也支持自行注册（邮箱 + 昵称 + 密码，验证码任意 6 位）。

## 交付物

| 文件 | 说明 |
|------|------|
| `docs/课程设计报告.docx` | 课程设计报告：选题背景、需求、架构、关键实现、数据层、测试、分工 |
| `docs/答辩PPT.pptx` | 答辩演示 PPT（12 页，16:9） |
| `docs/项目评审表.docx` | 评分表、模块演示与分工确认、答辩问答记录、评审意见 |
| `docs/接口文档.md` | 47 个接口的方法、参数、返回示例与数据层说明 |

## 功能介绍

### 用户系统
- **登录注册页** `/login`：独立路由页，邮箱 + 昵称 + 密码注册、6 位验证码、登录错误提示
- **状态管理**：登录态存在 Pinia `userStore`，刷新页面保持登录，路由守卫拦截未登录访问
- **权限分离**：普通用户与管理员使用相同登录入口，根据 `authority` 展示「管理后台」
- **个人信息**：编辑昵称、头像、收货地址、手机号

### 商品浏览
- **首页** `/welcome`：分类导航、搜索框、推荐商品（`/commodity/getCommodity` 数据）、价格筛选与排序
- **商品市场** `/shop`：关键词 + 分类筛选、价格/时间排序、分页
- **搜索页** `/search`：独立搜索页，热门搜索词、本地搜索历史、结果分页与空状态
- **商品详情页** `/goods/:id`：独立路由，图集轮播、商品介绍、卖家信息、收藏、下单支付、留言板、买家评价
- **卖家主页** `/seller/:id`：卖家信用与在售/已售统计、TA 的在售闲置（详情页卖家卡片进入）
- **收藏商品**：收藏状态由 Pinia `collectStore` 统一维护，市场 / 详情 / 个人中心实时一致

### 交易流程
1. 买家浏览商品，点击卡片进入详情页
2. 点击「购买」生成订单，填写/使用收货地址
3. 支付订单 → 卖家发货 → 买家确认收货
4. **订单详情页** `/order/:id`：状态时间线、支付/发货/收货操作、买家评价（5 星 + 文字）
5. 「我买到的 / 我卖出的」分别查看订单状态

### 商品发布与我的商品
- **发布页** `/publish`：独立路由，三步表单（信息 → 图片 → 确认），封面与最多 6 张轮播图
- **编辑商品**：`/publish?edit=id` 回填原信息，保存后重新进入审核队列
- **我的商品**：`/customer?tab=9`，支持编辑、下架、重新上架
- 发布后进入管理员审核流程，审核通过后在商品市场展示

### 留言与消息
- **商品留言板**：详情页内留言，登录后可提交，按时间倒序展示
- **消息留言页** `/customer?tab=10`：分「我收到的」「我发出的」两个列表，可跳转对应商品

### 管理后台（管理员）
- 审核待发布商品（通过 / 驳回）
- 下架违规商品
- 查看商品分类占比饼图（ECharts）
- **分类管理** `/admin/category`：分类增删改，带商品数统计；有商品的分类禁止删除，改名同步到已有商品

## 页面与路由（13 个）

| # | 路由 | 页面 |
|---|---|---|
| 1 | `/` | 站点欢迎页（intro） |
| 2 | `/welcome` | 首页 |
| 3 | `/shop` | 商品市场 |
| 4 | `/goods/:id` | 商品详情 |
| 5 | `/seller/:id` | 卖家主页 |
| 6 | `/order/:id` | 订单详情 + 评价 |
| 7 | `/search` | 搜索页 |
| 8 | `/login` | 登录注册 |
| 9 | `/publish` | 发布 / 编辑 |
| 10 | `/customer` | 个人中心（tab 6~10：收藏、订单、我的商品、消息、设置） |
| 11 | `/admin/category` | 分类管理（管理员） |
| 12 | `/want` | 求购专区（扩展） |
| 13 | `/circle` | 校园圈（扩展） |

## 技术栈

| 技术 | 用途 |
|------|------|
| Vue 3 | 前端框架（页面 Options API + 站点页头等组件 `<script setup>`） |
| Vite 4 | 开发与构建；构建产物同样内置 Mock |
| Vue Router 4 | 13 条路由、动态 import、全局登录守卫 |
| Pinia 2 | 状态管理：`userStore`（登录态/权限）、`collectStore`（收藏集合） |
| Axios | HTTP 请求；`src/api/request.js` 统一封装并把请求交给内置 Mock |
| 浏览器内置 Mock | `src/mock`：47 个接口、内存状态、演示图片转 base64 |
| ECharts 5.3 | 数据可视化（管理端分类占比饼图） |
| Animate.css | 过渡动画 |
| 自研设计系统（styles/cl.css） | 统一色板、排版与组件样式 |

## 项目结构

```
校园二手物品交易平台/
├── frontend/                         # 纯前端工程
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   ├── public/
│   │   └── demo/                     # 演示图片（19 张商品图 + 头像），随构建产物发布
│   └── src/
│       ├── main.js                   # 应用入口（Pinia / Router / 请求封装 / 调试钩子）
│       ├── App.vue                   # 根组件（全局提示、加载、导航）
│       ├── api/request.js            # axios 统一封装 + 内置 Mock adapter
│       ├── api/localStore.js         # 求购 / 校园圈本地数据层
│       ├── mock/                     # 浏览器内置 Mock
│       │   ├── index.js              # 请求分发：解析路径、参数、FormData、错误
│       │   ├── routes.js             # 47 个接口与内存状态
│       │   ├── data.js               # 演示种子数据
│       │   └── images.js             # public/demo 图片转 base64（带缓存）
│       ├── stores/                   # Pinia：user.js、collect.js
│       ├── router/index.js           # 13 条路由 + 登录守卫
│       ├── pages/                    # 13 个页面
│       ├── components/               # 19 个可复用组件
│       └── styles/cl.css             # 设计系统
│
├── docs/                             # 交付物：报告、PPT、评审表、接口文档
└── README.md
```

## 数据层：浏览器内置 Mock

- 请求由 `src/api/request.js` 的自定义 adapter 接管，不发出真实网络请求；`src/mock/index.js` 按路径匹配 47 个接口。
- 演示图片位于 `public/demo`，Mock 启动时通过 `images.js` 读成 base64（带缓存），因此详情页、发布页的图片逻辑与真实接口一致。
- 数据保存在**浏览器内存**：同一页面内切换账号（登录/退出）数据共享；**刷新页面会重置为初始数据**。
- 控制台调试：`window.__xmHttp({ method: 'get', url: '/commodity/getCommodity' })`，或 `window.__xmRouter.push('/welcome')`。
- 接口返回格式与真实后端一致（直接返回业务数据本身，不包 `{code,msg,data}`），后续若接真实后端，只需把 adapter 换成 `axios` 默认 adaptor 并配置 baseURL。

## 接口

前端统一通过 `/api` 前缀访问接口，全部由内置 Mock 提供，共 47 个端点；完整参数与返回示例见 [`docs/接口文档.md`](docs/接口文档.md)。

| 前缀 | 端点数 | 说明 |
|------|------|------|
| `/user` | 3 | 登录、注册、验证码 |
| `/commodity` | 10 | 列表查询、首图、收藏数、详情、发布、轮播图、编辑、下架、上架 |
| `/category` | 5 | 分类列表、分类管理（新增/改名/删除，带商品数） |
| `/seller` | 2 | 卖家信息、卖家在售商品 |
| `/order` | 1 | 订单详情（买卖双方与状态） |
| `/review` | 2 | 买家评价提交、按商品查询评价与均分 |
| `/search` | 1 | 热门搜索词 |
| `/collect` | 2 | 收藏增删 |
| `/shop` | 4 | 收藏切换、下单、支付、发货 |
| `/person` | 8 | 统计、我收藏/买到/卖出/发布、收货、删除订单 |
| `/personal` | 2 | 个人资料读取与保存 |
| `/comment` | 3 | 商品留言查询、新增、我收到/发出的留言 |
| `/manage` | 5 | 管理端审核列表、分类占比、通过、驳回、下架 |

## 常见问题

- **刷新后数据变了？** Mock 状态在浏览器内存里，刷新会回到初始数据；答辩时在同一个页面内完成演示即可。
- **图片不显示？** 确认 `public/demo` 存在；构建产物中对应 `dist/demo`。
- **换浏览器/清缓存后登录还在吗？** 登录态存在 `localStorage`，Mock 数据不持久化。
- **能否接真实后端？** 可以：把 `src/api/request.js` 里的自定义 adapter 去掉、恢复 `baseURL` 即可，页面无需改动。

## 团队

章恒彬 · 叶信强 · 王宇 · 凌陪钦（2471 班 · 第 1 组）
# 闲猫 (Xianmao) 校园二手交易平台

<div align="center">

![Version](https://img.shields.io/badge/version-1.2-blue)
![Vue](https://img.shields.io/badge/Vue-3.2-4FC08D)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-6DB33F)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1)

**校园二手交易平台** — 小组开发

</div>

## 项目简介

Web课设大作业，闲猫是一个面向大学生的**校园二手交易平台**，支持商品发布、审核、浏览、购买、收藏、留言、订单管理、商品编辑与上下架等完整交易流程。前端使用 Vue 3 + Vite + Pinia + Vue Router + Axios（Mock 演示模式），后端使用 Spring Boot + MyBatis-Plus，数据库采用 MySQL。前端共 **9 个独立路由页面**，覆盖任务书选题 8 要求的 7 个页面。

## 项目亮点

前端设计为本人大学初次纯手工完成全套UI设计，美观度及动画方面还是有一定可取之处的

## 快速启动

### 前置要求

- Node.js 16+
- JDK 8+
- Maven 3+
- MySQL 8.0

### 后端

```bash
cd backend

# 1. 创建数据库（配置中为 mydb）
mysql -u root -p -e "CREATE DATABASE mydb DEFAULT CHARACTER SET utf8mb4;"

# 2. 导入建表脚本
mysql -u root -p mydb < sql/transaction_sql.sql

# 3. 修改配置（可选）
# 编辑 src/main/resources/application-dev.yml（开发环境）或 application-pro.yml（生产环境），修改数据库账号密码

# 4. 启动
mvn spring-boot:run
# 后端运行在 http://localhost:9080
# Swagger 文档: http://localhost:9080/swagger-ui/
```

### 前端

```bash
cd frontend

npm install
npm run dev
# 前端运行在 http://localhost:5173
```

### 快速体验（不启动后端）

```bash
cd frontend
npm run dev
# 浏览器打开 http://localhost:5173
# 首页为项目欢迎页（intro），点击「进入详情」或按 Enter 进入网站首页 /welcome
```

前端默认以**演示模式**启动：Mock 中间件接管全部接口，无需 Spring Boot / MySQL 即可走通「发布 → 审核 → 上架 → 下单 → 支付 → 收货 → 收藏 → 设置」完整流程。

- 普通用户：`demo / 123456`
- 管理员：`admin / 123456`（多出「管理」入口：审核、下架、分类占比饼图）
- 演示模式只在 `npm run dev` 下生效，`npm run build` 产物不含 Mock；详细说明见 `frontend/README-DEMO.md`。

## 交付物

| 文件 | 说明 |
|------|------|
| `docs/课程设计报告.docx` | 课程设计报告：选题背景、需求、架构、关键实现、数据库、测试、分工 |
| `docs/答辩PPT.pptx` | 答辩演示 PPT（12 页，16:9） |
| `docs/项目评审表.docx` | 评分表、模块演示与分工确认、答辩问答记录、评审意见 |
| `docs/接口文档.md` | 47 个接口的方法、参数、返回示例与联调说明 |

## 功能介绍

### 用户系统
- **登录注册页** `/login`：独立路由页，邮箱 + 昵称 + 密码注册、6 位验证码、登录错误提示
- **状态管理**：登录态存在 Pinia `userStore`，刷新页面保持登录，路由守卫拦截未登录访问
- **权限分离**：普通用户与管理员使用相同登录入口，根据 `authority` 展示「管理后台」
- **个人信息**：编辑昵称、头像、收货地址、手机号

### 商品浏览
- **首页** `/welcome`：分类导航、搜索框、推荐商品（`/commodity/getCommodity` 接口数据）、价格筛选与排序
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
- 按状态、分类、时间筛选与搜索、分页
- **分类管理** `/admin/category`：分类增删改，带商品数统计；有商品的分类禁止删除，改名同步到已有商品

## 技术栈

### 前端 (frontend/)

| 技术 | 用途 |
|------|------|
| Vue 3 | 前端框架（页面 Options API + 站点页头等组件 `<script setup>`） |
| Vite 4 | 构建工具与热更新；Mock 以插件形式接入 dev server |
| Vue Router 4 | 客户端路由：13 条路由、动态 import、全局登录守卫 |
| Pinia 2 | 状态管理：`userStore`（登录态/权限）、`collectStore`（收藏集合） |
| Axios | HTTP 请求，`src/api/request.js` 统一封装（token 注入、错误文案） |
| ECharts 5.3 | 数据可视化（管理端分类占比饼图） |
| Animate.css | 过渡动画 |
| 自研设计系统（styles/cl.css）+ Element Plus | 页面以自研组件与设计规范为主，Element Plus 作为组件基础依赖保留 |

### 后端 (backend/)

| 技术 | 用途 |
|------|------|
| Spring Boot 2.7.5 | 后端框架 |
| MyBatis + MyBatis-Plus 3.5.1 | ORM 与数据访问 |
| MySQL 8.0 | 关系型数据库 |
| JWT (java-jwt 3.10) | Token 认证 |
| Swagger 3.0 (springfox) | API 文档 |
| Spring Mail | 邮箱验证码 |
| Hutool 5.7 | Java 工具库 |
| Lombok | 代码简化 |
| Joda-Time | 日期时间处理 |

## 项目结构

```
xianmao/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml                       # Maven 依赖
│   ├── sql/transaction_sql.sql       # 数据库建表脚本 + 测试数据
│   └── src/main/java/cn/edu/gxu/
│       ├── ProjectApplication.java   # 启动类
│       ├── Basic/                    # 常量 + 统一返回结果
│       ├── configure/                # CORS、拦截器、Swagger、MyBatis-Plus 配置
│       ├── controller/               # 控制器（9个）+ DTO（16个）
│       ├── dao/                      # MyBatis Mapper 接口（9个）
│       ├── model/                    # 实体类（8个）
│       ├── service/                  # 服务接口 + 实现（13个）
│       └── resources/
│           ├── application.yml       # 主配置文件
│           ├── application-dev.yml   # 开发环境配置
│           ├── application-pro.yml   # 生产环境配置
│           └── mapper/               # MyBatis XML 映射文件（9个）
│
├── frontend/                         # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js                # Mock 开关 / 真实后端代理
│   ├── index.html
│   ├── mock/                         # 演示 Mock（47 个 /api 端点）
│   │   ├── index.js                  # Vite 插件：注册 /api 中间件、托管演示图
│   │   ├── routes.js                 # 端点路由表与内存状态
│   │   └── data.js                   # 演示种子数据
│   └── src/
│       ├── main.js                   # 应用入口（Pinia / Router / 请求封装）
│       ├── App.vue                   # 根组件（全局提示、加载、导航）
│       ├── api/request.js            # axios 统一封装
│       ├── api/localStore.js         # 求购 / 校园圈本地数据层
│       ├── stores/                   # Pinia：user.js、collect.js
│       ├── router/index.js           # 13 条路由 + 登录守卫
│       ├── pages/                    # 13 个页面：intro、welcome、shop、login、publish、goods、seller、order、search、adminCategory、person、want、circle
│       ├── components/               # 19 个可复用组件
│       └── styles/cl.css             # 设计系统
│
└── docs/                             # 交付物：报告、PPT、评审表、接口文档
│
└── README.md
```

## 数据库设计

数据库 **11 张表**：

| 表名 | 说明 |
|------|------|
| `userlog` | 用户账号（account, password-MD5, authority, email） |
| `userinfo` | 用户资料（nickname, avatar） |
| `personinfo` | 收货信息（telephone, name, address） |
| `category` | 商品分类（16种：数码产品、食品、图书、服饰等） |
| `commodityinfo` | 商品信息（名称、价格、描述、分类、状态、卖家） |
| `images` | 商品图片 |
| `collect` | 用户收藏 |
| `order` | 订单（买家、卖家、价格、物流状态） |
| `audit` | 商品审核记录 |
| `comment` | 用户评论 |
| `sellcommodity` | 卖家商品关联 |

## API 路由

前端统一通过 `/api` 前缀访问接口，演示模式由 `mock/` 中间件接管，共 47 个端点；完整参数与返回示例见 [`docs/接口文档.md`](docs/接口文档.md)。

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

# 校园二手交易平台 2.0

### 项目简介

本项目是「闲猫二手交易平台」的**纯前端**版本，由 4 人小组开发。UI 全部手工设计，页面切换与元素入场均有过渡动画，交互完整。

项目已移除所有后端依赖（Spring Boot / MySQL 均已删除），接口由浏览器内置 Mock（`src/mock`）提供，共 47 个端点；`npm run dev` 与 `npm run build` 的产物都能直接演示全部功能。

本交易平台支持：

- **邮箱注册**：邮箱 + 昵称 + 密码，6 位验证码（演示环境不校验）
- **权限分离**：普通用户与管理员同一入口登录，按 `authority` 展示管理后台
- **商品审核**：管理员审核普通用户发布的商品，通过/驳回/下架
- **交易闭环**：发布 → 审核 → 上架 → 下单 → 支付 → 发货 → 收货 → 评价
- **收藏 / 留言 / 消息 / 分类管理 / 搜索 / 卖家主页**

### 如何运行

1. 安装 Node.js（16+）：https://nodejs.org/en
2. 在 `frontend` 目录执行：

```bash
npm install
npm run dev
```

看到终端提示后打开对应地址（默认 http://localhost:5173）。

演示账号：

- 普通用户 `demo / 123456`
- 管理员 `admin / 123456`（多出审核、下架、分类管理入口）

构建产物同样可演示：

```bash
npm run build
npm run preview
```

### 说明

- 数据层：`src/mock`（浏览器内置 Mock + 内存状态），演示图片在 `public/demo`。
- 刷新页面会重置演示数据；同一页面内切换账号数据共享。
- 详细演示流程与覆盖范围见 [README-DEMO.md](./README-DEMO.md)。
- 控制台调试：`window.__xmHttp({ method: 'get', url: '/commodity/getCommodity' })`。

### 项目截图

（答辩 PPT 与课程设计报告中包含页面截图与流程说明，见仓库根目录 `docs/`）
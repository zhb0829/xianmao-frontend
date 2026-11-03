// 演示数据：结构与前端页面实际消费的字段一一对应
// 注意：后端真实返回不是 {code,msg,data} 包装，前端直接消费 res.data 本身
//
// 统一状态口径（同时被商品市场、个人中心、管理端按钮判断使用）：
//   '通过'   —— 已审核上架，出现在商品市场
//   '未审核' —— 发布后待审核，只在管理端和"我发布的"里可见
//   '驳回'   —— 审核未通过，可在管理端再次审核
//   '下架'   —— 管理员下架，不在市场展示
//   '已售出' —— 已成交，保留记录不在市场展示
export const GOODS_STATUS = {
  PASS: '通过',
  PENDING: '未审核',
  REJECT: '驳回',
  OFFLINE: '下架',
  SOLD: '已售出',
}

// 发布页下拉 / 分类筛选使用的分类
export const categories = [
  '电子产品', '生活用品', '学习用品', '美食', '图书文具', '服饰鞋包',
  '运动户外', '乐器', '美妆护肤', '母婴', '宠物用品', '家居家装',
  '数码配件', '办公用品', '玩具手办', '其他',
]

// 演示账号：account 为登录名。演示账号不是邮箱，也可以直接用邮箱注册新账号登录
export const accounts = {
  demo: { account: 'demo', password: '123456', id: 1001, nickname: '演示同学', authority: 0 },
  admin: { account: 'admin', password: '123456', id: 1002, nickname: '管理员', authority: 1 },
}

// 商品主数据。img 为 mock/assets/demo/goods 下的序号（1~19），
// 每个序号对应的图片内容必须与该商品一致，取自淘宝商品主图
// sellerId 1001 对应演示账号 demo，因此"我发布的"里能看到 7~12 号商品
export const seedGoods = [
  { id: 1,  tradeName: 'iPhone 17 Pro 99新',     price: 6499, category: '电子产品', sellerId: 2001, sellerName: '张同学', status: '通过',   time: '2026-10-28 09:12', shippingAddress: '广西大学东校园 3 栋',  description: '自用一年，无磕碰，电池健康度 92%，带原装充电线，可当面试机。', img: 1 },
  { id: 2,  tradeName: '机械键盘 87 键',         price: 199,  category: '电子产品', sellerId: 2002, sellerName: '李同学', status: '通过',   time: '2026-10-27 19:40', shippingAddress: '广西大学西校园 12 栋', description: '茶轴手感，带 RGB 灯效，键帽无打油，附赠拔键器。', img: 2 },
  { id: 3,  tradeName: '小米蓝牙音箱',           price: 89,   category: '电子产品', sellerId: 2003, sellerName: '王同学', status: '通过',   time: '2026-10-29 08:05', shippingAddress: '广西大学东校园 7 栋',  description: '音质饱满，续航 8 小时左右，支持小爱同学语音控制。', img: 3 },
  { id: 4,  tradeName: '宿舍护眼台灯',           price: 35,   category: '生活用品', sellerId: 2004, sellerName: '陈同学', status: '通过',   time: '2026-10-26 21:30', shippingAddress: '广西大学西校园 5 栋',  description: '三档色温，无频闪，备考期间一直在用，功能完好。', img: 4 },
  { id: 5,  tradeName: '保温杯 500ml',           price: 25,   category: '生活用品', sellerId: 2005, sellerName: '赵同学', status: '通过',   time: '2026-10-25 14:22', shippingAddress: '广西大学东校园 9 栋',  description: '316 不锈钢内胆，保温 12 小时，只用了两周。', img: 5 },
  { id: 6,  tradeName: '宿舍收纳箱（大号）',     price: 18,   category: '生活用品', sellerId: 2006, sellerName: '周同学', status: '未审核', time: '2026-10-29 10:18', shippingAddress: '广西大学西校园 8 栋',  description: '带滑轮的塑料收纳箱，装过书籍和衣物，清洗干净。', img: 6 },
  { id: 7,  tradeName: '高等数学 同济第七版',    price: 15,   category: '学习用品', sellerId: 1001, sellerName: '演示同学', status: '已售出', time: '2026-10-24 16:05', shippingAddress: '广西大学东校园 2 栋',  description: '上册+下册，笔记完整，重点已用荧光笔标注。', img: 7 },
  { id: 8,  tradeName: '考研英语真题 2010-2025', price: 20,   category: '学习用品', sellerId: 1001, sellerName: '演示同学', status: '已售出', time: '2026-10-23 11:47', shippingAddress: '广西大学西校园 11 栋', description: '黄皮书正版，做过一部分，答案册齐全，无撕页。', img: 8 },
  { id: 9,  tradeName: '笔记本套装 5 本',        price: 12,   category: '学习用品', sellerId: 1001, sellerName: '演示同学', status: '未审核', time: '2026-10-29 09:02', shippingAddress: '广西大学东校园 4 栋',  description: 'A5 尺寸，方格内页，全新未拆封，买多了转卖。', img: 9 },
  { id: 10, tradeName: '零食大礼包',             price: 39,   category: '美食',     sellerId: 1001, sellerName: '演示同学', status: '通过',   time: '2026-10-22 20:11', shippingAddress: '广西大学西校园 6 栋',  description: '含薯片、饼干、坚果等 12 袋，保质期到年底。', img: 10 },
  { id: 11, tradeName: '螺蛳粉 3 袋装',          price: 16,   category: '美食',     sellerId: 1001, sellerName: '演示同学', status: '下架',   time: '2026-10-21 18:30', shippingAddress: '广西大学东校园 1 栋',  description: '广西本地品牌，酸辣开胃，宿舍煮 10 分钟即可。', img: 11 },
  { id: 12, tradeName: '手工戚风蛋糕',           price: 28,   category: '美食',     sellerId: 1001, sellerName: '演示同学', status: '通过',   time: '2026-10-29 07:20', shippingAddress: '广西大学西校园 10 栋', description: '当天现做，低糖配方，可自提，需提前一天预定。', img: 12 },
  { id: 13, tradeName: '山地自行车 21 速',       price: 380,  category: '运动户外', sellerId: 2007, sellerName: '孙同学', status: '通过',   time: '2026-10-24 12:06', shippingAddress: '广西大学东校园 5 栋',  description: '骑了一年，变速顺畅，前后刹车刚换过刹车皮，带车锁。', img: 13 },
  { id: 14, tradeName: '宿舍小冰箱 42L',         price: 260,  category: '生活用品', sellerId: 2006, sellerName: '周同学', status: '通过',   time: '2026-10-25 09:35', shippingAddress: '广西大学西校园 8 栋',  description: '冷藏冷冻双温区，宿舍限电也能用，毕业清仓。', img: 14 },
  { id: 15, tradeName: '羽毛球拍 碳素双拍',      price: 45,   category: '运动户外', sellerId: 2008, sellerName: '郑同学', status: '通过',   time: '2026-10-26 17:48', shippingAddress: '广西大学东校园 6 栋',  description: '两支装，含拍套和三只球，拍线张力正常。', img: 15 },
  // 首页分类入口需要每个类目都有在售商品，否则点进去是空列表
  { id: 16, tradeName: '考研数学复习全书', price: 45,   category: '学习用品', sellerId: 2009, sellerName: '黄同学', status: '通过',   time: '2026-10-31 10:24', shippingAddress: '广西大学图书馆 2 层',   description: '基础篇 + 强化篇，只做过前两章，笔记可撕下，不影响使用。', img: 16 },
  { id: 17, tradeName: '《平凡的世界》全三册',   price: 32,   category: '图书文具', sellerId: 2010, sellerName: '罗同学', status: '通过',   time: '2026-10-30 20:15', shippingAddress: '广西大学西校园 3 栋',   description: '人民文学出版社版本，无缺页无涂画，书角有轻微磨损。', img: 17 },
  { id: 18, tradeName: '优衣库摇粒绒外套 L 码',  price: 69,   category: '服饰鞋包', sellerId: 2011, sellerName: '梁同学', status: '通过',   time: '2026-10-31 08:42', shippingAddress: '广西大学东校园 11 栋',  description: '米白色，穿过三四次，已清洗，无起球无污渍。', img: 18 },
  { id: 19, tradeName: '尤克里里 23 寸 单板',    price: 128,  category: '乐器',     sellerId: 2012, sellerName: '韦同学', status: '通过',   time: '2026-10-29 19:03', shippingAddress: '广西大学艺术楼 1 层',  description: '桃花芯单板，音准正常，附赠琴包、背带和一副备用弦。', img: 19 },
]

// 订单状态口径：orderStatus 取 '未支付' / '已支付' / '已完成'
// logisticsStatus 取 '未发货' / '已发货' / '已签收'，前端按钮严格按这两组值判断
export const seedBought = [
  {
    id: 9001, commodityId: 3, name: '小米蓝牙音箱', price: 89, seller: '王同学',
    orderStatus: '未支付', logisticsStatus: '未发货', submitTime: '2026-10-20 10:12', buyerId: 1001,
  },
  {
    id: 9002, commodityId: 5, name: '保温杯 500ml', price: 25, seller: '赵同学',
    orderStatus: '已支付', logisticsStatus: '已发货', submitTime: '2026-10-18 15:36', buyerId: 1001,
  },
]

export const seedSold = [
  {
    id: 9101, commodityId: 7, name: '高等数学 同济第七版', price: 15, seller: '演示同学',
    orderStatus: '已支付', logisticsStatus: '未发货', submitTime: '2026-10-17 09:24', sellerId: 1001,
  },
  {
    id: 9102, commodityId: 8, name: '考研英语真题 2010-2025', price: 20, seller: '演示同学',
    orderStatus: '已完成', logisticsStatus: '已签收', submitTime: '2026-10-28 21:15', sellerId: 1001,
  },
]

// 个人资料默认值（保存设置后会进入 state.profiles）
export const defaultProfile = {
  nickname: '演示同学',
  address: '广西大学东校园 7 栋',
  telephone: '13800008888',
  avatar: '',
}
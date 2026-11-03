import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

// 开启历史模式
// vue2中使用的mode：history 实现

const routerHistory = createWebHistory();

const router = createRouter({
    history: routerHistory,
    routes: [
        {
            // 站点入口：项目欢迎页（点击「进入详情」进入网站首页 /welcome）
            path:'/',
            component: () => import('../pages/intro.vue'),
            meta:{
                need_login: false,
                index: 1
            }
        },
        {
            path:'/welcome',
            component: () => import('../pages/welcome.vue'),
            meta:{
                need_login: false,
                index: 1
            }
        },
        {
            path:'/shop',
            component: () => import('../pages/shop.vue'),
            meta:{
                need_login: false,
                index: 2
            },
        },
        {
            // 登录 / 注册独立页面
            path:'/login',
            component: () => import('../pages/login.vue'),
            meta:{
                need_login: false,
                index: 1
            }
        },
        {
            // 发布闲置独立页面（需要登录）
            path:'/publish',
            component: () => import('../pages/publish.vue'),
            meta:{
                need_login: true,
                index: 2
            }
        },
        {
            // 商品详情独立页面
            path:'/goods/:id',
            component: () => import('../pages/goods.vue'),
            meta:{
                need_login: false,
                index: 2
            }
        },
        {
            // 卖家主页：卖家信息 + 在售商品
            path:'/seller/:id',
            component: () => import('../pages/seller.vue'),
            meta:{
                need_login: false,
                index: 2
            }
        },
        {
            // 订单详情 + 买家评价
            path:'/order/:id',
            component: () => import('../pages/order.vue'),
            meta:{
                need_login: true,
                index: 5
            }
        },
        {
            // 独立搜索页：热搜词、搜索历史、结果分页
            path:'/search',
            component: () => import('../pages/search.vue'),
            meta:{
                need_login: false,
                index: 2
            }
        },
        {
            // 分类管理（管理员）
            path:'/admin/category',
            component: () => import('../pages/adminCategory.vue'),
            meta:{
                need_login: true,
                index: 5
            }
        },
        {
            path:'/customer',
            component: () => import('../pages/person.vue'),
            meta:{
                need_login: false,
                index: 5
            },
        },
        {
            path:'/want',
            component: () => import('../pages/want.vue'),
            meta:{
                need_login: false,
                index: 3
            },
        },
        {
            path:'/circle',
            component: () => import('../pages/circle.vue'),
            meta:{
                need_login: false,
                index: 4
            },
        },
        // 未匹配的地址（含已下线的 /auction、/help）统一回首页，避免白屏
        {
            path:'/:pathMatch(.*)*',
            redirect:'/welcome'
        }
    ]
})

// 全局前置守卫：需要登录的页面直接跳登录页，并带上回跳地址
router.beforeEach(to => {
    if (!to.meta || !to.meta.need_login) return true
    const userStore = useUserStore()
    if (userStore.isLogged) return true
    return { path: '/login', query: { redirect: to.fullPath } }
})

export default router
import { createRouter, createWebHistory } from 'vue-router'

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
            // component: resolve => require(['../pages/welcome.vue'], resolve),
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


export default router
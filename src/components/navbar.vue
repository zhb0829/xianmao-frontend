<template>
    <div class="container" :style="backOpacity">
        <div>
            <div class="logo">
            </div>
            <div class="nav">
                <ul class="navlist">
                    <!-- 动态绑定字体颜色样式与背景颜色样式 -->
                    <RouterLink to="welcome" :style="[fontColor, { '--color': hColor }]">首页</RouterLink>
                </ul>
                <ul class="navlist">
                    <RouterLink to="shop" :style="[fontColor, { '--color': hColor }]">商店</RouterLink>
                </ul>
                <div class="hk" :style="hkColor"></div>
            </div>
        </div>
        <!-- 试验下拉菜单用例 -->
        <!-- <div class="dropdown">
            <span>鼠标移动到我这！</span>
            <div class="dropdown-content">
                <p>菜鸟教程</p>
                <p>www.runoob.com</p>
            </div>
        </div> -->
        <!-- <div class="nav2">
            <div class="nav_header">导航..</div>
            <ul class="nav_select">
                <li><a href="javascript:;">选项1</a></li>
                <li><a href="javascript:;">选项2</a></li>
                <li><a href="javascript:;">选项3</a></li>
            </ul>
        </div> -->
        <div class="person-menu">
            <div class="menu-title">
                <RouterLink to="customer" :style="[fontColor, { '--color': hColor }]">个人</RouterLink>
            </div>
            <ul class="menu-content">
                <li class="menu-item"><RouterLink to="customer">我买入的</RouterLink></li>
                <li class="menu-item"><RouterLink to="customer">我卖出的</RouterLink></li>
                <!-- <li class="menu-item"><RouterLink to="customer">设置</RouterLink></li> -->
            </ul>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            // 这里设置透明度与颜色
            backOpacity: "background-color: transparent;",
            fontColor: "color: rgba(32, 133, 11, 0.856);",
            hkColor: "background-color: rgba(32, 133, 11, 0.856);",
            hColor: "rgba(255, 255, 255)",
            // 首页首屏是深色粒子背景，需要浅色导航文字
            isWelcome: false
        }
    },
    mounted() {
        this.isWelcome = this.$route.path === '/welcome';
        // 监听滚动条状态
        window.addEventListener('scroll', this.handleScroll)
        this.handleScroll();
    },
    watch: {
        // 常驻组件，路由切换后需要重新决定配色
        '$route.path'(path) {
            this.isWelcome = path === '/welcome';
            this.handleScroll();
        }
    },
    methods: {
        // 滚动条滑动测试
        handleScroll() {
            let scrollTop = document.documentElement.scrollTop
            // 首页顶部：深色星网背景上使用浅色字
            if (this.isWelcome && !scrollTop) {
                this.backOpacity = "background-color: transparent;";
                this.fontColor = "color: #eafff0;";
                this.hkColor = "background-color: #7ef0a8;";
                this.hColor = "rgba(6, 20, 13)";
                return;
            }
            if (scrollTop) {
                const opa = scrollTop / 300;
                this.backOpacity = `background-color: rgba(32, 133, 11, ${opa});`;
                this.fontColor = `color: rgba(255, 255, 255, ${opa});`;
                this.hkColor = `background-color: rgba(255, 255, 255, ${opa});`;
                this.hColor = `rgba(32, 133, 11, ${opa})`
            } else if (scrollTop === 0) {
                this.backOpacity = "background-color: transparent;"
                this.fontColor = `color: rgba(32, 133, 11, 0.856);`
                this.hkColor = `background-color: rgba(32, 133, 11, 0.856);`;
                this.hColor = `rgba(255, 255, 255)`
            }
        },
        beforeDestroy() {
            window.removeEventListener('scroll', this.handleScroll)
        },
    }
}
</script>

<style scoped>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}
/* 整体大容器 */
.container {
    position: fixed;
    height: 60px;
    width: 100vw;
    display: flex;
    justify-content: space-between;
    background-color: transparent;
    z-index: 5;
}
/* 左边带滑块的导航栏菜单 */
.nav {
    position: relative;
    width: 13vw;
    height: 60px;
    display: flex;
    margin-left: 5%;
}

.navlist {
    list-style: none;
    margin: 4px 0;
    width: 50%;
    border-radius: 7px;
    font-weight: 550;
    color: rgba(32, 133, 11, 0.856);
    align-items: center;
    display: flex;
    justify-content: center;
    transition: 0.5s;
}

.nav ul a {
    font-size: 18px;
    font-weight: 550;
    color: rgba(32, 133, 11, 0.856);
    height: 100%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: 0.3s;
    text-decoration: none;
}

.nav ul a:hover {
    font-size: 18px;
    font-weight: 550;
    color: var(--color) !important;
}

.nav ul:hover {
    cursor: pointer;
}

.nav ul li {
    display: none;
}
/* 这是滑块 */
.hk {
    left: 0;
    height: 52px;
    margin: 4px 0;
    width: 50%;
    border-radius: 7px;
    position: absolute;
    background-color: rgba(32, 133, 11, 0.856);
    display: flex;
    align-items: center;
    z-index: -1;
    opacity: 0;
    transition: all 0.5s;
}
/* 两个菜单项分别对应滑块位置 */
.navlist:nth-child(1):hover~.hk {
    opacity: 100%;
    left: 0
}

.navlist:nth-child(2):hover~.hk {
    opacity: 100%;
    left: 50%
}
/* 右边个人下拉菜单 */
.person-menu {
    font-size: 18px;
    font-weight: 550;
    color: rgba(32, 133, 11, 0.856);
    cursor: pointer;
    height: auto;
    position: relative;
    transition: all 0.2s;
    margin: 0vw 4vw;
    display: flex;
    justify-content: center;
}
/* 下拉菜单标题 */
.menu-title {
    margin: 4px 0;
    height: 52px;
    width: 6vw;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 8px;
    transition: all 0.2s;
}
.menu-title:hover{
    /* background-color: antiquewhite; */
    background-color: rgba(32, 133, 11, 0.856);
    color: var(--color) !important;
}
.menu-title:hover a{
    color: var(--color) !important;
}
/* 下拉菜单内容 */
.menu-content {
    position: absolute;
    left: calc(50% - 6vw / 2);
    margin-top: 0;
    top: 60px;
    flex-direction: column;
    list-style: none;
    visibility: hidden;
    opacity: 0;
    background-color: antiquewhite;
    border-radius: 8px;
    padding: 6px;
    overflow: hidden;
    width: 6vw;
    height: 0;
    transition: all 0.5s;
    min-width: 100px;
}

.person-menu:hover .menu-content {
    visibility: visible;
    opacity: 1;
    height: calc(6vw + 12px);
}
/* 每个子选项 */
.menu-item {
    display: flex;
    justify-content: center;
    border-radius: 8px;
    transition: all 0.2s;
    min-height: 40px;
    align-items: center;
    height: 1vw;
}
.menu-item a{
    color: rgba(32, 133, 11, 0.856);
}
.menu-item:hover {
    background-color: rgba(32, 133, 11, 0.856);
    color: white;
}
.menu-item:hover a{
    color: white;
}
/* 下面的实验用下拉菜单样式 */
.dropdown {
    position: relative;
    display: inline-block;
}

.dropdown-content {
    display: none;
    position: absolute;
    background-color: #f9f9f9;
    min-width: 160px;
    box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
    padding: 12px 16px;
}

.dropdown:hover .dropdown-content {
    display: block;
}

a {
    text-decoration: none;
    /* color: #000; */
}

.nav2 {
    position: relative;
    line-height: 50px;
    width: 200px;
    margin: 200px auto;
    text-align: center;
}

.nav_header,
.nav_select li {
    width: 200px;
    height: 50px;
    background-color: #fff;
    text-align: center;
}

.nav_select {
    position: absolute;
    top: 50px;
    height: 0;
    overflow: hidden;
    transition: all .5s;
}

.nav_select li {
    border: 1px solid #999;
}

.nav_header:hover {
    box-shadow: 0px 1px 2px;
}

.nav_header:hover+.nav_select,
.nav_select:hover {
    height: 150px;
}</style>
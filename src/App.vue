<template>
  <msg :msg="submitMsg" v-if="isMsgShow" @hiddemsg="triggerClose"></msg>
  <load v-if="isLoading"></load>
  <div class="top_container">
    <!-- 首页、商品市场、个人中心使用新版独立导航（组件内置页头），不再叠加旧导航栏 -->
    <navbar v-if="!isLanding"></navbar>
    <RouterView v-slot="{ Component }">
      <component :is="Component" @showmsg="showMsg" />
    </RouterView>
  </div>
</template>

<script>
import navbar from './components/navbar.vue';
import msg from './components/msg.vue';
import load from './components/loading.vue';
export default {
  name: 'topPage',
  components: { msg, navbar, load },
  computed: {
    // 首页、商品市场、个人中心、求购专区、校园圈、登录/注册、发布、商品详情都使用页面自带的站点页头
    isLanding() {
      const p = this.$route.path
      if (p.startsWith('/goods')) return true
      return ['/', '/welcome', '/shop', '/customer', '/want', '/circle', '/login', '/publish'].includes(p)
    }
  },
  data() {
    return {
      // 设置信息弹出，弹出提示，弹出颜色
      isMsgShow: 0,
      submitMsg: {
        info: "首页弹出",
        colorType: 1
      },
      // 设置加载动画
      isLoading: 0
    }
  },
  methods: {
    // 倒计时结束触发提示框关闭
    triggerClose() {
      this.isMsgShow = 0;
      // console.log("触发关闭");
    },
    // 触发提示框
    showMsg(data) {
      // console.log("触发一次提示");
      if (this.isMsgShow != 1) {
        this.submitMsg.info = data.info;
        this.submitMsg.colorType = data.colorType;
        this.isMsgShow = 1;
      }
      else {
        this.isMsgShow = 0;
        this.$nextTick(() => {
          this.submitMsg.info = data.info;
          this.submitMsg.colorType = data.colorType;
          this.isMsgShow = 1;
        })
      }
    },
    // 加载提示
    showLoad(){
      if(this.isLoading){
        this.isLoading = 0;
        this.$nextTick(()=>{

        })
      }
      else{

      }
    }
  }
}
</script>

<style scoped>
* {
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}

.top_container {
  position: relative;
}
</style>
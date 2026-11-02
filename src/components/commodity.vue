<template>
  <div class="container" ref="c" :stlye="backC">
    <div class="commodity-cotent" v-if="cType != 'manage'">
      <div class="imgItem" style="width: 10%;">
        <img v-if="this.pic != ''" class="fimg" :src="'data:image/Jpeg;base64,' + this.pic">
        <img v-else-if="this.pic == ''" class="fimg" src="../material/ico/xm-logo.png">
      </div>
      <div class="dItem">{{ this.name }}</div>
      <div class="dItem">{{ this.seller }}</div>
      <div class="dItem">{{ this.orderStatus }}</div>
      <div class="dItem">{{ this.logisticsStatus }}</div>
      <div class="dItem">{{ this.time }}</div>
      <div class="function-content">
        <styleButton2 class="button" text="去支付" @click="pay()" v-if="logisticsStatus == '未发货' && orderStatus == '未支付' && personType == 'buyer'"></styleButton2>
        <styleButton2 class="button" text="去发货" @click="press()" v-if="logisticsStatus == '未发货' && personType == 'seller'"></styleButton2>
        <styleButton2 class="button" text="确认收货" @click="confirmOrder()" v-if="logisticsStatus == '已发货' && orderStatus == '已支付' && personType == 'buyer'"></styleButton2>
        <styleButton2 class="button" text="收藏" @click="like()"></styleButton2>
        <div class="delButton" @click="deleteOrderItem()" v-if="personType == 'buyer' || personType == 'seller'">删除</div>
      </div>
    </div>
    <div class="commodity-cotent" v-else-if="cType == 'manage'">
      <div class="imgItem" style="width: 10%;">
        <img class="fimg" :src="'data:image/Jpeg;base64,' + this.pic">
      </div>
      <div class="dItem">{{ this.name }}</div>
      <div class="dItem">{{ this.seller }}</div>
      <div class="dItem">{{ this.description }}</div>
      <div class="dItem">{{ this.time }}</div>
      <div class="dItem">{{ this.status }}</div>
      <div class="function-content">
        <div class="refuseButton" @click="refuse()" v-if="status == '未审核' || status == '驳回'">驳回</div>
        <div class="passButton" @click="pass()" v-if="status == '未审核' || status == '驳回'">通过</div>
        <div class="delButton" @click="deleteOrder()" v-if="status == '通过'">下架</div>
      </div>
    </div>
  </div>
</template>

<script>
import styleButton2 from './styleButton2.vue';
export default {
  // 传入的商品数据
  props: ['personType','cData', 'cType', 'comfE', 'likeE', 'delE', 'passE','payE','refE','prsE'],
  data() {
    return {
      // 默认颜色
      backC: '',
      id: this.cData.id,
      name: this.cData.name,
      pic: this.cData.pic,
      seller: this.cData.seller,
      orderStatus: this.cData.orderStatus,
      logisticsStatus: this.cData.logisticsStatus,
      time: this.changeTime(this.cData.time),
      // 订单id
      oid: this.cData.oid,
      status: this.cData.status,
      description: this.cData.description
    }
  },
  methods: {
    confirmOrder() {
      this.$emit("comfE", this.oid ?? this.id);
      this.logisticsStatus = '已签收';
      this.orderStatus = '已完成';
    },
    deleteOrder() {
      // 管理端"下架"
      this.$emit("delE", this.id);
      this.status = '下架';
    },
    deleteOrderItem() {
      // 订单列表里删除这条订单
      this.$emit("delE", this.oid ?? this.id);
    },
    like() {
      this.$emit("likeE", this.id);
    },
    pass() {
      this.$emit("passE", this.id);
      this.status = '通过';
    },
    pay(){
      this.$emit("payE", this.oid ?? this.id);
      this.orderStatus = '已支付';
    },
    press(){
      this.$emit('prsE', this.oid ?? this.id);
      this.logisticsStatus = '已发货';
    },
    refuse(){
      this.$emit("refE",this.id);
      this.status = '驳回';
    },
    changeTime(t){
      let tt = new Date(t);
      let y = tt.getFullYear();
      let m = tt.getMonth()+1;
      m = m < 10? '0'+m:m;
      let d = tt.getDate();
      d = d < 10? '0'+d:d;
      return y+"-"+m+"-"+d;
    },
    reflashImg() {
      console.log("刷新图片"); 
      this.pic = this.cData.pic;
    }
  },
  mounted() {
    let div = this.$refs.c;
    if (this.orderStatus == '未审核') {
      this.backC = `background-color: rgba(0, 133, 11, 0.8);`;
    }
    else if (this.orderStatus == '已审核') {
      this.backC = `background-color: rgba(32, 133, 11, 0.8);`;
    }
    else {
      this.backC = `background-color: rgba(0, 0, 255, 0.8);`;
    }
    // console.log("创建商品", this.backC);
  },
  watch: {
    cData: {
      handler() {
        this.id = this.cData.id
        this.name = this.cData.name
        this.pic = this.cData.pic
        this.seller = this.cData.seller
        this.orderStatus = this.cData.orderStatus
        this.logisticsStatus = this.cData.logisticsStatus;
        this.time = this.changeTime(this.cData.time);
        this.reflashImg();
        // console.log("c",this.pic,this.cData.pic);
      },
      // immediate: true,
      deep: true
    }
  },
  components: { styleButton2 }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 主容器 */
.container {
  display: flex;
  overflow: hidden;
  width: 100%;
  height: 20%;
  max-height: 200px;
  min-height: 90px;
  margin: 1.5% 0 0 0;
  transition: all 0.3s;
  overflow: hidden;
  border-radius: 10px;
  /* background-color: white; */
}

.container:hover {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background-color: white;
  /* background-color: rgba(235, 200, 5, 0.2); */
}

/* 商品内容容器 */
.commodity-cotent {
  width: 100%;
  display: flex;
  align-items: center;
  position: relative;
}

/* 图片专用div */
.imgItem {
  width: 20%;
  max-height: 60px;
  border-radius: 10px;
  overflow: hidden;
  margin: 0 2%;
}

.fimg {
  width: 100%;
  height: 100%;
}

.dItem {
  width: 10%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  /* /超出的文本隐藏 */
  text-overflow: ellipsis;
  /* //溢出用省略号显示 */
  white-space: nowrap;
  /* //溢出不换行 */
}

/* 功能区 */
.function-content {
  display: flex;
  align-items: center;
  min-width: 30%;
  justify-content: right;
  position: absolute; 
  right: 0; 
  margin-right: 5%;
  /* justify-content: space-around; */
}
.refuseButton{
  border: none;
  width: 6vw;
  height: 30px;
  transition: all 0.3s;
  color: rgba(32, 133, 11, 0.856);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  margin: 0 1%;
  font-size: 14px;
  color: white;
  border: 2px solid rgba(32, 133, 11, 0.856);
  background-color: rgba(32, 133, 11, 0.95);
}
.refuseButton:hover{
  background-color: rgba(32, 160, 11, 0.9);
  border: 2px solid rgba(32, 160, 11, 0.8);
  color: white;
}
/* 通过按钮 */
.passButton {
  border: none;
  width: 6vw;
  height: 30px;
  transition: all 0.3s;
  color: rgba(32, 133, 11, 0.856);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  margin: 0 1%;
  font-size: 14px;
  color: white;
  border: 2px solid rgba(32, 133, 11, 0.856);
  background-color: rgba(32, 133, 11, 0.95);
}

.passButton:hover {
  background-color: rgba(32, 160, 11, 0.9);
  border: 2px solid rgba(32, 160, 11, 0.8);
  color: white;
}

/* 删除按钮 */
.delButton {
  border: none;
  width: 6vw;
  height: 30px;
  transition: all 0.3s;
  /* border: 2px solid rgba(32, 133, 11, 0.856); */
  border: 2px solid brown;
  color: brown;
  /* color: rgba(32, 133, 11, 0.856); */
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  margin: 0 1%;
  font-size: 14px;
  background-color: white;
}

.delButton:hover {
  /* background-color: rgba(32, 133, 11, 0.95); */
  background-color: brown;
  border: 2px solid brown;
  color: white;
}</style>
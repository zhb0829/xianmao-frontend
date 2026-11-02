<template>
  <div class="img-content" ref="ic" @mousewheel="onMouseWheel($event)">
    <img class="img" v-for="(imgSrc, index) in pictures" @click="switchImg(index)"
      :class="{ seleC: index == selectedID, unseleC: index != selectedID }" :src="imgSrc" :ref="'imgID' + index" />
  </div>
</template>
  
<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.img-content {
  margin: 0;
  padding: 0;
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: left;
  overflow-y: hidden;
  min-height: 80px;
  padding: 5px 0;
}

.img-content::-Webkit-scrollbar {
  display: none;
}

.img {
  margin: 0 5px;
  padding: 0;
  width: 100%;
  height: 100%;
  max-width: 100px;
  max-height: 10vw;
  cursor: pointer;
  border-radius: 8px;
  object-fit: contain;
}

.seleC {
  border: 4px solid rgba(255, 0, 0, 0.6);
}

.unseleC {
  border: 4px solid rgba(255, 255, 255, 0.0);
}
</style>
  
<script>
export default {
  props: [
    'imgL',
    'changeImg',
    'isCirculate'
  ],
  data() {
    return {
      selectedID: 0,
      seleC: 'border: 2px solid rgba(255, 0, 0, 0.6);',
      unseleC: 'border: 2px solid rgba(255, 255, 255, 0.0);',
      // 图片列表
      pictures: this.imgL,
      timer: ''
    }
  },
  watch: {
    imgL: {
      handler(oldvalue, newvalue) {
        // console.log("监听", this.pictures, this.imgL);
        this.pictures = this.imgL;
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.pictures = this.imgL;
      this.circleImg();
    })
  },
  methods: {
    // 鼠标点击切换图片
    switchImg(index) {
      this.onselected(index);
      clearInterval(this.timer);
    },
    onselected(index) {
      // 这两种方式都可以获取div
      // eval将字符串转换成js代码
      // let val = eval('this.$refs.imgID'+index)[0]
      // 将选中id更改为当前点击的
      this.selectedID = index;
      let temp2 = eval('this.$refs.imgID' + index)[0];
      this.changeImg(index);
    },
    // 鼠标滚轮触发事件
    onMouseWheel(e) {
      let eventDelta = -e.wheelDelta || -e.deltaY * 40;
      let temp = this.$refs.ic;
      temp.scrollLeft = temp.scrollLeft + eventDelta / 2;
    },
    // 循环播放函数
    circleImg() {
      // console.log("此时状态",this.isCirculate);
      let len = this.ditlen(this.pictures);
      // console.log("进1", len, this.pictures);
      if (this.pictures) {
        if (this.isCirculate == 1) {
          // console.log("进去");
          this.timer = setInterval(() => {
            this.selectedID += 1;
            if (this.selectedID >= this.imgL.length)
              this.selectedID = 0;
            this.onselected(this.selectedID);
          }, 3000);
        }
      }
    },
    // 获取字典长度
    ditlen(d){
      let count = 0;
      for(let i in d){
        count += 1;
        // console.log(count);
      }
      return count;
    }
  },
  beforeUnmount() {
    clearInterval(this.timer);
  }
}
</script>
<template>
  <div class="pie-container">
    <div class="pie" v-bind:style="colorStyle">
      <!--        <strong v-if="this.isShowNumber=='true'" v-bind:style="strongStyle">{{(this.value * 100).toFixed()}}%</strong>-->
      <div class="div_1" v-bind:style="div1Style"></div>
      <div class="div_2" v-bind:style="div2Style"></div>
      <span v-bind:style="ringStyle" v-if="this.ring !== 0"></span>
    </div>
    <div class="pie-item">
      <div style="font-size: 3vh">{{ setting.rank_type }}</div>
      <p>{{ this.pm }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "VuePie",
  props: [
    'setting'
    // settings:{
    // "value": String,
    // "ring" : String,
    // "isShowNumber" : String,
    // "color" : String,
    // "rotate" : String,
    // "isAnimate" : String
    // },
  ],
  data() {
    return {
      pm: "",
      value: this.setting.rank,
      ring: 0.8,
      isShowNumber: "true",
      // color : "cornflowerblue",
      color: 'rgba(32, 133, 11, 1.0)',
      rotate: "",
      isAnimate: "true",
      ringStyle: {},
      colorStyle: {},
      div1Style: {
        transform: 'rotate(0turn)'
      },
      div2Style: {
        opacity: 0,
        transform: 'rotate(0turn)'
      },
      strongStyle: {}
    }
  },
  watch: {
    ring(oldvalue, newvalue) {
      console.log(newvalue);
    },
    setting: {
      handler() {
        this.value = this.setting.rank;
        console.log("更新详细饼形图");
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initPie();
      this.numberAnimation({
        number: this.value * 100,
        s: 1.5
      });
    })
  },
  methods: {
    initPie() {
      var self = this;
      var isAnimate = this.isAnimate; // 是否动画
      // console.log(isAnimate)
      if (isAnimate == 'true') {
        setTimeout(self._showPie, 100);
      } else {
        self._showPie()
      }
    },
    _showPie() {
      var self = this;
      var percent = this.value, // 百分比
        isRing = this.ring || 0,  // 是否空心
        color = this.color,  // 自定义颜色
        rotate = this.rotate,    // 是否旋转
        isAnimate = this.isAnimate || 'false'; // 是否动画
      if (isRing) {
        var w = isRing * 100;
        this.ringStyle = {
          width: w + '%',
          height: w + '%',
          top: (50 - w / 2) + '%',
          left: (50 - w / 2) + '%',
          // 颜色测试
          // background: '#f0f0f0'
          background: '#ffffff'
        }
      }
      if (color) {
        this.colorStyle.backgroundImage = 'linear-gradient(to right, transparent 50%, ' + color + ' 0)';
        this.div2Style.background = color;
      }
      if (percent > 0.5) {
        self.div1Style.transform = 'rotate(0.5turn)';
        if (isAnimate == 'true') {
          setTimeout(function () {
            self.div2Style.opacity = 1;
            self.div2Style.transform = 'rotate(' + (percent - 0.5) + 'turn)';
          }, 800);
        } else {
          self.div2Style.opacity = 1;
          self.div2Style.transform = 'rotate(' + (percent - 0.5) + 'turn)';
        }
      } else {
        self.div1Style.transform = 'rotate(' + percent + 'turn)';
      }
      if (rotate) {
        this.colorStyle.transform = 'rotate(' + rotate + 'deg)';
        // 文字转回来
        this.strongStyle.transform = 'rotate(' + -rotate + 'deg)';
      }
    },
    numberAnimation({ number, s }) {
      var handle, //句柄
        content = 0; //内容
      var self = this;
      var time = number / (s * 60); //秒数
      //requestAnimationFrame 每秒大约60帧 就是每秒的执行次数 所以这里  *60
      // number / (s * 60) 每一次执行 需要相加的大小
      var fn = () => {
        if (number - time <= content) {
          //number - time  因为精度问题 最后一次执行 不一定满足 === number
          cancelAnimationFrame(handle);
          content = number;
        } else {
          content += time;
          handle = requestAnimationFrame(fn);
        }
        // console.log("===>"+content)
        self.pm = parseInt(content) + "%";
        //取整
      };
      requestAnimationFrame(fn);
    }
  },
  created() {
    // this.initPie();
    // this.numberAnimation({
    //
    //   number: this.value*100,
    //   s: 1
    // });
  }
}


function numberAnimation({ number, s }) {
  var handle, //句柄
    content = 0; //内容
  var time = number / (s * 60); //秒数
  //requestAnimationFrame 每秒大约60帧 就是每秒的执行次数 所以这里  *60
  // number / (s * 60) 每一次执行 需要相加的大小
  var fn = () => {
    if (number - time <= content) {
      //number - time  因为精度问题 最后一次执行 不一定满足 === number
      cancelAnimationFrame(handle);
      content = number;
    } else {
      content += time;
      handle = requestAnimationFrame(fn);
    }
    // console.log("===>"+content)
    this.pm = parseInt(content) + "%";
    //取整
  };
  requestAnimationFrame(fn);
}
// numberAnimation({
//   number: 90,
//   s: 2
// });
</script>

<style lang="less" rel="stylesheet/less" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.pie-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 100%;
}

.pie-item {
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: right;
  align-items: flex-end;
  height: 100%;
}

.pie-item>p {
  font-size: 7vh;
  margin-top: 2vh;
}

.pie {
  position: relative;
  z-index: 0;
  left: 5vh;
  top: 0;
  float: left;
  //width: 140px;
  //height: 140px;
  width: 14vh;
  height: 14vh;
  //line-height: 140px;
  line-height: 14vh;
  border-radius: 50%;
  text-align: center;
  color: #000;
  font-size: 140%;
  // background-color:#f0f0f0;
  background: white;
  //background-image: linear-gradient(to right, transparent 50%, yellowgreen 0);
  //cursor:pointer;
  overflow: hidden;

  strong {
    position: absolute;
    width: 100%;
    left: 0;
    z-index: 1;
    margin: 0;
    padding: 0;
  }

  span {
    display: block;
    border-radius: 50%;
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    margin: 0;
    padding: 0;
  }

  div {
    position: absolute;
    top: 0;
    width: 60%;
    height: 100%;
    left: 50%;
    transform: rotate(0turn);
    transform-origin: center left;
    transition: transform 0.7s linear;
    margin: 0;
    padding: 0;
  }

  >.div_1 {
    background-color: inherit;
    z-index: -2;
  }

  >.div_2 {
    opacity: 0;
    z-index: -1;
    background-color: yellowgreen;
  }

}
</style>

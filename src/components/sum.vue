<style scoped>
#sector {
  width: 100%;
  height: 100%;
  margin: 0 auto;
}
</style>
<template>
  <div id="sector">
  </div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'sector',
  props: ['input'],
  data() {
    return {
      ec: null,
      option: {
        title: {
          text: '商品种类比例',
          top: '6%',
          right: '5%'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}'
        },
        // legend: {
        //   orient: 'vertical',
        //   left: 'right',
        //   top:'15%',
        //   data: ['10~12', '12~14', '14~15', '大于15']
        // },
        series: [
          {
            name: '分值',
            type: 'pie',
            radius: '60%',
            center: ['40%', '50%'],
            data: this.input,
            // itemStyle:{
            //   normal:{
            //     label:{
            //       show: true,
            //       formatter: '{b} : {c}'
            //     },
            //     labelLine :{show:true}
            //   }
            // },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              }
            }
          }
        ]
      }

    }
  },
  mounted() {
    this.init();
  },
  methods: {
    // 初始化
    init() {
      this.ec = echarts.init(document.getElementById('sector'));
      this.ec.showLoading();
      this.$http({
        method: 'get',
        url: 'manage/category'
      }).then(res => {
        let tdata = [];
        // console.log('饼型统计图开始转换', res.data);
        for (let item in res.data) {
          let tempv = res.data[item].toFixed(2)*100;
          let temp = {
            value: tempv,
            name: item
          }
          tdata.push(temp);
        }
        let tempoption = {
          title: {
            text: '商品种类比例',
            top: '6%',
            right: '5%'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{b} {c}%'
          },
          series: [
            {
              name: '分值',
              type: 'pie',
              radius: '60%',
              center: ['40%', '50%'],
              data: tdata,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                }
              }
            }
          ]
        }
        this.ec.hideLoading();
        this.ec.setOption(tempoption);
        //建议加上以下这一行代码，不加的效果图如下（当浏览器窗口缩小的时候）。超过了div的界限（红色边框）
        window.addEventListener('resize', ()=> { 
          this.ec.resize();
        });
        // console.log("成功");
        this.$emit("showmsg",{info:"饼成功",coloType: 1});
      }).catch(err => {
        this.$emit("showmsg",{info:"饼失败",coloType: 3});
      });
      // 重绘大小用例，ResizeObserver
      // let chartsResizeObser = new ResizeObserver(()=>{
      //   // 这里面是界面大小改变时需要执行的函数
      //   this.ec && this.ec.resize();
      // })
      // // observe函数里面添加需要重绘大小的dom，通过id拿到dom
      // chartsResizeObser.observe(document.getElementById("sector"));
    }
  },
  watch: {
    input: {
      deep: true,
      handler(oldvalue, newvalue) {
        // this.option.series.data = this.input;
        // console.log("更新饼形图", this.option.series[0], this.input);
        // this.ec.setOption(this.option);
      }
    }
  },
  created() {
  }
}
</script>
<template>
  <div class="msgbox" ref="box" :style="infocolor">
    {{ this.infomsg }}
  </div>
</template>

<script>
export default {
  props: ['msg', 'hiddemsg'],
  data() {
    return {
      // 提示的颜色，1 为成功（品牌绿），2 为警告（橙），其他为中性
      infocolor: this.msg.colorType,
      infomsg: this.msg.info,
      timer: null,
      leaveTime: 400,
    }
  },
  mounted() {
    const div = this.$refs.box
    if (this.msg.colorType == 1) {
      this.infocolor = 'background-color: var(--cl-brand-strong, #00805f);'
    } else if (this.msg.colorType == 2) {
      this.infocolor = 'background-color: #c2701c;'
    } else {
      this.infocolor = 'background-color: rgba(15, 27, 22, 0.9);'
    }

    this.$nextTick(() => {
      this.timer = setInterval(() => {
        div.style.top = '20px'
        div.style.opacity = this.leaveTime / 270
        if (this.leaveTime == 0) {
          clearInterval(this.timer)
          this.$emit('hiddemsg')
        } else {
          this.leaveTime--
        }
      })
    })
  },
  beforeUnmount() {
    clearInterval(this.timer)
  },
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.msgbox {
  position: fixed;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: min(86vw, 620px);
  min-height: 40px;
  padding: 9px 22px;
  border-radius: 999px;
  color: #fff;
  font-size: 13.5px;
  font-weight: 500;
  letter-spacing: 0.2px;
  text-align: center;
  box-shadow: 0 16px 38px rgba(6, 20, 13, 0.28);
  transition: top 0.2s ease, opacity 0.15s linear;
  pointer-events: none;
}
</style>
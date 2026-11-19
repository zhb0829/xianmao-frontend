<template>
  <div class="styleButton2">{{ name }}</div>
</template>

<script>
export default {
  props: [
    'text',
    'color',
  ],
  data() {
    return {
      name: this.text,
    }
  },
  mounted() {

  },
  watch: {
    text: {
      handler(oldvalue,newvalue){
        // console.log("新值",newvalue);
        this.name = this.text;
      },
      immediate: true,
      deep: true
      }
  },
  methods: {
    // p(){
    //   console.log("当前值",this.name, this.text);
    // }
  }
}
</script>

<style scoped>
.styleButton2 {
  border: none;
  width: 6vw;
  height: 30px;
  transition: all 0.3s;
  /* min-height: 40px; */
  border: 2px solid rgba(32, 133, 11, 0.856);
  /* font-weight: 550; */
  color: rgba(32, 133, 11, 0.856);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  margin: 0 1%;
  /* font-family: Arial Black; */
  /* font-family: Verdana; */
  font-size: 14px;
  background-color: white;
}

.styleButton2:hover {
  background-color: rgba(32, 133, 11, 0.95);
  /* box-shadow: 2px 5px 15px rgba(32, 133, 11, 0.856); */
  color: white;
}
</style>
<template>
  <div class="pagination">
    <div class="switchButton" @click="prePage()" v-if="totalPageNum > 1">&lt</div>
    <div :class="[{ 'on': currentPageNum == item }, { 'pageButton': currentPageNum != item }]" v-for="item in pageL"
      @click="switchPage(item)">{{ item }}</div>
    <div class="switchButton" @click="nextPage()" v-if="totalPageNum > 1">></div>
  </div>
</template>

<style scoped>
.pagination {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: center;
}

.pageButton {
  width: 46px;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 4px 2px;
  border-radius: 10px;
  background-color: rgba(32, 133, 11, 0.2);
  transition: all 0.2s;
}

.pageButton:hover {
  cursor: pointer;
  background-color: rgba(32, 133, 11, 0.856);
}

.switchButton:hover {
  cursor: pointer;
  background-color: rgba(32, 133, 11, 0.856);
  transition: all 0.2s;
}

.switchButton {
  width: 46px;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 4px 2px;
  border-radius: 10px;
  background-color: rgba(32, 133, 11, 0.856);
  background-color: rgba(32, 133, 11, 0.2);
}

.on {
  width: 46px;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 4px 2px;
  border-radius: 10px;
  background-color: rgba(32, 133, 11, 0.856);
}
</style>
<script>
export default {
  props: ['pageInfo', 'changePage'],
  data() {
    return {
      // 默认值
      currentPageNum: this.pageInfo.cNum,
      // 一页内容
      pageSize: this.pageInfo.pSize,
      // 总页数
      totalPageNum: this.pageInfo.pageL,
      // 总页数
      pageL: this.pageInfo.pageL,
      currentType: 'all',
    }
  },
  mounted() {
    // console.log("分页组件",this.pageInfo);
  },
  methods: {
    prePage() {
      if (this.currentPageNum > 1) {
        this.currentPageNum -= 1;
        this.switchPage(this.currentPageNum);
      }
    },
    nextPage() {
      if (this.currentPageNum < this.totalPageNum) {
        this.currentPageNum += 1;
        this.switchPage(this.currentPageNum);
      }
    },
    switchPage(item, ind) {
      // console.log("切换", this.pageInfo);
      this.currentPageNum = item;
      this.$emit("changePage", this.currentPageNum);
    },
  },
  watch: {
    pageInfo: {
      handler(oldvalue, newvalue) {
        this.pageL = this.pageInfo.pageL;
        this.totalPageNum = this.pageInfo.pageL;
        // console.log("触发页数改变",newvalue);
      },
      immediate: true,
      deep: true  
    }
  }
}
</script>
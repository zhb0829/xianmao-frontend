<template>
    <button class="styleButton">{{ this.text }}</button>
</template>
  
<style scoped>
* {
    margin: 0;
    padding: 0;
}

.styleButton {
    border: none;
    width: 5vw;
    height: 2vw;
    transition: 0.2s;
    min-height: 40px;
    background-color: rgba(32, 133, 11, 0.856);
    color: white;
    cursor: pointer;
    border-radius: 8px;
}
.styleButton:hover {
  background-color: rgba(32, 160, 11, 0.856);
  box-shadow: 2px 5px 15px rgba(32, 133, 11, 0.856);
}
</style>
  
<script>
export default {
    props: [
        'text',
        'color',
        'Price',
        'Likes',
    ],
    data() {
        return {
            name: this.text,
            price: this.Price,
            likes: this.Likes,
            discribe: '',
            pictures: '',
        }
    },
    mounted() {

    },
    methods: {

    }
}
</script>
<template>
    <div class="mask">
        <div class="ring-1"></div>
        <div class="ring-2"></div>
    </div>
</template>

<style scoped>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    transition: all 0.2s;
}

.mask {
    position: absolute;
    width: 100%;
    height: 100%;
    z-index: 25;
    background-color: rgba(255, 255, 255, 0.8);
    display: flex;
    justify-content: center;
    align-items: center;
}

.ring-1 {
    position: absolute;
    width: 50px;
    height: 50px;
    /* background-color: rgba(32,160,11,0.8); */
    border: 5px solid;
    border-color: rgba(32, 160, 11, 0.8) rgba(32, 160, 11, 0.8) transparent transparent;
    border-radius: 50%;
    animation: rotation1 1s linear infinite;
}

.ring-2 {
    position: absolute;
    width: 35px;
    height: 35px;
    border: 5px solid;
    border-color: transparent transparent rgba(101, 136, 115, 0.8) rgba(101, 136, 115, 0.8);
    border-radius: 50%;
    animation: rotation2 1.5s linear infinite;
}
@keyframes rotation1 {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
@keyframes rotation2 {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(-360deg);
    }
}
</style>

<script>
export default {
    data() {
        return {

        }
    },
    methods: {

    }
}
</script>
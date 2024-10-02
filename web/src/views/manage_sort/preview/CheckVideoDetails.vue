<!-- 查看视频详情 -->
<template>
    <div id="dplayer" ref="player"></div>
</template>

<script setup>
import { onMounted,ref,defineProps } from 'vue'
import Hls from "hls.js";
import DPlayer from "dplayer";
const props = defineProps({
    filePath: {
    type: String,
    default: "",
  },
})
let dp = null
const player = ref(null)
let videoInfo = {
    img: "https://cn.bing.com/th?id=OHR.MeotoIwa_ZH-CN3126370410_1920x1080.jpg&rf=LaDigue_1920x1080.jpg", // 视频封面
};

onMounted(() => {
    loadVideo()
})
const loadVideo = () => {
    dp = new DPlayer({
        element: player.value,
        loop: false,
        video: {
            pic: videoInfo.img, // 封面
            url: process.env.VUE_APP_BASE_URL + "/uploadFile/preview/" + props.filePath,
            type: "customHls",
            scoreenshot: true,
            customType: {
                customHls: function (video, player) {
                    let config = {
                        xhrSetup: function (xhr, url) {
                            xhr.setRequestHeader('Authorization', `Bearer ${localStorage.getItem('COLLECT_URL_TOKEN')}`)
                        },
                    }
                    const hls = new Hls(config);
                    hls.loadSource(video.src);
                    hls.attachMedia(video);
                },
            },
        },
    });

}
</script>

<style scoped>
#dplayer{
    width: 100%;
    height: 100%;
}
</style>
<template>
    <div class="container">
        <el-card class="card-box-share">
            <h3 class="title">{{ shareData.title }}</h3>
            <div class="info-box">
                <span>
                    <router-link class="router-link"
                        :to="{ name: 'userInfo', params: { userInfoId: shareData.userId } }">
                        <span class="imgDom"><img :src="shareData.photo" alt=""></span>
                        <span class="username">{{ shareData.username }}</span>
                    </router-link>
                </span>
                <el-divider direction="vertical" class="dividerVertical" />
                <span class="info text">{{ shareData.createTime }}</span>
                <el-divider direction="vertical" class="dividerVertical" />
                <span class="info text">{{ '阅读 ' + shareData.reading }}</span>
                <el-divider direction="vertical" class="dividerVertical" />
            </div>
            <div class="mainBody" id="content"></div>
        </el-card>
        <slot></slot>
    </div>
</template>

<script setup>
import Vditor from "vditor";
import "vditor/dist/index.css";
import { onMounted,defineProps,defineExpose } from 'vue';
import { useRoute } from 'vue-router'
const route = useRoute();
const props = defineProps(['shareData'])
const renderingMarkDown = () => {
    Vditor.preview(
        document.getElementById('content'), props.shareData.content,
        {
            after() {
            },
        }
    );
}
defineExpose({
    renderingMarkDown
})
onMounted(()=>{
})
</script>

<style scoped>
.container {
    margin-top: 50px;
}

.card-box-share{
    padding: 30px;
}

.title {
    font-size: 30px;
    font-weight: 600;
}

.info,
.dividerVertical {
    margin-left: 30px;
}

.info-box {
    margin-top: 20px;
}

.imgDom img {
    width: 26px;
    height: 26px;
    border-radius: 50%;
    cursor: pointer;
}

.username {
    margin-left: 15px;
    font-size: 16px;
    cursor: pointer;
}

.text {
    color: gray;
}

.mainBody {
    margin-top: 30px;
}

.router-link:hover {
    color: #5FCBFF;
}
</style>
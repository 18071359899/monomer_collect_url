<template>
    <ShareContentComponent :shareData="shareData" ref="shareContentComponentRef">
        <CommentView :shareId="route.params.shareId" :commentCount="shareData.comment" />
    </ShareContentComponent>
</template>

<script setup>
import { onMounted, ref, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import MyAxios from '@/utils/MyAxios.js'
import { useCounterStore } from '@/store/UserStore.js'
import CommentView from "./comment/CommentView.vue";
import ShareContentComponent from "@/components/ShareContentComponent.vue";
const userStore = useCounterStore()
const route = useRoute();
const shareContentComponentRef = ref(null)
const shareData = ref({
    userId: 0,
})
const getShareRequest = (shareId) => {
    return MyAxios.get("/share/detail/" + shareId + '/');
}
const getShareContent = () => {
    if (route.params.shareId) {
        getShareRequest(route.params.shareId).then(res => {
            if (res.code == undefined) {
                shareData.value = res
                nextTick(() => {
                    shareContentComponentRef.value.renderingMarkDown()
                })
            }
        })
    }
}
onMounted(() => {
    getShareContent()
})
</script>

<style scoped></style>
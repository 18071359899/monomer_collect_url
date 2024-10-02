<!-- 查看文档详情 -->
<template>
    <VuePdfEmbed annotation-layer text-layer :source="pdfSource" />
</template>

<script setup>
import { onMounted, ref, defineProps } from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import { previewFileApi } from '@/api/upload_file/preview/PreviewFile.js'
const pdfSource = ref(null)
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
const fileData = ref(null)
onMounted(() => {
    previewFileApi(props.filePath, 'arraybuffer').then(res => {
        pdfSource.value = res
    })
})

</script>
<style scoped></style>
<!-- 查看文档详情 -->
<template>
    <el-image-viewer
    :url-list="imgList"
    teleported
  /></template>

<script setup>
import { onMounted, ref, defineProps } from 'vue'
import { previewFileApi } from '@/api/upload_file/preview/PreviewFile.js'
const pdfSource = ref(null)
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
const imgList = ref([])
onMounted(() => {
    previewFileApi(props.filePath, 'arraybuffer').then(res => {  //转换成base64格式，渲染到图片预览组件上即可
        const handleResult = 'data:image/png;base64,' + btoa(
                new Uint8Array(res)
                .reduce((data, byte) => data + String.fromCharCode(byte), ''));
        imgList.value.push(handleResult)
    })
})

</script>
<style scoped></style>
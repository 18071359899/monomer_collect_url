<!-- 查看pptx文件详情
<template>
    <div id="pptx"></div>
</template>

<script setup>
import { onMounted, ref, defineProps } from 'vue'
import { previewFileApi } from '@/api/upload_file/preview/PreviewFile.js'
import $ from 'jquery'
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
// js部分 jquery已在index.html中引入 无需另外安装
const handlePPtx = (fileData) => {
  $("#pptx").pptxToHtml({ 
      pptxFileUrl: fileData, //pptx文件地址
      slidesScale: "100%", 
  });
}
onMounted(() => {
    previewFileApi(props.filePath, 'blob').then(res => {
        handlePPtx(res)
    })
})

</script>
<style scoped></style> -->
<!-- 查看文档详情 -->
<template>
    <div ref="reportContainer" class="reportContainer"></div>
</template>

<script setup>
import { onMounted, ref, defineProps,onUnmounted } from 'vue'
import { previewFileApi } from '@/api/upload_file/preview/PreviewFile.js'
import { renderAsync } from "docx-preview";
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
const reportContainer = ref(null)
onMounted(() => {
    previewFileApi(props.filePath,'blob').then(res=>{
        renderAsync(res, reportContainer.value)
    }) .catch((error) => {
          console.error(error)
        })
})
onUnmounted(()=>{
})
</script>
<style scoped>
.reportContainer {
    margin: 0px auto;
}

:deep(.docx-wrapper) {
    background-color: #fff;
    padding: 10px 0px;
}

:deep(.docx-wrapper > section.docx) {
    margin-bottom: 0px;
}
</style>
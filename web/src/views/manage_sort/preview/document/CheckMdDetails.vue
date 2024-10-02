<!-- 查看markdown文本 -->
<template>
    <pre  id="content">
        {{ fileData }}
    </pre>
</template>

<script setup>
import { onMounted, ref, defineProps } from 'vue'
import { previewFileApi } from '@/api/upload_file/preview/PreviewFile.js'
import Vditor from "vditor";
import "vditor/dist/index.css";
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
onMounted(() => {
    previewFileApi(props.filePath, 'text').then(res => {
        Vditor.preview(
        document.getElementById('content'), res,
        {
            after() {
               
            },
        }
    );
    })
})

</script>
<style scoped>
#content{
    padding: 20px;
}
</style>
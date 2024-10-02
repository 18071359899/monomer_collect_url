<!-- 查看文档详情 -->
<template>
    <div  id="text">
        {{ fileData }}
    </div>
</template>

<script setup>
import { onMounted, ref, defineProps } from 'vue'
import { previewFileApi } from '@/api/upload_file/preview/PreviewFile.js'
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
const fileData = ref(null)
onMounted(() => {
    previewFileApi(props.filePath, 'blob', function (data) {
        return new Promise((resolve) => {
            let reader = new FileReader();
            reader.readAsText(data, 'utf-8');
            reader.onload = () => {
                resolve(reader.result)
            }
        })
    }).then(res => {
        fileData.value = res
    })
})

</script>
<style scoped>
#text{
    padding: 20px;
    white-space: pre-line;
}
</style>
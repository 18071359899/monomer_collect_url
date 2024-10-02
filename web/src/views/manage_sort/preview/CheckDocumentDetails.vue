<!-- 查看文档详情 -->
<template>
    <CheckDocxDetails v-if="fileRealType === 0" :filePath="filePath" />
    <CheckElxsDetails v-else-if="fileRealType === 1" :filePath="filePath" />
    <CheckPdfDetails v-else-if="fileRealType === 2" :filePath="filePath" />
    <CheckTxtDetails v-else-if="fileRealType === 4" :filePath="filePath" />
    <CheckMdDetails v-else-if="fileRealType === 5" :filePath="filePath"/>
</template>

<script setup>
import { onMounted, ref, defineProps } from 'vue'
import CheckDocxDetails from './document/CheckDocxDetails.vue'
import CheckElxsDetails from './document/CheckElxsDetails.vue'
import CheckPdfDetails from './document/CheckPdfDetails.vue';
import CheckTxtDetails from './document/CheckTxtDetails.vue';
import CheckMdDetails from './document/CheckMdDetails.vue';
const suffixFileName = ref()  //文件后缀名称，根据它来区分预览模式
let fileRealType = ref(10)
const props = defineProps({
    filePath: {
        type: String,
        default: "",
    },
})
const typeBySuffix = () => {  //0：doc  1：excel  2：ppt  3：txt，4：md
    if (suffixFileName.value === 'doc' || suffixFileName.value === 'docx') fileRealType.value = 0
    else if (suffixFileName.value === 'xls' || suffixFileName.value === 'xlsx') fileRealType.value = 1
    else if (suffixFileName.value === 'pdf') fileRealType.value = 2
    else if (suffixFileName.value === 'ppt' || suffixFileName.value === 'pptx') fileRealType.value = 3
    else if (suffixFileName.value === 'txt' || suffixFileName.value === 'txt') fileRealType.value = 4
    else if (suffixFileName.value === 'md' || suffixFileName.value === 'md') fileRealType.value = 5
}
onMounted(() => {
    if (props.filePath) {
        suffixFileName.value = props.filePath.substring(props.filePath.lastIndexOf(".") + 1)
        typeBySuffix()
        console.log(suffixFileName.value);
    }
})

</script>
<style scoped></style>
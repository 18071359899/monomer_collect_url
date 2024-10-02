<!-- 查看文件详情对话框 -->
<template>
  <div>
    <el-dialog v-model="dialogState" :title="title" width="800" custom-class="dialog" :show-close="true" @close="handleDialogClose"
    height="100vh">
      <CheckVideoDetails v-if="fileType === 0" :filePath="filePath"/>
      <CheckDocumentDetails v-if="fileType === 2" :filePath="filePath"/>
      <CheckImageDetails v-if="fileType === 1" :filePath="filePath"/>
      <div v-if="fileType === 3 || fileType === 4" class="noPreview">
        该文件暂不能预览
      </div>
      <template #header>
        <div class="my-header">
          <h4>{{ fileName }}</h4>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref, defineProps, defineEmits, computed, onUnmounted, defineExpose, nextTick } from 'vue';
import CheckVideoDetails from './CheckVideoDetails.vue';
import CheckDocumentDetails from './CheckDocumentDetails.vue';
import CheckImageDetails from './CheckImageDetails'
const fileType = ref(0)
const filePath = ref("")
const fileName = ref("")
const dialogState = ref(false)
const handleDialogClose = () =>{
  fileType.value = -1
}
onMounted(() => {
})
const showDialog = () => {
  dialogState.value = true
}
const setFileType = (FileType) =>{
  fileType.value = FileType
}
const setFilePath = (FilePath) =>{
  filePath.value = FilePath
}
const setFileName = (FileName) =>{
  fileName.value = FileName
}
onUnmounted(() => {
  console.log("ttt");
})
defineExpose({
  showDialog,setFileType,setFilePath,setFileName
})
</script>

<style scoped>
.video-box {
  width: 100%;
  max-width: 500px;
  max-height: 500px;
}

:deep(.el-dialog__body) {
  padding: 0px;
}
.noPreview{
  text-align: center;
  font-size: 18px;
  margin-top: 10px;
}
</style>
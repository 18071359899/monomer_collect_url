<template>
    <div class="topMenu">
        <el-upload :show-file-list="false" multiple :http-request="addFile" :accept="fileAccept" style="margin-right:10px;">
          <el-button type="primary">上传文件<el-icon class="el-icon--right">
              <Upload />
            </el-icon></el-button>
        </el-upload>
        <el-button type="primary" @click="handleAddWebsite">
          上传网址<el-icon class="el-icon--right">
            <Upload />
          </el-icon>
        </el-button>
        <el-button type="success" @click="handleAddCategory" v-if="userChooseType === 'myFile'">
          新建文件夹<el-icon class="el-icon--right">
            <Folder />
          </el-icon>
        </el-button>
        <el-button type="danger" :disabled="getSelectedDocumentId <= 0" @click="$emit('deleteDocumentByIds')">
          批量删除<el-icon class="el-icon--right">
            <Delete />
          </el-icon>
        </el-button>
        <el-button type="warning" :disabled="getSelectedDocumentId <= 0" @click="$emit('HandleMoveToDirectory')">
          批量移动
        </el-button>
        <div class="inputBox">
          <el-input v-model="searchInput" class="w-50 m-2" placeholder="请输入文件夹、网址名称" :prefix-icon="Search" />
        </div>
        <el-popover trigger="click" v-model:visible="uploadListVisible" :show-arrow="false" direction="vertical"
          :width="800" popper-style="margin-left: auto;">
          <template #reference>
            <svg-icon icon-class="taskManage" style="height: 30px; width: 30px;cursor: pointer;margin-left: auto;" />
          </template>
          <UploadList ref="uploaderListRef" @unionFileSuccess="unionFileSuccess" />
        </el-popover>
      </div>
</template>

<script setup>
import { ref,defineExpose,nextTick,defineProps,defineEmits,watch,computed } from 'vue'
import UploadList from "./UploaderList.vue"
import { addUploadFileRequest } from "@/api/upload_file/upload_file/UploadFileApi";
import { ElMessage } from "element-plus";
const searchInput = computed({
  get() {
    return props.searchInput;
  },
  set(value) {
    emits("update:searchInput", value);
  },
});
const uploaderListRef = ref(null)
const uploadListVisible = ref(false)
const props = defineProps(['documentList','navigationRecord','getSelectedDocumentId','searchInput','userChooseType'])
const emits = defineEmits(['handleAddCategory','handleAddWebsite','deleteDocumentByIds','HandleMoveToDirectory',
'update:searchInput','getCurrPid'])
const addFile = (fileData) => {
  uploadListVisible.value = true
  let pid = 0
  emits('getCurrPid',function(resultPid) {
    pid = resultPid
  })
  uploaderListRef.value.addFile(fileData.file, pid)  //todo 加上pid
}
const unionFileSuccess = async (addFileData, fileUid) => {
  try {
    const addFileResult = await addUploadFileRequest(addFileData)
    if (addFileResult != null && addFileResult.code == undefined) {
      uploaderListRef.value.setUploadFileSuccess(fileUid)
      ElMessage({ type: "success", message: "文件上传成功，点击刷新按钮或刷新试试" });
    }
  } catch (error) {
    uploaderListRef.value.setUploadFileSuccess(fileUid, error)
  }
}
const handleAddWebsite = () =>{
    emits('handleAddWebsite')
}
const handleAddCategory = async () => {
  emits('handleAddCategory')
};
defineExpose({
})
</script>

<style scoped>
.inputBox {
  width: 600px;
  margin-left: 30px;
}
</style>
<template>
  <div class="documentListBox" v-if="documentList.length > 0">
    <div v-for="document in documentList" class="documentList noSelect"
      :class="document.isCut === true ? 'openness' : ''" @mouseover="changeState(document, 'hover')"
      @mouseout="changeState(document, 'none')" @click="$emit('setSelectedCount',document)"
      :style="getDocumentStyle(document.mouseState)" :key="document.id"
      @contextmenu.stop="$emit('handleElementRightClick',$event, document)">
      <el-icon class="chooseIcon" :style="getDocumentIcon(document.mouseState)">
        <Check />
      </el-icon>
      <div v-if="document.type === 0" @dblclick="$emit('handleToDirectory',document)">
        <div class="documentImg">
          <Folder />
        </div>
        <div v-if="document.isEdit === false" class="documentText">
          {{ document.name }}
        </div>
        <div v-else-if="document.isEdit === true">
          <el-input v-model="document.name" size="small" input-style="text-align:center;"
            @blur="handleBlurToUpdateDocument(document)" :ref="(el) => {
      document.documentRef = el;
    }
      "></el-input>
        </div>
      </div>
      <div v-else-if="document.type === 1" @dblclick="handleToUrl(document)">
        <div class="documentImg">
          <img :src="document.icon" @error="document.icon = '@/assets/load_file.png'" />
        </div>
        <div class="documentText">
          {{ document.title }}
        </div>
      </div>
      <div v-else-if="document.type === 2" @dblclick="$emit('handleOpenCheckFileDetailsDialog',document)">
        <div class="documentImg">
          <img :src="VUE_APP_BASE_URL + '/images/get/' + document.icon" />
        </div>
        <div v-if="document.isEdit === false" class="documentText">
          {{ document.name }}
        </div>
        <div v-else-if="document.isEdit === true">
          <el-input v-model="document.name" size="small" input-style="text-align:center;"
            @blur="handleBlurToUpdateDocument(document)" :ref="(el) => {
      document.documentRef = el;
    }
      "></el-input>
        </div>
      </div>
    </div>
  </div>
  <el-empty v-if="documentList.length <= 0" :image-size="200" style=""/>
</template>

<script setup>
import { ref, defineExpose, nextTick, defineProps, defineEmits, watch, computed } from 'vue'
import { changeState, getDocumentStyle, getDocumentIcon } from "@/js/manage_sort/GetStyleByMouseState.js"
import { addCategory, updateCategory } from "@/api/manage_url/CategoryApi";
import { resetFileName } from '@/api/upload_file/upload_file/UploadFileApi';
import { ElMessage } from "element-plus";
import Folder from "@/components/icons/FolderIcon.vue";


const VUE_APP_BASE_URL = process.env.VUE_APP_BASE_URL  //获取请求地址

const props = defineProps(['documentList'])
const emits = defineEmits(['handleElementRightClick', 'handleToDirectory',
  'toRecordContentDirectory', 'setSelectedCount','handleOpenCheckFileDetailsDialog'])
const handleToUrl = (document) => {
  window.open(document.url, "_blank");
};
const handleBlurToUpdateDocument = (document) => {
  if (document.id === undefined) {
    addCategory(document).then((res) => {
      if (res.code == undefined) {
        ElMessage({ message: "新建文件夹成功", type: "success" });
        document.id = res.id;
        document.name = res.name;
      }
    });
  } else if(document.type === 1){
    updateCategory(document).then((res) => {
      if (res.code == undefined) {
        ElMessage({ message: "文件夹重命名成功", type: "success" });
        document.name = res;
      }
    });
  }else{
    resetFileName({id: document.id,pid: document.pid,fileName: document.name}).then(res=>{
      if (res.code == undefined) {
        ElMessage({ message: "文件重命名成功", type: "success" });
        document.name = res;
      }
    })
  }

  document.isEdit = false;
};
</script>

<style scoped>
.documentListBox {
  display: flex;
  flex-wrap: wrap;
  padding: 20px;
}

.documentList {
  margin: 5px;
  cursor: pointer;
  position: relative;
  width: 130px;
  height: 120px;
  padding: 10px;
}

.documentText {
  padding-top: 5px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}


.chooseIcon {
  display: none;
  position: absolute;
  top: 8px;
  left: 8px;
  background-color: #99d5ff;
  color: #f2faff;
  border-radius: 50%;
}

.documentImg {
  padding-top: 5px;
  text-align: center;
  white-space: nowrap;
}

.documentImg img {
  box-sizing: content-box;
  padding-left: 8px;
  padding-right: 8px;
  padding-top: 6px;
  padding-bottom: 7px;
  width: 48px;
  height: 51px;
}

.openness {
  opacity: 0.5;
}
</style>
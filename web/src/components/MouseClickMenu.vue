 <!-- 鼠标右键菜单 -->
<template>
  <div class="context-menu">
    <div class="noSelected" v-if="selectState === 1">
      <div class="context-menu-item" @click="$emit('handleAddWebsite')">上传网址</div>
      <div class="context-menu-item" @click="$emit('handleAddCategory')">新建文件夹</div>
      <div class="context-menu-item" @click="$emit('getContentByPid')">刷新</div>
      <div class="context-menu-item" @click="$emit('handlePasteToDirectory')">粘贴</div>
    </div>
    <div class="selected" v-if="selectState === 2">
      <div class="context-menu-item" @click="$emit('handleToDirectory')">打开</div>
      <div class="context-menu-item" @click="$emit('handleUpdateCategoryAndWebSite')">编辑</div>
      <div class="context-menu-item" @click="$emit('handleDetailsInfo')">详细信息</div>
      <div class="context-menu-item" @click="$emit('handleDownloadFile')">下载</div>
    </div>
    <div class="selected" v-if="selectState === 2 || selectState === 3">
      <div class="context-menu-item" @click="$emit('copySaveDirectory')">复制</div>
      <div class="context-menu-item" @click="$emit('cutSaveDirectory')">剪贴</div>
      <div class="context-menu-item" @click="$emit('HandleMoveToDirectory')">移动到</div>
      <div class="context-menu-item" @click="$emit('deleteDocumentByIds')">删除</div>
    </div>
  </div>
</template>
<script setup>
import { defineProps } from "vue";
defineProps({
  selectState: {
    type: Number,
    default: 0, //1 为未选中，2为选中或鼠标右键在元素上，3为多选并且鼠标在右键在选中的元素上
  },
});
</script>
<style scoped>
.context-menu {
  position: fixed;
  color: black;
  padding: 8px;
  width: fit-content;
  background-color: rgb(255, 255, 255);
  padding: 8px 0px;
  border-radius: 4px;
  z-index: 1000;
  box-shadow: 0 0 10px 2px rgba(0, 0, 0, 0.1);
  caret-color: rgba(0, 0, 0, 0);
}
.context-menu-item {
  color: #555666;
  padding: 8px 12px;
  font-size: 14px;
  cursor: pointer;
}
.context-menu-item:hover {
  background-color: #f8f8f8;
  color: #222226;
}
</style>

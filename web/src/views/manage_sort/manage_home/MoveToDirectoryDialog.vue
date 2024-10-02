<!-- 移动到某个目录对话框 -->
<template>
  <el-dialog
    custom-class="directory-dialog"
    v-model="dialogState"
    :title="title"
    width="500"
    :close-on-click-modal="false"
  >
    <DirectoryView @clickOnNode="handleClickOnNode" />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogState = false">关闭</el-button>
        <el-button type="primary" @click="submitToMove"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { defineProps, computed, defineEmits, ref, defineExpose } from "vue";
import MyAxios from "@/utils/MyAxios";
import { ElMessage } from "element-plus";
import DirectoryView from "@/components/DirectoryView.vue";
import { cutDirectory } from "@/api/manage_url/DirectoryApi";
const emit = defineEmits(["update:dialogState", "handleCutDirectory"]);
const target = ref({}); // 目标目录
const handleClickOnNode = (node) => {
  target.value = node;
};
const dialogState = computed({
  get() {
    return props.dialogState;
  },
  set(value) {
    emit("update:dialogState", value);
  },
});
const props = defineProps({
  dialogState: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: "新增网址",
  },
  chooseList: {
    type: Array,
    default: () => [],
  },
});

const submitToMove = () => {
  emit("handleCutDirectory", "tree",target.value.id);
};
</script>

<style scoped></style>

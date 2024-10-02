<!-- 管理网址对话框 -->
<template>
  <el-dialog
    v-model="dialogState"
    :title="title"
    width="800"
    :close-on-click-modal="false"
  >
    <el-form ref="form" label-width="auto" label-position="top">
      <el-form-item label="标题">
        <el-input v-model="DataForm.title" placeholder="可选填(不填自动获取)" />
      </el-form-item>
      <el-form-item label="链接">
        <el-input v-model="DataForm.url" placeholder="请输入网址链接" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input
          v-model="DataForm.description"
          :rows="4"
          placeholder="请输入描述"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogState = false">关闭</el-button>
        <el-button type="primary" @click="submitUpdateWebsite"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { defineProps, computed, defineEmits, ref, defineExpose } from "vue";
import MyAxios from "@/utils/MyAxios";
import { ElMessage } from "element-plus";
const props = defineProps({
  dialogState: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: "新增网址",
  },
});
const DataForm = ref({
  url: "",
  title: "",
  describe: "",
  pid: 0,
});
const getDataForm = (id) => {
  MyAxios.get("/website/get/" + id).then((res) => {
    if(res.code == undefined){
      DataForm.value = res;
    }
  });
};
const setDataFormPid = (pid) => {
  DataForm.value.pid = pid;
};
const submitUpdateWebsite = () => {
  if (props.title === "新增网址") {
    MyAxios.post("/website/add/", DataForm.value).then((res) => {
      if (res.code == undefined) {
        ElMessage({ message: "添加成功", type: "success" });
        emit("updateWebsiteSuccess");
        dialogState.value = false;
      }
    });
  }else {
    MyAxios.post("/website/update/", DataForm.value).then((res) => {
      if (res.code == undefined) {
        ElMessage({ message: "修改成功", type: "success" });
        emit("updateWebsiteSuccess");
        dialogState.value = false;
      }
    });
  }
};
defineExpose({
  getDataForm,
  setDataFormPid,
});
const emit = defineEmits(["update:dialogState", "updateWebsiteSuccess"]);
const dialogState = computed({
  get() {
    return props.dialogState;
  },
  set(value) {
    emit("update:dialogState", value);
  },
});
</script>
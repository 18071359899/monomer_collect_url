<!-- 管理网址对话框 -->
<template>
    <el-dialog
      v-model="dialogState"
      :title="title"
      width="800"
      :close-on-click-modal="false"
    >
      <el-form ref="form" label-width="auto" label-position="left">
        <div v-if="DataForm.type === 1">
            <el-form-item label="标题">
                <span>{{  DataForm.title}}</span>
            </el-form-item>
            <el-form-item label="链接">
                  <span>{{  DataForm.url}}</span>
            </el-form-item>
            <el-form-item label="描述">
              <span>{{  DataForm.description}}</span>
            </el-form-item>
        </div>
        <div v-else-if="DataForm.type === 0">
            <el-form-item label="名称">
                <span>{{  DataForm.name}}</span>
            </el-form-item>
        </div>
        <div v-else-if="DataForm.type === 2">
          <el-form-item label="文件名称">
            <span>{{  DataForm.name}}</span>
        </el-form-item>
        </div>
        <el-form-item label="位置">
            <span>{{  DataForm.position}}</span>
          </el-form-item>
        <el-form-item label="更新时间">
            <span>{{  DataForm.updateTime}}</span>
          </el-form-item>
      </el-form>
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
      default: "详细信息",
    },
  });
  const DataForm = ref({
    url: "",
    title: "",
    describe: "",
    pid: 0,
  });
  const getDataForm = (type,id) => {
    MyAxios.get("/directory/details/" + type + "/" + id).then((res) => {
      if(res.code == undefined){
        DataForm.value = res;
        if(DataForm.value.description == null || DataForm.value.description == ""){
            DataForm.value.description = "无"
        }
      }
    });
  };
  const setDataFormPid = (pid) => {
    DataForm.value.pid = pid;
  };
  defineExpose({
    getDataForm,
  });
  const emit = defineEmits(["update:dialogState"]);
  const dialogState = computed({
    get() {
      return props.dialogState;
    },
    set(value) {
      emit("update:dialogState", value);
    },
  });
  </script>

<style scoped>
span{
    margin-left: 30px;
}
</style>
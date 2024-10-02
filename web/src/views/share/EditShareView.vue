<!-- 添加或编辑分享页面 -->
<template>
  <div class="container">
    <el-card>
      <div class="shareDescribe setPadding">
        分享文章
      </div>
      <el-divider style="margin-top:-10px;" border-style="double" />
      <div class="content setPadding">
        <el-form :model="shareData" label-position="left">
          <el-form-item label="标题：">
            <el-input placeholder="请输入文章标题" v-model="shareData.title"></el-input>
          </el-form-item>
          <el-form-item label="内容：" style="margin-top:50px;">
            <div id="vditorContent"></div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit" style="margin-left:60px;width:200px;">提交</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>

</template>
<script setup>
import { nextTick, onMounted, ref } from 'vue'
import MyAxios from '@/utils/MyAxios.js'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { ElMessage } from "element-plus";
import { useRoute } from 'vue-router';
const route = useRoute()
import router from '@/router';
const shareData = ref({
  title: "",
  content: "",
})
const sumbitShareRequest = (share) => {
  if (share.id != null) {
    return MyAxios.post('/share/update/', share)
  } else {
    return MyAxios.post('/share/add/', share)
  }

}
const onSubmit = () => {
  shareData.value.content = contentEditor.getValue()
  sumbitShareRequest(shareData.value).then(res => {
    if (res.code == undefined) {
      if (!shareData.value.id) shareData.value.id = res
      ElMessage({ type: "success", message: "提交成功" });
      router.push({ name: 'showShare',params:{ shareId: shareData.value.id } })
    }
  })
}
const getShareContent = (shareId) => {
  return MyAxios.get('/share/' + shareId + '/');
}
let contentEditor = null
onMounted(() => {
  let token = localStorage.getItem('COLLECT_URL_TOKEN')
  contentEditor = new Vditor('vditorContent', {
    width: '100%',
    height: 'auto',
    minHeight: 400,
    placeholder: '请输入文章内容',
    toolbarConfig: {
      pin: true,
    },
    cache: {
      enable: false,
    },
    upload: {
      accept: 'image/*',
      fieldName: 'file',
      headers:{
        Authorization: `Bearer ${token}`
      },
      url: process.env.VUE_APP_BASE_URL+'/file/uploadFile',
      linkToImgUrl: process.env.VUE_APP_BASE_URL+'/file/uploadFile',
      success(editor,msg){
        contentEditor.insertValue(`![}](${msg})`)
      },
    },
    after: () => {
      if (route.params.shareId) {
        getShareContent(route.params.shareId).then(res => {
          if(res.code == undefined){
          shareData.value = res
          contentEditor.setValue(shareData.value.content)
          }
        })
      }
    },
  });

}
)
</script>
<style scoped>
.container {
  margin-top: 30px;
}

.setPadding {
  padding: 30px;
}

.shareDescribe {
  font-size: 20px;
}
</style>